package kz.uco.tsadv.service;

import com.haulmont.addon.bproc.entity.HistoricVariableInstanceData;
import com.haulmont.addon.bproc.entity.ProcessInstanceData;
import com.haulmont.addon.bproc.service.BprocHistoricService;
import com.haulmont.addon.bproc.service.BprocRuntimeService;
import com.haulmont.addon.bproc.service.BprocTaskService;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.entity.contracts.Id;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.security.entity.User;
import kz.uco.base.notification.NotificationSenderAPI;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.bproc.beans.BprocUserListProvider;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.entity.bproc.ExtTaskData;
import kz.uco.tsadv.modules.administration.UserExt;
import kz.uco.tsadv.modules.personal.dictionary.DicAbsenceType;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.modules.personal.model.AbsenceRequest;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.uactivity.entity.ActivityType;
import kz.uco.uactivity.entity.StatusEnum;
import kz.uco.uactivity.entity.WindowProperty;
import kz.uco.uactivity.service.ActivityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;

@Service(BprocService.NAME)
public class BprocServiceBean implements BprocService {

    @Inject
    protected CommonService commonService;
    @Inject
    protected Persistence persistence;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected ActivityService activityService;
    @Inject
    protected UserSessionSource userSessionSource;
    @Inject
    protected NotificationSenderAPI notificationSender;
    @Inject
    protected BprocHistoricService bprocHistoricService;
    @Inject
    protected Metadata metadata;
    @Inject
    protected Resources resources;
    @Inject
    protected BprocUserListProvider bprocUserListProvider;

    @Override
    public List<User> getTaskCandidates(String executionId) {
        return bprocUserListProvider.get(executionId);
    }

    @Override
    @Transactional
    public <T extends AbstractBprocRequest> void start(T entity) {
        changeRequestStatus(entity, "APPROVING");
    }

    @Override
    @Transactional
    public <T extends AbstractBprocRequest> void reject(T entity) {
        changeRequestStatus(entity, "REJECT");
        sendNotificationToInitiator(entity);
    }

    @Override
    public <T extends AbstractBprocRequest> void sendNotificationToInitiator(T bprocRequest) {
        sendNotificationToInitiator(bprocRequest, null);
    }

    @Override
    public <T extends AbstractBprocRequest> void sendNotificationToInitiator(T bprocRequest, String notificationTemplateCode) {
        ProcessInstanceData processInstanceData = bprocHistoricService.createHistoricProcessInstanceDataQuery()
                .processInstanceBusinessKey(bprocRequest.getId().toString())
                .processDefinitionKey(bprocRequest.getProcessDefinitionKey())
                .singleResult();

        User initiator = getProcessVariable(processInstanceData.getId(), "initiator");

        if (notificationTemplateCode == null)
            notificationTemplateCode = getProcessVariable(processInstanceData.getId(), "initiatorNotificationTemplateCode");

        ActivityType activityType = dataManager.load(ActivityType.class)
                .query("select e from uactivity$ActivityType e where e.code = :code")
                .parameter("code", "NOTIFICATION")
                .view(new View(ActivityType.class)
                        .addProperty("code")
                        .addProperty("windowProperty",
                                new View(WindowProperty.class).addProperty("entityName").addProperty("screenName")))
                .one();

        sendNotificationAndActivity(bprocRequest, initiator, activityType, notificationTemplateCode);
    }

    @SuppressWarnings("unchecked")
    public <T> T getProcessVariable(String processInstanceDataId, String variableName) {
        return (T) bprocHistoricService
                .createHistoricVariableInstanceDataQuery()
                .processInstanceId(processInstanceDataId)
                .variableName(variableName)
                .singleResult()
                .getValue();
    }

    @Override
    @Transactional
    public <T extends AbstractBprocRequest> void changeRequestStatus(T entity, String code) {
        EntityManager entityManager = persistence.getEntityManager();
        @SuppressWarnings("unchecked") Class<T> entityClass = (Class<T>) entity.getClass();
        T bprocRequest = entityManager.find(entityClass, entity.getId(), new View(entityClass).addProperty("status"));
        Assert.notNull(bprocRequest, "bprocRequest not found!");
        bprocRequest.setStatus(commonService.getEntity(DicRequestStatus.class, code));
        entityManager.merge(bprocRequest);
    }

    @Override
    public <T extends AbstractBprocRequest> boolean hasActor(T entity, String taskCode) {
        return !getActors(entity.getId(), taskCode).isEmpty();
    }

    @Override
    public ProcessInstanceData getProcessInstanceData(String processInstanceBusinessKey, String processDefinitionKey) {
        return bprocHistoricService.createHistoricProcessInstanceDataQuery()
                .processInstanceBusinessKey(processInstanceBusinessKey)
                .processDefinitionKey(processDefinitionKey)
                .singleResult();
    }

    @Override
    public List<ExtTaskData> getProcessTasks(ProcessInstanceData processInstanceData) {
        List<ExtTaskData> tasks = new ArrayList<>();

        ExtTaskData initiatorTask = initInitiatorTask(processInstanceData);
        if (initiatorTask != null) tasks.add(initiatorTask);

        bprocHistoricService
                .createHistoricTaskDataQuery()
                .processInstanceId(processInstanceData.getId())
                .orderByHistoricTaskInstanceStartTime().asc()
                .list()
                .stream()
                .map(taskData -> (ExtTaskData) taskData)
                .peek(taskData -> {
                    if (taskData.getEndTime() == null)
                        taskData.setAssigneeOrCandidates(getTaskCandidates(taskData.getExecutionId()));
                    else if (taskData.getAssignee() == null) {
                        User user = dataManager.load(Id.of(UUID.fromString(taskData.getAssignee()), User.class)).view(View.BASE).one();
                        taskData.setAssigneeOrCandidates(Collections.singletonList(user));
                    }
                })
                .forEach(tasks::add);

        return tasks;
    }

    protected ExtTaskData initInitiatorTask(ProcessInstanceData processInstanceData) {
        if (processInstanceData != null && processInstanceData.getStartTime() != null) {

            List<HistoricVariableInstanceData> initiatorVariableList = this.bprocHistoricService.createHistoricVariableInstanceDataQuery()
                    .processInstanceId(processInstanceData.getId())
                    .variableName("initiator")
                    .list();

            if (!CollectionUtils.isEmpty(initiatorVariableList)) {
                UserExt initiator = (UserExt) initiatorVariableList.get(0).getValue();

                ExtTaskData initiatorTask = metadata.create(ExtTaskData.class);
                initiatorTask.setId(UUID.randomUUID().toString());
                initiatorTask.setName("Initiator");
                initiatorTask.setTaskDefinitionKey("initiator");
                initiatorTask.setAssignee(initiator.getId().toString());
                initiatorTask.setAssigneeOrCandidates(Collections.singletonList(initiator));
                initiatorTask.setCreateTime(processInstanceData.getStartTime());
                initiatorTask.setEndTime(processInstanceData.getStartTime());
                return initiatorTask;
            }
        }
        return null;
    }

    @Override
    public <T extends AbstractBprocRequest> void sendNotificationAndActivity(T entity, User user, ActivityType activityType, String notificationTemplateCode) {

        User sessionUser = userSessionSource.getUserSession().getUser();

        Map<String, Object> notificationParams = getNotificationParams(notificationTemplateCode, entity);
        activityService.createActivity(
                user,
                sessionUser,
                activityType,
                StatusEnum.active,
                "description",
                null,
                new Date(),
                null,
                null,
                entity.getId(),
                notificationTemplateCode,
                notificationParams);

        notificationSender.sendParametrizedNotification(notificationTemplateCode, (UserExt) user, notificationParams);
    }

    @Override
    public List<? extends User> getActors(UUID bprocRequestId, String bprocUserTaskCode) {
        return dataManager.load(User.class)
                .query("select e.user from tsadv_BprocActors e where e.entityId = :entityId and e.bprocUserTaskCode = :bprocUserTaskCode ")
                .setParameters(ParamsMap.of("entityId", bprocRequestId, "bprocUserTaskCode", bprocUserTaskCode))
                .list();
    }

    @Override
    public void approveAbsence(AbsenceRequest absenceRequest) {
        changeRequestStatus(absenceRequest, "APPROVED");
        sendNotificationToInitiator(absenceRequest);
    }

    protected <T extends AbstractBprocRequest> Map<String, Object> getNotificationParams(String templateCode, T entity) {
        Map<String, Object> params = new HashMap<>();
        params.put("item", entity);
        params.put("entity", entity);

        switch (templateCode) {
            case "bpm.absenceRequest.initiator.notification":
            case "bpm.absenceRequest.approver.notification": {
                AbsenceRequest absenceRequest = (AbsenceRequest) dataManager.reload(entity, "absenceRequest.view");

                params.putIfAbsent("item", absenceRequest);
                params.putIfAbsent("tableRu", createTableAbsence(absenceRequest, "Ru"));
                params.putIfAbsent("tableEn", createTableAbsence(absenceRequest, "En"));
                break;
            }
        }

        return params;
    }

    protected String createTableAbsence(AbsenceRequest absenceRequest, String lang) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        boolean isRussian = !lang.equals("en");

        PersonExt person = commonService.getEntity(PersonExt.class,
                "select p from base$AssignmentExt e " +
                        "   join e.personGroup.list p " +
                        " where e.group.id = :groupId" +
                        "   and current_date between e.startDate and e.endDate" +
                        "   and current_date between p.startDate and p.endDate" +
                        "   and e.primaryFlag = 'TRUE'  ",
                ParamsMap.of("groupId", absenceRequest.getAssignmentGroup().getId()),
                View.LOCAL);

        DicAbsenceType type = absenceRequest.getType();

        Map<String, Object> params = new HashMap<>();
        params.put("employeeFullName", person.getFullNameLatin(lang.toLowerCase()));
        params.put("absenceType", isRussian ? type.getLangValue1() : type.getLangValue3());
        params.put("dateFrom", dateFormat.format(absenceRequest.getDateFrom()));
        params.put("dateTo", dateFormat.format(absenceRequest.getDateTo()));
        params.put("days", absenceRequest.getAbsenceDays());
        params.put("comment", absenceRequest.getComment());

        String templateContents = resources.getResourceAsString(String.format("classpath:kz/uco/tsadv/templates/absenceRequest/AbsenceRequestTable%s.html", lang));
        Assert.notNull(templateContents, "templateContents not found!");
        return TemplateHelper.processTemplate(templateContents, params);
    }
}