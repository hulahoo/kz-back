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
import com.haulmont.chile.core.datatypes.Datatype;
import com.haulmont.chile.core.datatypes.Datatypes;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.entity.contracts.Id;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.security.entity.User;
import kz.uco.base.service.NotificationSenderAPIService;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.bproc.beans.BprocUserListProviderWithoutRedirect;
import kz.uco.tsadv.bproc.beans.entity.BprocEntityBeanAdapter;
import kz.uco.tsadv.bproc.beans.helper.AbstractBprocHelper;
import kz.uco.tsadv.config.FrontConfig;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.entity.bproc.ExtTaskData;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.modules.bpm.BpmRolesLink;
import kz.uco.tsadv.modules.bpm.BprocReassignment;
import kz.uco.tsadv.modules.performance.model.AssignedPerformancePlan;
import kz.uco.tsadv.modules.personal.dictionary.DicAbsenceType;
import kz.uco.tsadv.modules.personal.dictionary.DicHrRole;
import kz.uco.tsadv.modules.personal.model.*;
import kz.uco.tsadv.service.portal.NotificationService;
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
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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
    protected NotificationSenderAPIService notificationSenderAPIService;
    @Inject
    protected BprocHistoricService bprocHistoricService;
    @Inject
    protected Metadata metadata;
    @Inject
    protected Messages messages;
    @Inject
    protected Resources resources;
    @Inject
    protected BprocUserListProviderWithoutRedirect bprocUserListProvider;
    protected String templateFolder = "classpath:kz/uco/tsadv/templates/";
    @Inject
    protected GlobalConfig globalConfig;
    @Inject
    protected FrontConfig frontConfig;
    @Inject
    protected BprocRepositoryService bprocRepositoryService;
    @Inject
    protected BprocFormService bprocFormService;
    @Inject
    protected DatesService datesService;
    @Inject
    protected NotificationService notificationService;
    @Inject
    protected BprocEntityBeanAdapter<AbstractBprocRequest> bprocEntityBeanAdapter;
    @Inject
    protected ViewRepository viewRepository;
    @Inject
    protected BpmRolesDefinerService definerService;

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
    public <T extends AbstractBprocRequest> void start(T entity) {
        bprocEntityBeanAdapter.start(entity);
    }

    @Override
    public <T extends AbstractBprocRequest> void cancel(T entity) {
        bprocEntityBeanAdapter.cancel(entity);
    }

    @Override
    public <T extends AbstractBprocRequest> void revision(T entity) {
        bprocEntityBeanAdapter.revision(entity);
    }

    @Override
    public <T extends AbstractBprocRequest> void reject(T entity) {
        bprocEntityBeanAdapter.reject(entity);
    }

    @Override
    public <T extends AbstractBprocRequest> void approve(T entity) {
        bprocEntityBeanAdapter.approve(entity);
    }

    @Override
    public <T extends AbstractBprocRequest> void sendNotificationAndActivityToInitiator(T bprocRequest, String notificationTemplateCode) {
        ProcessInstanceData processInstanceData =
                this.getProcessInstanceData(bprocRequest.getProcessInstanceBusinessKey(), bprocRequest.getProcessDefinitionKey());

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
                .processInstanceBusinessKey(bprocRequest.getProcessInstanceBusinessKey())
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
    public <T> T getProcessVariable(AbstractBprocRequest entity, String variableName) {
        return this.getProcessVariable(
                this.getProcessInstanceData(entity.getProcessInstanceBusinessKey(), entity.getProcessDefinitionKey()).getId(),
                variableName);
    }

    @Override
    public <T extends AbstractBprocRequest> void changeRequestStatus(T entity, String code) {
        bprocEntityBeanAdapter.changeRequestStatus(entity, code);
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
        List<BpmRolesLink> links = CollectionUtils.isEmpty(rolesLinks)
                ? initiatorTask == null
                ? null
                : definerService.getBpmRolesDefiner(
                processInstanceData.getProcessDefinitionKey(),
                initiatorTask.getAssigneeOrCandidates().get(0).getPersonGroup().getId())
                .getLinks()
                : dataManager.load(BpmRolesLink.class)
                .query("select e from tsadv$BpmRolesLink e where e.id in :links ")
                .setParameters(ParamsMap.of("links", rolesLinks))
                .softDeletion(false)
                .view(new View(BpmRolesLink.class)
                        .addProperty("bprocUserTaskCode")
                        .addProperty("hrRole", new View(DicHrRole.class)
                                .addProperty("langValue")
                                .addProperty("code")))
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
                    if (taskData.getTaskDefinitionKey().equals(AbstractBprocRequest.INITIATOR_TASK_CODE))
                        taskData.setHrRole(getInitiatorHrRole());
                    if (links != null)
                        links.stream()
                                .filter(bpmRolesLink -> bpmRolesLink.getBprocUserTaskCode().equals(taskData.getTaskDefinitionKey()))
                                .findAny()
                                .map(BpmRolesLink::getHrRole)
                                .ifPresent(taskData::setHrRole);
                })
                .peek(taskData -> {
                    String taskId = taskData.getTaskDefinitionKey();
                    if (taskId != null) {
                        List<BprocReassignment> reassignments = getBprocReassignments(taskId, taskData.getProcessInstanceId())
                                .stream()
                                .filter(bprocReassignment -> !bprocReassignment.getStartTime().before(taskData.getCreateTime()))
                                .filter(bprocReassignment -> taskData.getEndTime() == null || !bprocReassignment.getEndTime().after(taskData.getEndTime()))
                                .collect(Collectors.toList());

                        reassignments.stream()
                                .map(reassignment -> parseToTaskData(taskData, reassignment))
                                .forEach(tasks::add);
                        if (!reassignments.isEmpty())
                            taskData.setCreateTime(reassignments.stream().map(BprocReassignment::getEndTime).max(Date::compareTo).orElse(null));
                    }
                })
                .forEach(tasks::add);

        return tasks;
    }

    public List<BprocReassignment> getBprocReassignments(String taskDefinitionKey, String processInstanceId) {
        return transactionalDataManager.load(BprocReassignment.class)
                .query("select e from tsadv_BprocReassignment e " +
                        " where e.taskDefinitionKey = :taskDefinitionKey " +
                        "   and e.processInstanceId = :processInstanceId " +
                        "   order by e.order ")
                .parameter("taskDefinitionKey", taskDefinitionKey)
                .parameter("processInstanceId", processInstanceId)
                .view(viewRepository.getView(BprocReassignment.class, View.LOCAL).addProperty("assignee", viewRepository.getView(TsadvUser.class, "user-fioWithLogin")))
                .list();
    }

    protected ExtTaskData parseToTaskData(ExtTaskData taskData, BprocReassignment reassignment) {
        ExtTaskData parsedTask = metadata.create(ExtTaskData.class);
        parsedTask.setId(reassignment.getId().toString());
        parsedTask.setName(taskData.getName());
        parsedTask.setTaskDefinitionKey(taskData.getTaskDefinitionKey());
        parsedTask.setHrRole(taskData.getHrRole());
        parsedTask.setExecutionId(taskData.getExecutionId());
        parsedTask.setOutcome(reassignment.getOutcome());
        parsedTask.setAssignee(reassignment.getAssignee().getId().toString());
        parsedTask.setAssigneeOrCandidates(Collections.singletonList(reassignment.getAssignee()));
        parsedTask.setCreateTime(reassignment.getStartTime());
        parsedTask.setEndTime(reassignment.getStartTime());
        parsedTask.setComment(reassignment.getComment());
        return parsedTask;
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
                String startComment = getProcessVariable(processInstanceData.getId(), "startComment");

                ExtTaskData initiatorTask = metadata.create(ExtTaskData.class);
                initiatorTask.setId(UUID.randomUUID().toString());
                initiatorTask.setName(AbstractBprocRequest.INITIATOR_TASK_CODE);
                initiatorTask.setTaskDefinitionKey(AbstractBprocRequest.INITIATOR_TASK_CODE);
                initiatorTask.setAssignee(initiator.getId().toString());
                initiatorTask.setAssigneeOrCandidates(Collections.singletonList(initiator));
                initiatorTask.setCreateTime(processInstanceData.getStartTime());
                initiatorTask.setEndTime(processInstanceData.getStartTime());
                initiatorTask.setHrRole(getInitiatorHrRole());
                initiatorTask.setOutcome(AbstractBprocRequest.OUTCOME_START);
                initiatorTask.setComment(startComment);
                return initiatorTask;
            }
        }
        return null;
    }

    protected DicHrRole getInitiatorHrRole() {
        DicHrRole dicHrRole = metadata.create(DicHrRole.class);
        dicHrRole.setLangValue1("Инициатор");
        dicHrRole.setLangValue3("Initiator");
        dicHrRole.setCode("INITIATOR");
        return dicHrRole;
    }

    @Override
    public <T extends AbstractBprocRequest> void sendNotificationAndActivity(T entity, User user, ActivityType activityType, String notificationTemplateCode) {

        User sessionUser = userSessionSource.getUserSession().getUser();
        Datatype<Long> datatype = Datatypes.getNN(Long.class);

        notificationTemplateCode = changeNotificationTemplateCode(notificationTemplateCode, entity);

        Map<String, Object> notificationParams = getNotificationParams(notificationTemplateCode, entity);

        if (!PersistenceHelper.isLoadedWithView(user, "user-fioWithLogin"))
            user = dataManager.reload(user, "user-fioWithLogin");

        notificationParams.put("userRu", ((TsadvUser) user).getFullNameWithLogin(Locale.forLanguageTag("ru")));
        notificationParams.put("userEn", ((TsadvUser) user).getFullNameWithLogin(Locale.forLanguageTag("en")));

        notificationParams.put("requestLinkRu", "");
        notificationParams.put("requestLinkEn", "");

        notificationParams.put("requestFrontLinkRu", "");
        notificationParams.put("requestFrontLinkEn", "");

        String requestFrontLink = getRequestLinkFront(entity);
        notificationParams.put("entityFrontLinkRu", String.format(requestFrontLink, "Открыть заявку " + datatype.format(entity.getRequestNumber())));
        notificationParams.put("entityFrontLinkEn", String.format(requestFrontLink, "Open request " + datatype.format(entity.getRequestNumber())));

        notificationParams.put("requestNumber", datatype.format(entity.getRequestNumber()));

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

        String activityLink = getActivityLink(globalConfig.getWebAppUrl(), entity, activity, true);
        String activityFrontLink = getActivityLink(frontConfig.getFrontAppUrl(), entity, activity, false);

        notificationParams.put("requestLinkRu", String.format(activityLink, "Открыть заявку " + entity.getRequestNumber()));
        notificationParams.put("requestLinkEn", String.format(activityLink, "Open request " + entity.getRequestNumber()));

        notificationParams.put("requestFrontLinkRu", String.format(activityFrontLink, "Открыть заявку " + entity.getRequestNumber()));
        notificationParams.put("requestFrontLinkEn", String.format(activityFrontLink, "Open request " + entity.getRequestNumber()));

        notificationSenderAPIService.sendParametrizedNotification(notificationTemplateCode, (TsadvUser) user, notificationParams);
    }

    protected <T extends AbstractBprocRequest> String changeNotificationTemplateCode(String notificationTemplateCode, T entity) {
        return bprocEntityBeanAdapter.changeNotificationTemplateCode(notificationTemplateCode, entity);
    }

    protected <T extends AbstractBprocRequest> String getRequestLinkFront(T entity) {
        return "<a href=\"" +
                frontConfig.getFrontAppUrl() +
                "/" + notificationService.getLinkByEntityName(entity.getMetaClass().getName()) +
                "/" + entity.getId() + "\" target=\"_blank\">%s " + "</a>";
    }

    protected <T extends AbstractBprocRequest> String getActivityLink(String appUrl, T entity, Activity activity, boolean isWebUrl) {
        if (!"NOTIFICATION".equals(activity.getType().getCode())) {
            ActivityType activityType = activity.getType();
            if (activityType.getWindowProperty() != null) {
                if (isWebUrl)
                    return "<a href=\"" + appUrl + "/open?screen=" + activityType.getWindowProperty().getScreenName() +
                            "&item=" + activityType.getWindowProperty().getEntityName() + "-" + activity.getReferenceId() +
                            "&params=activityId:" + activity.getId() +
                            "\" target=\"_blank\">%s " + "</a>";
                else return this.getRequestLinkFront(entity);
            }
        }

        String entityName = entity.getMetaClass().getName();
        WindowProperty windowProperty = dataManager.load(WindowProperty.class)
                .query("select e from uactivity$WindowProperty e where e.entityName = :entityName")
                .setParameters(ParamsMap.of("entityName", entityName))
                .one();

        if (isWebUrl)
            return "<a href=\"" + appUrl + "/open?screen=" + windowProperty.getScreenName() +
                    "&item=" + entityName + "-" + entity.getId() +
                    "\" target=\"_blank\">%s " + "</a>";
        else
            return "<a href=\"" + appUrl + "/activity/" + activity.getId() + "\" target=\"_blank\">%s " + "</a>";
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
        Map<String, Object> notificationParams = bprocEntityBeanAdapter.getNotificationParams(templateCode, entity);
        return getDefaultNotificationParams(notificationParams, entity);
    }

    protected <T extends AbstractBprocRequest> Map<String, Object> getDefaultNotificationParams(Map<String, Object> params, T entity) {

        params.put("item", entity);
        params.put("entity", entity);

        ProcessInstanceData processInstanceData = getProcessInstanceData(entity.getProcessInstanceBusinessKey(), entity.getProcessDefinitionKey());
        TsadvUser initiator = getProcessVariable(processInstanceData.getId(), "initiator");
        Assert.notNull(initiator, "Initiator not found!");
        initiator = dataManager.reload(initiator, "user-fioWithLogin");
        params.put("initiatorRu", initiator.getFullNameWithLogin(Locale.forLanguageTag("ru")));
        params.put("initiatorEn", initiator.getFullNameWithLogin(Locale.forLanguageTag("en")));

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        List<ExtTaskData> processTasks = getProcessTasks(processInstanceData);
        String lastApprovedUserRu = "";
        String lastApprovedUserEn = "";
        for (ExtTaskData processTask : processTasks) {
            if (processTask.getOutcome() != null && AbstractBprocRequest.OUTCOME_APPROVE.equals(processTask.getOutcome())
                    && processTask.getAssigneeOrCandidates() != null && !processTask.getAssigneeOrCandidates().isEmpty()) {
                lastApprovedUserRu = processTask.getAssigneeOrCandidates().stream().map(tsadvUser ->
                        tsadvUser.getFullNameWithLogin(Locale.forLanguageTag("ru"))).findFirst().orElse("");
                lastApprovedUserEn = processTask.getAssigneeOrCandidates().stream().map(tsadvUser ->
                        tsadvUser.getFullNameWithLogin(Locale.forLanguageTag("en"))).findFirst().orElse("");
            }
        }
        params.put("lastApprovedUserRu", lastApprovedUserRu);
        params.put("lastApprovedUserEn", lastApprovedUserEn);
        params.put("approversTableRu", getApproversTable("Ru", processInstanceData));
        params.put("approversTableEn", getApproversTable("En", processInstanceData));
        params.put("comment", StringUtils.defaultString(getProcessVariable(processInstanceData.getId(), "comment"), ""));

        //noinspection unchecked
        entity = transactionalDataManager.load((Class<T>) entity.getClass())
                .id(entity.getId())
                .viewProperties("status.langValue1", "status.langValue3", "requestDate")
                .one();

        params.putIfAbsent("requestStatusRu", entity.getStatus().getLangValue1());
        params.putIfAbsent("requestStatusEn", entity.getStatus().getLangValue3());
        params.put("requestDate", entity.getRequestDate() != null
                ? dateFormat.format(entity.getRequestDate())
                : "");

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

    @Override
    public void changeStatusBprocRequest(AbstractBprocRequest entity, String status, String notificationCode) {
        changeRequestStatus(entity, status);
        sendNotificationToInitiator(entity, notificationCode);
    }

    @Override
    public void changeStatusLeavingVacationRequest(LeavingVacationRequest entity, String status, String notificationCode) {
        changeRequestStatus(entity, status);
        sendNotificationToInitiator(entity, notificationCode);
    }

    @Override
    public void changeStatusChangeAbsenceDaysRequest(ChangeAbsenceDaysRequest entity, String status, String notificationCode) {
        changeRequestStatus(entity, status);
        sendNotificationToInitiator(entity, notificationCode);
    }

    @Override
    public void approveAssignedPerformancePlan(AssignedPerformancePlan request) {
        this.approve(request);
    }

}