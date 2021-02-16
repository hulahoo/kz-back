package kz.uco.tsadv.service;

import com.haulmont.addon.bproc.data.Outcome;
import com.haulmont.addon.bproc.data.OutcomesContainer;
import com.haulmont.addon.bproc.entity.HistoricVariableInstanceData;
import com.haulmont.addon.bproc.entity.ProcessDefinitionData;
import com.haulmont.addon.bproc.entity.ProcessInstanceData;
import com.haulmont.addon.bproc.form.FormData;
import com.haulmont.addon.bproc.service.BprocFormService;
import com.haulmont.addon.bproc.service.BprocHistoricService;
import com.haulmont.addon.bproc.service.BprocRepositoryService;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.entity.contracts.Id;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.security.entity.User;
import kz.uco.base.notification.NotificationSenderAPI;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.bproc.beans.BprocUserListProvider;
import kz.uco.tsadv.bproc.beans.helper.AbstractBprocHelper;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.entity.bproc.ExtTaskData;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.modules.bpm.BpmRolesLink;
import kz.uco.tsadv.modules.personal.dictionary.DicAbsenceType;
import kz.uco.tsadv.modules.personal.dictionary.DicHrRole;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.modules.personal.model.Absence;
import kz.uco.tsadv.modules.personal.model.AbsenceForRecall;
import kz.uco.tsadv.modules.personal.model.AbsenceRequest;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.uactivity.entity.Activity;
import kz.uco.uactivity.entity.ActivityType;
import kz.uco.uactivity.entity.StatusEnum;
import kz.uco.uactivity.entity.WindowProperty;
import kz.uco.uactivity.service.ActivityService;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.ProcessEngines;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;

@Service(BprocService.NAME)
public class BprocServiceBean extends AbstractBprocHelper implements BprocService {

    @Inject
    protected CommonService commonService;
    @Inject
    protected Persistence persistence;
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
    protected Messages messages;
    @Inject
    protected Resources resources;
    @Inject
    protected BprocUserListProvider bprocUserListProvider;

    protected String templateFolder = "classpath:kz/uco/tsadv/templates/";
    @Inject
    protected GlobalConfig globalConfig;
    @Inject
    protected BprocRepositoryService bprocRepositoryService;
    @Inject
    protected BprocFormService bprocFormService;
    @Inject
    protected DatesService datesService;

    @Override
    public List<? extends User> getTaskCandidates(String executionId, String viewName) {
        List<User> users = bprocUserListProvider.get(executionId);
        return dataManager.load(TsadvUser.class)
                .query("select e from tsadv$UserExt e where e.id in :users")
                .setParameters(ParamsMap.of("users", users))
                .view(viewName != null ? viewName : View.MINIMAL)
                .list();
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
    public <T extends AbstractBprocRequest> void sendNotificationAndActivityToInitiator(T bprocRequest, String notificationTemplateCode) {
        ProcessInstanceData processInstanceData =
                this.getProcessInstanceData(bprocRequest.getId().toString(), bprocRequest.getProcessDefinitionKey());

        User initiator = getProcessVariable(processInstanceData.getId(), "initiator");

        this.sendNotificationAndActivity(bprocRequest, initiator, getActivityFromEntity(bprocRequest), notificationTemplateCode);
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

        if (notificationTemplateCode == null)
            notificationTemplateCode = getProcessVariable(processInstanceData.getId(), "initiatorNotificationTemplateCode");

        if (StringUtils.isBlank(notificationTemplateCode)) return;

        User initiator = getProcessVariable(processInstanceData.getId(), "initiator");

        ActivityType activityType = dataManager.load(ActivityType.class)
                .query("select e from uactivity$ActivityType e where e.code = :code")
                .parameter("code", "NOTIFICATION")
                .view(new View(ActivityType.class)
                        .addProperty("code")
                        .addProperty("windowProperty",
                                new View(WindowProperty.class).addProperty("entityName").addProperty("screenName")))
                .one();

        assert initiator != null;
        sendNotificationAndActivity(bprocRequest, dataManager.reload(initiator, "user-fioWithLogin"), activityType, notificationTemplateCode);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T getProcessVariable(String processInstanceDataId, String variableName) {
        return (T) Optional.ofNullable(
                bprocHistoricService
                        .createHistoricVariableInstanceDataQuery()
                        .processInstanceId(processInstanceDataId)
                        .variableName(variableName)
                        .singleResult()
        )
                .map(HistoricVariableInstanceData::getValue)
                .orElse(null);
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
        return !getActors(entity.getId(), taskCode, View.MINIMAL).isEmpty();
    }

    @Override
    public ProcessInstanceData getProcessInstanceData(String processInstanceBusinessKey, String processDefinitionKey) {
        return bprocHistoricService.createHistoricProcessInstanceDataQuery()
                .processInstanceBusinessKey(processInstanceBusinessKey)
                .processDefinitionKey(processDefinitionKey)
                .singleResult();
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Override
    public ExtTaskData getActiveTask(ProcessInstanceData processInstanceData) {
        return getProcessTasks(processInstanceData)
                .stream()
                .filter(taskData -> taskData.getEndTime() == null)
                .filter(taskData -> taskData.getAssigneeOrCandidates() != null)
                .filter(taskData -> taskData.getAssigneeOrCandidates().contains(userSessionSource.getUserSession().getCurrentOrSubstitutedUser()))
                .findAny()
                .orElse(null);
    }

    @Override
    public List<ExtTaskData> getProcessTasks(ProcessInstanceData processInstanceData) {
        List<ExtTaskData> tasks = new ArrayList<>();

        ExtTaskData initiatorTask = initInitiatorTask(processInstanceData);
        if (initiatorTask != null) tasks.add(initiatorTask);

        List<BpmRolesLink> rolesLinks = getProcessVariable(processInstanceData.getId(), "rolesLinks");
        List<BpmRolesLink> links = rolesLinks == null
                ? null
                : dataManager.load(BpmRolesLink.class)
                .query("select e from tsadv$BpmRolesLink e where e.id in :links ")
                .setParameters(ParamsMap.of("links", rolesLinks))
                .view(new View(BpmRolesLink.class)
                        .addProperty("bprocUserTaskCode")
                        .addProperty("hrRole", new View(DicHrRole.class)
                                .addProperty("langValue")))
                .list();

        bprocHistoricService
                .createHistoricTaskDataQuery()
                .processInstanceId(processInstanceData.getId())
                .orderByHistoricTaskInstanceStartTime().asc()
                .list()
                .stream()
                .map(taskData -> (ExtTaskData) taskData)
                .peek(taskData -> {
                    if (taskData.getEndTime() == null && taskData.getAssignee() == null) {
                        @SuppressWarnings("unchecked") List<TsadvUser> taskCandidates = (List<TsadvUser>) getTaskCandidates(taskData.getExecutionId(), "user-fioWithLogin");
                        taskData.setAssigneeOrCandidates(taskCandidates);
                    } else if (taskData.getAssignee() != null) {
                        TsadvUser user = dataManager.load(Id.of(UUID.fromString(taskData.getAssignee()), TsadvUser.class)).view("user-fioWithLogin").one();
                        taskData.setAssigneeOrCandidates(Collections.singletonList(user));
                    }
                })
                .peek(taskData -> {
                    if (taskData.getEndTime() != null) {
                        OutcomesContainer outcomesContainer = getProcessVariable(processInstanceData.getId(), taskData.getTaskDefinitionKey() + "_result");
                        if (outcomesContainer != null && !CollectionUtils.isEmpty(outcomesContainer.getOutcomes()))
                            for (Outcome outcome : outcomesContainer.getOutcomes()) {
                                if (Objects.equals(outcome.getUser(), taskData.getAssignee())
                                        && Math.abs(outcome.getDate().getTime() - taskData.getEndTime().getTime()) < 2000) {
                                    taskData.setOutcome(outcome.getOutcomeId());
                                    taskData.setComment(getProcessVariable(processInstanceData.getId(), ExtTaskData.getUniqueCommentKey(outcome)));
                                    break;
                                }
                            }
                    }
                })
                .peek(taskData -> {
                    if (taskData.getTaskDefinitionKey().equals("initiator_task"))
                        taskData.setHrRole(getInitiatorHrRole());
                    if (links != null)
                        links.stream()
                                .filter(bpmRolesLink -> bpmRolesLink.getBprocUserTaskCode().equals(taskData.getTaskDefinitionKey()))
                                .findAny()
                                .map(BpmRolesLink::getHrRole)
                                .ifPresent(taskData::setHrRole);
                })
                .forEach(tasks::add);

        return tasks;
    }

    @Override
    public ProcessDefinitionData getProcessDefinitionData(String processDefinitionKey) {
        return bprocRepositoryService
                .createProcessDefinitionDataQuery()
                .processDefinitionKey(processDefinitionKey)
                .active()
                .latestVersion()
                .singleResult();
    }

    @Override
    public FormData getStartFormData(String processDefinitionKey) {
        return bprocFormService.getStartFormData(getProcessDefinitionData(processDefinitionKey).getId());
    }

    protected ExtTaskData initInitiatorTask(ProcessInstanceData processInstanceData) {
        if (processInstanceData != null && processInstanceData.getStartTime() != null) {

            List<HistoricVariableInstanceData> initiatorVariableList = this.bprocHistoricService.createHistoricVariableInstanceDataQuery()
                    .processInstanceId(processInstanceData.getId())
                    .variableName("initiator")
                    .list();

            if (!CollectionUtils.isEmpty(initiatorVariableList)) {
                TsadvUser initiator = dataManager.reload((TsadvUser) initiatorVariableList.get(0).getValue(), "user-fioWithLogin");

                ExtTaskData initiatorTask = metadata.create(ExtTaskData.class);
                initiatorTask.setId(UUID.randomUUID().toString());
                initiatorTask.setName("Initiator");
                initiatorTask.setTaskDefinitionKey("initiator");
                initiatorTask.setAssignee(initiator.getId().toString());
                initiatorTask.setAssigneeOrCandidates(Collections.singletonList(initiator));
                initiatorTask.setCreateTime(processInstanceData.getStartTime());
                initiatorTask.setEndTime(processInstanceData.getStartTime());
                initiatorTask.setHrRole(getInitiatorHrRole());
                initiatorTask.setOutcome("START");
                return initiatorTask;
            }
        }
        return null;
    }

    protected DicHrRole getInitiatorHrRole() {
        DicHrRole dicHrRole = metadata.create(DicHrRole.class);
        dicHrRole.setLangValue1("Инициатор");
        dicHrRole.setLangValue3("Initiator");
        return dicHrRole;
    }

    @Override
    public <T extends AbstractBprocRequest> void sendNotificationAndActivity(T entity, User user, ActivityType activityType, String notificationTemplateCode) {

        User sessionUser = userSessionSource.getUserSession().getUser();

        Map<String, Object> notificationParams = getNotificationParams(notificationTemplateCode, entity);

        if (!PersistenceHelper.isLoadedWithView(user, "user-fioWithLogin"))
            user = dataManager.reload(user, "user-fioWithLogin");

        notificationParams.put("userRu", ((TsadvUser) user).getFullNameWithLogin(Locale.forLanguageTag("ru")));
        notificationParams.put("userEn", ((TsadvUser) user).getFullNameWithLogin(Locale.forLanguageTag("en")));

        notificationParams.put("requestLinkRu", "");
        notificationParams.put("requestLinkEn", "");

        Activity activity = activityService.createActivity(
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

        String requestLink = getRequestLink(entity, activity);
        notificationParams.put("requestLinkRu", String.format(requestLink, "Открыть заявку " + entity.getRequestNumber()));
        notificationParams.put("requestLinkEn", String.format(requestLink, "Open request " + entity.getRequestNumber()));

        notificationSender.sendParametrizedNotification(notificationTemplateCode, (TsadvUser) user, notificationParams);
    }

    protected <T extends AbstractBprocRequest> String getRequestLink(T entity, Activity activity) {
        if (!"NOTIFICATION".equals(activity.getType().getCode())) {
            ActivityType activityType = activity.getType();
            if (activityType.getWindowProperty() != null) {
                return "<a href=\"" + globalConfig.getWebAppUrl() + "/open?screen=" + activity.getType().getWindowProperty().getScreenName() +
                        "&item=" + activityType.getWindowProperty().getEntityName() + "-" + activity.getReferenceId() +
                        "&params=activityId:" + activity.getId() +
                        "\" target=\"_blank\">%s " + "</a>";
            }
        }

        String entityName = entity.getMetaClass().getName();
        WindowProperty windowProperty = dataManager.load(WindowProperty.class)
                .query("select e from uactivity$WindowProperty e where e.entityName = :entityName")
                .setParameters(ParamsMap.of("entityName", entityName))
                .one();

        return "<a href=\"" + globalConfig.getWebAppUrl() + "/open?screen=" + windowProperty.getScreenName() +
                "&item=" + entityName + "-" + entity.getId() +
                "\" target=\"_blank\">%s " + "</a>";
    }

    @Override
    public List<? extends User> getActors(UUID bprocRequestId, String bprocUserTaskCode, String viewName) {
        return dataManager.load(User.class)
                .query("select e.user from tsadv_BprocActors e where e.entityId = :entityId and e.bprocUserTaskCode = :bprocUserTaskCode ")
                .setParameters(ParamsMap.of("entityId", bprocRequestId, "bprocUserTaskCode", bprocUserTaskCode))
                .view(viewName != null ? viewName : View.MINIMAL)
                .list();
    }

    @Override
    public void approveAbsence(AbsenceRequest absenceRequest) {
        changeRequestStatus(absenceRequest, "APPROVED");
        sendNotificationToInitiator(absenceRequest);
    }

    @Override
    public Map<String, String> getActivityIdMap(String processDefinitionKey) {
        BpmnModel bpmnModel = ProcessEngines.getDefaultProcessEngine().getRepositoryService()
                .getBpmnModel(processDefinitionKey);
        Map<String, String> activityIdMap = new HashMap<>();
        bpmnModel.getProcesses().forEach(process -> process.getFlowElements().stream()
                .filter(flowElement -> flowElement instanceof UserTask)
                .forEach(flowElement -> activityIdMap.put(flowElement.getName(), flowElement.getId())));
        return activityIdMap;
    }

    protected <T extends AbstractBprocRequest> Map<String, Object> getNotificationParams(String templateCode, T entity) {
        Map<String, Object> params = new HashMap<>();
        params.put("item", entity);
        params.put("entity", entity);
        params.put("requestNumber", entity.getRequestNumber());

        ProcessInstanceData processInstanceData = getProcessInstanceData(entity.getId().toString(), entity.getProcessDefinitionKey());
        TsadvUser initiator = getProcessVariable(processInstanceData.getId(), "initiator");
        Assert.notNull(initiator, "Initiator not found!");
        initiator = dataManager.reload(initiator, "user-fioWithLogin");
        params.put("initiatorRu", initiator.getFullNameWithLogin(Locale.forLanguageTag("ru")));
        params.put("initiatorEn", initiator.getFullNameWithLogin(Locale.forLanguageTag("en")));

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

        params.put("approversTableRu", getApproversTable("Ru", processInstanceData));
        params.put("approversTableEn", getApproversTable("En", processInstanceData));
        params.put("comment", getProcessVariable(processInstanceData.getId(), "comment"));

        return params;
    }

    protected String getApproversTable(String lang, ProcessInstanceData processInstanceData) {
        Locale locale = Locale.forLanguageTag(lang.toLowerCase());

        String table = resources.getResourceAsString(String.format(templateFolder + "approversTable/ApproversTable.html", lang));
        String tableTr = resources.getResourceAsString(String.format(templateFolder + "approversTable/ApproversTableTr.html", lang));

        assert tableTr != null;
        assert table != null;

        StringBuilder builder = new StringBuilder();

        MessageTools tools = messages.getTools();
        MetaClass taskMetaClass = metadata.getClassNN(ExtTaskData.class);

        Map<String, Object> params = new HashMap<>();
        params.put("class", "class=\"tableHeader\"");
        params.put("role", tools.getPropertyCaption(taskMetaClass, "hrRole", locale));
        params.put("user", tools.getPropertyCaption(taskMetaClass, "assignee", locale));
        params.put("createTime", tools.getPropertyCaption(taskMetaClass, "createTime", locale));
        params.put("endTime", tools.getPropertyCaption(taskMetaClass, "endTime", locale));
        params.put("outcome", tools.getPropertyCaption(taskMetaClass, "outcome", locale));
        params.put("comment", tools.getPropertyCaption(taskMetaClass, "comment", locale));

        builder.append(TemplateHelper.processTemplate(tableTr, params));

        SimpleDateFormat dateTimeFormat = new SimpleDateFormat(messages.getMainMessage("dateTimeFormat", locale));

        boolean isColor = false;
        List<ExtTaskData> processTasks = getProcessTasks(processInstanceData);
        for (ExtTaskData processTask : processTasks) {
            params.clear();
            params.put("class", isColor ? "class=\"color\"" : "");
            params.put("role", processTask.getHrRole().getLangValue());
            params.put("user", getCreateUsersPopup(locale, processTask));
            params.put("createTime", dateTimeFormat.format(processTask.getCreateTime()));
            params.put("endTime", processTask.getEndTime() != null ? dateTimeFormat.format(processTask.getEndTime()) : "");
            params.put("outcome", processTask.getOutcome() != null ? messages.getMainMessage("OUTCOME_" + processTask.getOutcome(), locale) : "");
            params.put("comment", processTask.getComment());

            builder.append("\n").append(TemplateHelper.processTemplate(tableTr, params));

            isColor = !isColor;
        }

        return TemplateHelper.processTemplate(table, ParamsMap.of("trs", builder.toString()));
    }

    protected String getCreateUsersPopup(Locale locale, ExtTaskData processTask) {
        List<TsadvUser> assigneeOrCandidates = processTask.getAssigneeOrCandidates();
        if (assigneeOrCandidates == null || assigneeOrCandidates.size() <= 1) {
            if (assigneeOrCandidates == null || assigneeOrCandidates.isEmpty()) return "";
            return assigneeOrCandidates.get(0).getFullNameWithLogin(locale);
        }
        String popup = resources.getResourceAsString(templateFolder + "approversTable/Popup.html");
        assert popup != null;
        StringBuilder builder = new StringBuilder();
        builder.append("<table>");
        for (TsadvUser assigneeOrCandidate : assigneeOrCandidates) {
            builder.append("<tr><td>")
                    .append(assigneeOrCandidate.getFullNameWithLogin(locale))
                    .append("</td></tr>");
        }
        builder.append("</table>");
        Map<String, Object> params = ParamsMap.of("minValue", assigneeOrCandidates.get(0).getFullNameWithLogin(locale) + " +" + (assigneeOrCandidates.size() - 1),
                "value", builder.toString());
        return TemplateHelper.processTemplate(popup, params);
    }

    protected String createTableAbsence(AbsenceRequest absenceRequest, String lang) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        boolean isRussian = !lang.equals("En");

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

        String templateContents = resources.getResourceAsString(String.format(templateFolder + "absenceRequest/AbsenceRequestTable%s.html", lang));
        Assert.notNull(templateContents, "templateContents not found!");
        return TemplateHelper.processTemplate(templateContents, params);
    }

    @Override
    public void approveAbsenceForRecall(AbsenceForRecall absenceForRecall) {
        try (Transaction tx = persistence.getTransaction()) {

            Absence newAbsence = metadata.create(Absence.class);
            newAbsence.setPersonGroup(absenceForRecall.getEmployee());
            newAbsence.setType(dataManager.load(DicAbsenceType.class)
                    .query("select e from tsadv$DicAbsenceType e " +
                            " where e.code = 'RECALL'")
                    .list().stream().findFirst().orElse(null));
            newAbsence.setDateFrom(absenceForRecall.getRecallDateFrom());
            newAbsence.setDateTo(absenceForRecall.getRecallDateTo());
            newAbsence.setAbsenceDays(datesService.getFullDaysCount(absenceForRecall.getRecallDateFrom(),
                    absenceForRecall.getRecallDateTo()));
            EntityManager entityManager = persistence.getEntityManager();
            changeRequestStatus(absenceForRecall, "APPROVED");
            entityManager.persist(newAbsence);
        }
    }
}