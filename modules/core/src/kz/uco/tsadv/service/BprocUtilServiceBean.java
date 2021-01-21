package kz.uco.tsadv.service;

import com.haulmont.addon.bproc.data.Outcome;
import com.haulmont.addon.bproc.data.OutcomesContainer;
import com.haulmont.addon.bproc.entity.HistoricVariableInstanceData;
import com.haulmont.addon.bproc.entity.TaskData;
import com.haulmont.addon.bproc.service.BprocHistoricService;
import com.haulmont.addon.bproc.service.BprocRuntimeService;
import com.haulmont.addon.bproc.service.BprocTaskService;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.TransactionalDataManager;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.security.entity.User;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.entity.bproc.BprocTaskHistory;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.administration.UserExt;
import kz.uco.tsadv.modules.bpm.BprocInstanceRolesLink;
import kz.uco.tsadv.modules.personal.dictionary.DicHrRole;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.modules.personal.enums.PositionChangeRequestType;
import kz.uco.tsadv.modules.personal.model.AbsenceRequest;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.modules.personal.model.PositionChangeRequest;
import kz.uco.uactivity.entity.Activity;
import kz.uco.uactivity.entity.ActivityType;
import kz.uco.uactivity.entity.StatusEnum;
import kz.uco.uactivity.service.ActivityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service(BprocUtilService.NAME)
public class BprocUtilServiceBean implements BprocUtilService {

    @Inject
    protected TransactionalDataManager transactionalDataManager;
    @Inject
    protected BprocRuntimeService bprocRuntimeService;
    @Inject
    protected Metadata metadata;
    @Inject
    protected BprocTaskService bprocTaskService;
    @Inject
    protected ActivityService activityService;
    @Inject
    protected MetadataTools metadataTools;
    @Inject
    protected CommonService commonService;
    @Inject
    protected Persistence persistence;
    @Inject
    private BprocHistoricService bprocHistoricService;

    @Override
    @Transactional
    public List<String> getAssignee(String processDefinitionKey, String entityId, String approverCode) {
        List<BprocInstanceRolesLink> bprocInstanceRolesLinks = transactionalDataManager.load(BprocInstanceRolesLink.class)
                .query("select e from tsadv_BprocInstanceRolesLink e" +
                        " join tsadv$BpmRolesLink brl on brl.hrRole.code = e.hrRole.code" +
                        " where e.processInstanceId = :entityId" +
                        "   and brl.bprocUserTaskCode = :approverCode" +
                        "   and brl.bpmRolesDefiner.processDefinitionKey = :processDefinitionKey")
                .parameter("entityId", entityId)
                .parameter("approverCode", approverCode)
                .parameter("processDefinitionKey", processDefinitionKey)
                .view("bprocInstanceRolesLink-getAssignee")
                .list();
        List<UserExt> resultUserList = new ArrayList<>();
        for (BprocInstanceRolesLink bprocInstanceRolesLink : bprocInstanceRolesLinks) {
            if (bprocInstanceRolesLink.getUser() == null) {
                DicHrRole dicHrRole = bprocInstanceRolesLink.getHrRole();
                List<UserExt> userList = transactionalDataManager.load(UserExt.class)
                        .query("select distinct e.user from tsadv$HrUserRole e" +
                                " where e.role.code = :roleCode")
                        .parameter("roleCode", dicHrRole.getCode())
                        .view(View.LOCAL)
                        .list();
                resultUserList.addAll(userList);
            } else {
                resultUserList.add(bprocInstanceRolesLink.getUser());
            }
        }
        return resultUserList.stream().map(userExt -> userExt.getId().toString()).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean hasAssignee(String processDefinitionKey, String entityId, String approverCode) {
        return !getAssignee(processDefinitionKey, entityId, approverCode).isEmpty();
    }

    @Override
    public void setStatus(String statusCode, String entityName, String entityId) {
        DicRequestStatus dicRequestStatus = transactionalDataManager.load(DicRequestStatus.class)
                .query("select e from tsadv$DicRequestStatus e" +
                        " where e.code = :statusCode")
                .parameter("statusCode", statusCode)
                .one();
        MetaClass metaClass = metadata.getClass(entityName);
        if (metaClass == null) {
            throw new NullPointerException("metaClass is null");
        }
        Class<? extends AbstractBprocRequest> aClass = metaClass.getJavaClass();
        View view = new View(AbstractBprocRequest.class);
        view.addProperty("status", new View(DicRequestStatus.class));
        AbstractBprocRequest abstractBprocRequest = transactionalDataManager.load(aClass)
                .id(UUID.fromString(entityId)).view(view).one();
        abstractBprocRequest.setStatus(dicRequestStatus);
        transactionalDataManager.save(abstractBprocRequest);
    }

    @Inject
    protected UserSessionSource userSessionSource;

    @Override
    public void sendNotification(String assignee, String notificationCode, String entityName, String entityId) {
        UserExt assignedUser = getUserById(assignee);
        User assignedBy = userSessionSource.getUserSession().getUser();
        ActivityType activityType = getActivityTypeByCode(getActivityCodeFromTableName(UUID.fromString(entityId), entityName));
        Map<String, Object> paramsMap = getParamsMap(assignedUser, notificationCode, entityName, entityId);
        Activity activity = activityService.createActivity(
                assignedUser,
                assignedBy,
                activityType,
                StatusEnum.active,
                "description",
                null,
                new Date(),
                null,
                null,
                UUID.fromString(entityId),
                notificationCode,
                paramsMap);
    }

    @Override
    @Transactional
    public void doneActivity(String assigneeUserId, String entityId) {
        List<Activity> activityList = getActivityList(UUID.fromString(entityId), UUID.fromString(assigneeUserId));
        try (Transaction transaction = persistence.getTransaction()) {
            EntityManager em = persistence.getEntityManager();
            for (Activity activity : activityList) {
                activity.setStatus(StatusEnum.done);
                em.merge(activity);
            }
            transaction.commit();
        }
    }

    protected List<Activity> getActivityList(UUID referenceId, UUID assignedUserId) {
        LoadContext<Activity> loadContext = LoadContext.create(Activity.class)
                .setQuery(LoadContext.createQuery(
                        "select e from uactivity$Activity e " +
                                " where e.referenceId = :referenceId " +
                                "  and e.assignedUser.id = :assignedUserId " +
                                "   and e.id not in ( select a.activity.id from tsadv$BpmRequestMessage a where a.entityId = :referenceId )  ")
                        .setParameter("referenceId", referenceId)
                        .setParameter("assignedUserId", assignedUserId)
                ).setView(View.LOCAL);
        return transactionalDataManager.loadList(loadContext);
    }

    protected Map<String, Object> getParamsMap(UserExt toWhom, String notificationCode, String entityName,
                                               String entityId) {
        Map<String, Object> resultMap = new HashMap<>();
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        AbstractBprocRequest abstractBprocRequest = null;
        //специфические параметры для каждого конкретного уведомления
        switch (notificationCode) {
            case "absence.request.approver.notification":
            case "absence.request.revision":
            case "absence.request.cancelled":
            case "absence.request.approved":
                resultMap.put("personFullName", getPersonFullName(toWhom));
                AbsenceRequest absenceRequest = transactionalDataManager.load(AbsenceRequest.class)
                        .id(UUID.fromString(entityId))
                        .view("absenceRequest.view").one();
                abstractBprocRequest = absenceRequest;
                resultMap.put("absenceRequestAssignmentGroup", absenceRequest.getAssignmentGroup().getAssignment().getPersonGroup().getPerson().getFullName());
                resultMap.put("absenceRequestAbsenceType", absenceRequest.getType().getLangValue());
                resultMap.put("absenceRequestDateFrom", df.format(absenceRequest.getDateFrom()));
                resultMap.put("absenceRequestDateTo", df.format(absenceRequest.getDateTo()));
                break;
        }
        //общие для всех сообщений параметры
        if (abstractBprocRequest != null) {
            resultMap.put("requestNumber", abstractBprocRequest.getRequestNumber());
            resultMap.put("requestStatus", abstractBprocRequest.getStatus().getLangValue());
        }
        return resultMap;
    }

    protected String getPersonFullName(UserExt assignee) {
        return transactionalDataManager.load(PersonExt.class)
                .query("select e from base$PersonExt e" +
                        " where e.group.id = :personGroupId" +
                        "   and :systemDate between e.startDate and e.endDate")
                .parameter("personGroupId", assignee.getPersonGroup().getId())
                .parameter("systemDate", CommonUtils.getSystemDate())
                .one().getFullName();
    }

    protected ActivityType getActivityTypeByCode(String activityTypeCode) {
        return transactionalDataManager.load(ActivityType.class)
                .query("select e from uactivity$ActivityType e" +
                        " where e.code = :code")
                .parameter("code", activityTypeCode)
                .one();
    }

    protected UserExt getUserById(String assignee) {
        return transactionalDataManager.load(UserExt.class)
                .id(UUID.fromString(assignee))
                .view("userExt.bproc").one();
    }

    protected String getActivityCodeFromTableName(UUID entityId, String entityName) {
        StringBuilder builder = new StringBuilder();
        String tableName = metadataTools.getDatabaseTable(metadata.getClass(entityName));
        builder.append(tableName, Objects.requireNonNull(tableName).indexOf('_') + 1, tableName.length());
        builder.append("_APPROVE");
        if (tableName.equalsIgnoreCase("TSADV_POSITION_CHANGE_REQUEST")) {
            PositionChangeRequest positionChangeRequest = commonService.getEntity(PositionChangeRequest.class,
                    String.format("select e from %s e where e.id = :id ", entityName),
                    ParamsMap.of("id", entityId), View.LOCAL);
            if (positionChangeRequest.getRequestType().equals(PositionChangeRequestType.CHANGE)) {
                return "POSITION_CHANGE_REQUEST_TYPE_CHANGE_APPROVE";
            }
        }
        return builder.toString();
    }

    @Override
    public void taskCreate(String executionId) {
        System.out.println("TASK LISTENER CREATE");
    }

    @Override
    public void taskComplete(String executionId) {
        System.out.println("TASK LISTENER COMPLETE");
    }

    @Override
    public void taskDelete(String executionId) {
        System.out.println("TASK LISTENER DELETE");
    }

    @Override
    public void executionStart(String executionId) {
        System.out.println("EXECUTION LISTENER START");
    }

    @Override
    public void executionEnd(String executionId) {
        System.out.println("EXECUTION LISTENER END");
    }

    @Override
    public void executionTake(String executionId) {
        System.out.println("EXECUTION LISTENER TAKE");
    }

//    public void sendUserNotification(User assignedUser, User assignedBy, UUID entityId, UUID bpmProcInstanceId, String activityCode, String emailTemplateCode, String notificationTemplateCode) {
//        HashMap<String, Object> emailParams = new HashMap<>();
//        HashMap<String, Object> notificationParams;
//        emailParams = (HashMap<String, Object>) getParams(emailParams, emailTemplateCode, entityId, bpmProcInstanceId);
//        if (emailTemplateCode.equals(notificationTemplateCode)) {
//            notificationParams = emailParams;
//        } else {
//            notificationParams = (HashMap<String, Object>) getParams(new HashMap<>(emailParams), notificationTemplateCode, entityId, bpmProcInstanceId);
//        }
//
//        if (!PersistenceHelper.isLoaded(assignedUser, "email")
//                || PersistenceHelper.isLoaded(assignedUser, "language")
//                || PersistenceHelper.isLoaded(assignedUser, "mobilePhone")
//                || PersistenceHelper.isLoaded(assignedUser, "telegramChatId")) {
//            View view = new View(assignedUser.getClass())
//                    .addProperty("email").addProperty("language").addProperty("mobilePhone").addProperty("telegramChatId");
//            assignedUser = findById(User.class, assignedUser.getId(), view);
//        }
//
//        View view = new View(ActivityType.class)
//                .addProperty("code")
//                .addProperty("windowProperty",
//                        new View(WindowProperty.class).addProperty("entityName").addProperty("screenName"));
//
//        LoadContext<ActivityType> loadContext = LoadContext.create(ActivityType.class);
//        loadContext.setQuery(LoadContext.createQuery("select e from uactivity$ActivityType e where e.code = :code")
//                .setParameter("code", activityCode))
//                .setView(view);
//
//
//        notificationParams.put("requestUrlRu", "");
//        notificationParams.put("requestUrlEn", "");
//
//        Activity activity = activityService.createActivity(
//                assignedUser,
//                assignedBy,
//                dataManager.load(loadContext),
//                StatusEnum.active,
//                "description",
//                null,
//                new Date(),
//                null,
//                null,
//                entityId,
//                notificationTemplateCode,
//                notificationParams);
//
//        ProcInstance procInstance = commonService.getEntity(ProcInstance.class, bpmProcInstanceId, "procInstance-start");
//
//        String requestUrl = getRequestUrl(activity, procInstance);
//        String tagA = getTagHtmlA(requestUrl, procInstance);
//        emailParams.put("requestUrlRu", String.format(tagA, "Открыть заявку"));
//        emailParams.put("requestUrlEn", String.format(tagA, "Open request"));
//        sendNotification(emailTemplateCode, (UserExt) assignedUser, emailParams);
//    }

    @Override
    public void reject() {

    }

    @Override
    public void approve(String entityName, String entityId) {

    }

    @Override
    public List<? extends BprocTaskHistory> getBprocTaskHistory(String procInstanceId) {
        List<BprocTaskHistory> result = new ArrayList<>();
        List<TaskData> tasks = bprocHistoricService.createHistoricTaskDataQuery().processInstanceId(procInstanceId).list();
        for (TaskData task : tasks) {
            BprocTaskHistory bprocTaskHistory = metadata.create(BprocTaskHistory.class);
            bprocTaskHistory.setProcInstanseId(procInstanceId);
            bprocTaskHistory.setUser(getUserExtForTaskHistory(task));
            bprocTaskHistory.setRole(getRoleForTaskHistory(task));
            bprocTaskHistory.setStartDate(task.getCreateTime());
            bprocTaskHistory.setEndDate(task.getEndTime());
            bprocTaskHistory.setOutcome(getOutcomeForTaskHistory(task));
            result.add(bprocTaskHistory);
        }
        return result;
    }

    private String getOutcomeForTaskHistory(TaskData task) {
        String outcomeId = null;
        HistoricVariableInstanceData historicVariableInstanceData
                = bprocHistoricService.createHistoricVariableInstanceDataQuery()
                .processInstanceId(task.getProcessInstanceId())
                .variableName(task.getTaskDefinitionKey() + "_result")
                .list().stream()
                .findAny().orElse(null);
        if (historicVariableInstanceData != null
                && historicVariableInstanceData.getValue() != null
                && historicVariableInstanceData.getValue() instanceof OutcomesContainer) {
            OutcomesContainer outcomesContainer = (OutcomesContainer) historicVariableInstanceData.getValue();
            if (outcomesContainer != null
                    && outcomesContainer.getOutcomes() != null) {
                Outcome outcome = outcomesContainer.getOutcomes().stream().findFirst().orElse(null);
                if (outcome != null) {
                    outcomeId = outcome.getOutcomeId();
                }
            }
        }
        return outcomeId;
    }

    private DicHrRole getRoleForTaskHistory(TaskData task) {
        return null;
    }

    public UserExt getUserExtForTaskHistory(TaskData task) {
        UserExt userExt = metadata.create(UserExt.class);
        userExt.setId(UUID.fromString(task.getAssignee()));
        return userExt;
    }
}