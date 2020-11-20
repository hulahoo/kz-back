package kz.uco.tsadv.service;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.bpm.core.ProcessRuntimeManager;
import com.haulmont.bpm.entity.ProcActor;
import com.haulmont.bpm.entity.ProcDefinition;
import com.haulmont.bpm.entity.ProcInstance;
import com.haulmont.bpm.entity.ProcTask;
import com.haulmont.bpm.service.ProcessMessagesService;
import com.haulmont.bpm.service.ProcessRuntimeService;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.entity.CategoryAttributeValue;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.security.entity.User;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.entity.abstraction.AbstractTimeBasedEntity;
import kz.uco.base.entity.core.notification.SendingNotification;
import kz.uco.base.entity.dictionary.DicCurrency;
import kz.uco.base.notification.NotificationSenderAPI;
import kz.uco.base.service.NotificationService;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.beans.BpmUtils;
import kz.uco.tsadv.entity.AssignmentRequest;
import kz.uco.tsadv.entity.AssignmentSalaryRequest;
import kz.uco.tsadv.entity.dbview.ActivityTask;
import kz.uco.tsadv.entity.dbview.ProcInstanceV;
import kz.uco.tsadv.entity.tb.TemporaryTranslationRequest;
import kz.uco.tsadv.exceptions.ItemNotFoundException;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.administration.UserExt;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.modules.personal.dictionary.DicSalaryChangeReason;
import kz.uco.tsadv.modules.personal.enums.GrossNet;
import kz.uco.tsadv.modules.personal.enums.PositionChangeRequestType;
import kz.uco.tsadv.modules.personal.enums.SalaryType;
import kz.uco.tsadv.modules.personal.group.*;
import kz.uco.tsadv.modules.personal.model.*;
import kz.uco.uactivity.entity.Activity;
import kz.uco.uactivity.entity.ActivityType;
import kz.uco.uactivity.entity.StatusEnum;
import kz.uco.uactivity.entity.WindowProperty;
import kz.uco.uactivity.service.ActivityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service(BpmService.NAME)
public class BpmServiceBean implements BpmService {

    @Inject
    protected ProcessRuntimeManager processRuntimeManager;
    @Inject
    protected Persistence persistence;
    @Inject
    protected EmployeeService employeeService;
    @Inject
    protected Metadata metadata;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected CommonService commonService;
    @Inject
    protected NotificationSenderAPI notificationSender;
    @Inject
    protected ActivityService activityService;
    @Inject
    protected MetadataTools metadataTools;
    @Inject
    protected Messages messages;
    @Inject
    protected UserSessionSource userSessionSource;
    @Inject
    protected AssignmentSalaryService assignmentSalaryService;
    @Inject
    protected BpmUtils bpmUtils;
    @Inject
    protected BpmUserSubstitutionService bpmUserSubstitutionService;
    @Inject
    protected ProcessMessagesService processMessagesService;
    @Inject
    protected RuntimeService runtimeService;
    @Inject
    protected CallStoredFunctionService callStoredFunctionService;
    @Inject
    protected TimeSource timeSource;
    @Inject
    protected ViewRepository viewRepository;
    @Inject
    protected HierarchyService hierarchyService;
    @Inject
    protected GlobalConfig globalConfig;
    @Inject
    protected NotificationService notificationService;
    @Inject
    protected ProcessRuntimeService processRuntimeService;
    @Inject
    protected BpmProcActorsService bpmProcActorsService;

    @Override
    public void startBpmProcess(@Nonnull Entity entity, @Nonnull String processName, @Nonnull PersonGroupExt personGroupExt,
                                PositionGroupExt positionGroupExt, OrganizationGroupExt organizationGroupExt) {

        ProcDefinition definition = bpmUtils.getProcDefinition(entity, processName, "procDefinition-with-model");
        if (definition == null) {
            throw new RuntimeException("ProcDefinition not found!");
        }
        ProcInstance procInstance = metadata.create(ProcInstance.class);
        procInstance.setActive(true);
        procInstance.setProcDefinition(definition);
        procInstance.setObjectEntityId(entity.getId());
        procInstance.setEntityName(entity.getMetaClass().getName());

        Map<String, Object> param = bpmProcActorsService.generateProcActors(procInstance, personGroupExt, positionGroupExt,
                organizationGroupExt, definition.getProcRoles(), ParamsMap.empty());

        if (!((Set<String>) param.get("notFoundUserInRoleRequired")).isEmpty()) {
            throw new RuntimeException("Required roles not found!");
        }

        Set<ProcActor> procActors = new HashSet<>((Collection<ProcActor>) param.get("procActorsDs"));

        if (procActors.isEmpty()) {
            throw new RuntimeException("Actors not found!");
        }

        procInstance.setProcActors(procActors);

        CommitContext context = new CommitContext();
        procActors.forEach(context::addInstanceToCommit);
        context.addInstanceToCommit(procInstance);

        Set<Entity> committed = dataManager.commit(context);

        ProcInstance committedProcInstance = (ProcInstance) committed.stream()
                .filter(p -> p.equals(procInstance))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Error"));

        processRuntimeService.startProcess(committedProcInstance, "", new HashMap<>());
    }

    @Override
    @Deprecated
    public void doneActivity(UUID entityId, String activityCode) {
        UUID userId = userSessionSource.getUserSession().getUser().getId();
        if (activityCode.toUpperCase().matches("POSITION_CHANGE_REQUEST_APPROVE|POSITION_CHANGE_REQUEST_TYPE_CHANGE_APPROVE")) {
            doneActivity(entityId, "POSITION_CHANGE_REQUEST_TYPE_CHANGE_APPROVE", userId);
            doneActivity(entityId, "POSITION_CHANGE_REQUEST_APPROVE", userId);
        } else {
            doneActivity(entityId, activityCode, userId);
        }
    }

    @Override
    public void doneActivity(UUID bpmProcInstanceId) {
        ProcInstance procInstance = findById(ProcInstance.class, bpmProcInstanceId, "procInstance-start");

        UserExt activeTaskUser = bpmUtils.getActiveTaskUser(bpmProcInstanceId, "userExt.edit");

        Assert.notNull(activeTaskUser, String.format("bpmProcInstance[%s]: active user not found!", bpmProcInstanceId));
        UUID userId = activeTaskUser.getId();

        UUID entityId = (UUID) Objects.requireNonNull(procInstance).getObjectEntityId();
        String entityName = procInstance.getEntityName();

        doneActivity(entityId, getActivityCodeFromTableName(entityId, entityName), userId);
    }

    protected void doneActivity(UUID entityId, String activityCode, UUID userId) {
        List<Activity> activityList = getActivityList(entityId, activityCode, userId);
        try (Transaction transaction = persistence.getTransaction()) {
            EntityManager em = persistence.getEntityManager();
            for (Activity activity : activityList) {
                activity.setStatus(StatusEnum.done);
                em.merge(activity);
            }
            transaction.commit();
        }
    }

    //todo
    protected List<Activity> getActivityList(UUID referenceId, String taskCode, UUID assignedUserId) {
        LoadContext<Activity> loadContext = LoadContext.create(Activity.class)
                .setQuery(LoadContext.createQuery(
                        "select e from uactivity$Activity e " +
                                " where e.referenceId = :referenceId " +
                                "  and e.type.code = :taskCode " +
                                "  and e.assignedUser.id = :assignedUserId " +
                                "   and e.id not in ( select a.activity.id from tsadv$BpmRequestMessage a where a.entityId = :referenceId )  ")
                        .setParameter("referenceId", referenceId)
                        .setParameter("taskCode", taskCode)
                        .setParameter("assignedUserId", assignedUserId)
                ).setView(View.LOCAL);
        return dataManager.loadList(loadContext);
    }

    /**
     * For call method in map {@link RuntimeService#getVariableLocal(String, String)} have to contain key "assignee"
     * <p>
     * To calling use {@link ActivitiEvent} with type {@link org.activiti.engine.delegate.event.ActivitiEventType#TASK_CREATED}
     *
     * @param bpmProcInstanceId        ProcInctance id
     * @param bpmProcTaskId            ProcTask id
     * @param emailTemplateCode        Code of template that sending to email
     * @param notificationTemplateCode Code of template that sending to bell
     * @see com.haulmont.bpm.core.engine.listener.BpmActivitiListener#onEvent(ActivitiEvent)
     */
    public void notifyAssignee(UUID bpmProcInstanceId, UUID bpmProcTaskId, String emailTemplateCode, String notificationTemplateCode) {
        ProcInstance procInstance = reloadById(ProcInstance.class, bpmProcInstanceId, "procInstance-start");
        ProcTask procTask = reloadById(ProcTask.class, bpmProcTaskId, "procTask-frame-extended");

        String assigneeId = runtimeService.getVariableLocal(Objects.requireNonNull(procTask).getActExecutionId(), "assignee").toString();

        UserExt notifyUser = findById(UserExt.class, UUID.fromString(assigneeId), "user.edit");
        UserExt assignedBy = findById(UserExt.class, userSessionSource.getUserSession().getAttribute(StaticVariable.USER_EXT_ID), "user.edit");

        sendUserNotification(notifyUser, assignedBy, (UUID) Objects.requireNonNull(procInstance).getObjectEntityId(),
                bpmProcInstanceId, getActivityCodeFromTableName((UUID) procInstance.getObjectEntityId(), procInstance.getEntityName()),
                emailTemplateCode, notificationTemplateCode);
    }

    @Deprecated
    public void notify(UUID entityId, String entityName, UUID bpmProcInstanceId, String role, String emailTemplateCode) {
        this.notify(entityId, entityName, bpmProcInstanceId, role, emailTemplateCode, emailTemplateCode);
    }

    @Deprecated
    public void notify(UUID entityId, String entityName, UUID bpmProcInstanceId, String role, String emailTemplateCode, String notificationTemplateCode) {
        EntityManager em = persistence.getEntityManager();

        UserExt userExt = bpmUtils.getActiveTaskUser(bpmProcInstanceId, "userExt.edit");

        UserExt notifyUser = getNextUser(userExt, bpmProcInstanceId, role);
        if (notifyUser == null) {
            throw new ItemNotFoundException("The next approver not found!");
        }

        String idBpmUserSubstitution = bpmUserSubstitutionService.getCurrentBpmUserSubstitution(notifyUser, false);
        notifyUser = idBpmUserSubstitution != null ? em.find(UserExt.class, UUID.fromString(idBpmUserSubstitution), View.MINIMAL) : notifyUser;

        if (userExt == null) {
            userExt = bpmUtils.getCreatedBy(bpmProcInstanceId, "userExt.edit");
        }

        sendUserNotification(notifyUser, userExt, entityId, bpmProcInstanceId,
                getActivityCodeFromTableName(entityId, entityName),
                emailTemplateCode, notificationTemplateCode);
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

    @Deprecated
    protected UserExt getNextUser(UserExt userExt, UUID bpmProcInstanceId, String role) {
        List<String> list = processRuntimeManager.getTaskAssigneeList(bpmProcInstanceId, role);
        if (list.size() > 0) {
            String id = list.get(0);
            if (userExt != null) {
                for (int i = 0; i < list.size() - 1; i++) {
                    if (list.get(i).equals(userExt.getId().toString()))
                        id = list.get(i + 1);
                }
            }
            return getUserByKey("userExt.id", UUID.fromString(id));
        }
        return null;
    }

    @Override
    public boolean hasActor(UUID bpmProcInstanceId, String role) {
        return !processRuntimeManager.getTaskAssigneeList(bpmProcInstanceId, role).isEmpty();
    }

    @Override
    public void sendUserNotification(User assignedUser, User assignedBy, UUID entityId, UUID bpmProcInstanceId, String activityCode, String emailTemplateCode, String notificationTemplateCode) {
        HashMap<String, Object> emailParams = new HashMap<>();
        HashMap<String, Object> notificationParams;
        emailParams = (HashMap<String, Object>) getParams(emailParams, emailTemplateCode, entityId, bpmProcInstanceId);
        if (emailTemplateCode.equals(notificationTemplateCode)) {
            notificationParams = emailParams;
        } else {
            notificationParams = (HashMap<String, Object>) getParams(new HashMap<>(emailParams), notificationTemplateCode, entityId, bpmProcInstanceId);
        }

        if (!PersistenceHelper.isLoaded(assignedUser, "email")
                || PersistenceHelper.isLoaded(assignedUser, "language")
                || PersistenceHelper.isLoaded(assignedUser, "mobilePhone")
                || PersistenceHelper.isLoaded(assignedUser, "telegramChatId")) {
            View view = new View(assignedUser.getClass())
                    .addProperty("email").addProperty("language").addProperty("mobilePhone").addProperty("telegramChatId");
            assignedUser = findById(User.class, assignedUser.getId(), view);
        }

        View view = new View(ActivityType.class)
                .addProperty("code")
                .addProperty("windowProperty",
                        new View(WindowProperty.class).addProperty("entityName").addProperty("screenName"));

        LoadContext<ActivityType> loadContext = LoadContext.create(ActivityType.class);
        loadContext.setQuery(LoadContext.createQuery("select e from uactivity$ActivityType e where e.code = :code")
                .setParameter("code", activityCode))
                .setView(view);


        notificationParams.put("requestUrlRu", "");
        notificationParams.put("requestUrlEn", "");

        Activity activity = activityService.createActivity(
                assignedUser,
                assignedBy,
                dataManager.load(loadContext),
                StatusEnum.active,
                "description",
                null,
                new Date(),
                null,
                null,
                entityId,
                notificationTemplateCode,
                notificationParams);

        ProcInstance procInstance = commonService.getEntity(ProcInstance.class, bpmProcInstanceId, "procInstance-start");

        String requestUrl = getRequestUrl(activity, procInstance);
        String tagA = getTagHtmlA(requestUrl, procInstance);
        emailParams.put("requestUrlRu", String.format(tagA, "Открыть заявку"));
        emailParams.put("requestUrlEn", String.format(tagA, "Open request"));
        sendNotification(emailTemplateCode, (UserExt) assignedUser, emailParams);
    }

    protected String getTagHtmlA(String requestUrl, ProcInstance instance) {
        if (requestUrl == null) return "";
        MetaClass metaClass = metadata.getClass(instance.getEntityName());

        Collection<MetaProperty> properties = metaClass.getProperties();
        String property = null;
        for (MetaProperty metaProperty : properties) {
            if (metaProperty.getName().equals("requestNumber")) property = "requestNumber";
            else if (metaProperty.getName().equals("code")) property = "code";

            if (property != null) break;
        }

        if (property == null) return "";
        Entity entity = commonService.getEntity(metaClass.getJavaClass(), instance.getEntity().getEntityId(), View.LOCAL);
        return "<a href=\"" + requestUrl + "\" target=\"_blank\">%s " + entity.getValue(property) + "</a>";
    }

    //todo нужно переносить на commonService
    @Nullable
    protected <T extends StandardEntity> T reloadById(Class<T> tClass, UUID entityId, String viewName) {
        T entity = metadata.create(tClass);
        entity.setId(entityId);
        try (Transaction tx = persistence.getTransaction()) {
            entity = persistence.getEntityManager().reload(entity, viewName != null ? viewName : View.MINIMAL);
            tx.commit();
        }
        return entity;
    }

    //todo нужно переносить на commonService
    @Nullable
    protected <T extends StandardEntity> T findById(Class<T> tClass, UUID entityId, String viewName) {
        return findById(tClass, entityId, viewRepository.getView(tClass, viewName));
    }

    //todo нужно переносить на commonService
    @Nullable
    protected <T extends StandardEntity> T findById(Class<T> tClass, UUID entityId, View view) {
        T entity;
        try (Transaction tx = persistence.getTransaction()) {
            EntityManager em = persistence.getEntityManager();
            entity = view != null ? em.find(tClass, entityId, view) : em.find(tClass, entityId);
            tx.commit();
        }
        return entity;
    }

    protected Map<String, Object> getParams(Map<String, Object> params, String templateCode, UUID entityId, UUID bpmProcInstanceId) {
        UserExt startedByUser = bpmUtils.getCreatedBy(bpmProcInstanceId, "userExt.edit");
        UserExt lastUser = bpmUtils.getActiveTaskUser(bpmProcInstanceId, "userExt.edit");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        PersonGroupExt personGroupExt;

        EntityManager em = persistence.getEntityManager();

        ProcTask procTask = bpmUtils.getProcTaskList(bpmProcInstanceId, "procTask-frame-extended")
                .stream().filter(p -> !PersistenceHelper.isNew(p)).max(Comparator.comparing(ProcTask::getStartDate)).orElse(null);

        ProcActor procActor = procTask != null && procTask.getProcActor() != null ? em.reload(procTask.getProcActor(), "procActor-edit") : null;
        UserExt nextApprover = procActor != null ? getNextApprover(procActor) : null;

        switch (templateCode) {
            case "absence.notify.initiator":
            case "absence.approver.task.notification.email":
            case "absence.approver.task.notification": {
                AbsenceRequest absenceRequest = reloadById(AbsenceRequest.class, entityId, "absenceRequest.view");

                params.putIfAbsent("item", absenceRequest);
                params.putIfAbsent("date", dateFormat.format(Objects.requireNonNull(absenceRequest).getDateFrom()));
                params.putIfAbsent("tableRu", createTableAbsence(absenceRequest, "ru", startedByUser));
                params.putIfAbsent("tableEn", createTableAbsence(absenceRequest, "en", startedByUser));
                break;
            }
            case "address.notify.initiator":
            case "address.approver.task.notification":
            case "address.approver.task.notification.email": {
                AddressRequest addressRequest = reloadById(AddressRequest.class, entityId, "addressRequest-view");
                params.putIfAbsent("item", addressRequest);
                params.put("tableRu", createTableAddress("ru", entityId));
                params.put("tableEn", createTableAddress("en", entityId));
                break;
            }
            case "personalData.notify.initiator":
            case "personalData.approver.task.notification":
            case "personalData.approver.task.notification.email": {
                PersonalDataRequest personalDataRequest = reloadById(PersonalDataRequest.class, entityId, "personalDataRequest-view");
                params.putIfAbsent("item", personalDataRequest);
                params.put("tableRu", createTablePersonalData("ru", entityId));
                params.put("tableEn", createTablePersonalData("en", entityId));
                break;
            }
            case "position.approver.task.emailNotification":
            case "position.notify.initiator":
            case "position.approve.notification":
            case "position.reject.notification":
                params.putIfAbsent("tableRu", createTableForPositionRequest(entityId, "ru"));
                params.putIfAbsent("tableEn", createTableForPositionRequest(entityId, "en"));
            case "position.approver.task.notification": {
                // старый уведомление
                PositionChangeRequest positionChangeRequest = reloadById(PositionChangeRequest.class, entityId, "positionChangeRequest.edit");
                String typeRu = "", typeEn = "";
                switch (Objects.requireNonNull(positionChangeRequest).getRequestType()) {
                    case NEW: {
                        typeRu = "новая ШЕ";
                        typeEn = "New position";
                        break;
                    }
                    case CHANGE: {
                        typeRu = "изменение ШЕ";
                        typeEn = "Change position";
                        break;
                    }
                    case CLOSE: {
                        typeRu = "закрытие ШЕ";
                        typeEn = "Close position";
                        break;
                    }
                }
                params.putIfAbsent("typeRu", typeRu);
                params.putIfAbsent("typeEn", typeEn);
                params.putIfAbsent("item", positionChangeRequest);
                break;
            }
            case "salaryRequest.notify.initiator":
            case "assignment.notify.initiator":
            case "assignmentSalary.notify.initiator":
            case "temporaryTranslation.notify.initiator":
            case "salaryRequest.template.email":
            case "salaryRequest.template.notification":
            case "assignment.approver.task.notification":
            case "assignment.approver.task.emailnotification":
            case "assignmentSalary.approver.task.notification":
            case "assignmentSalary.approver.task.emailnotification":
            case "temporaryTranslation.approver.task.notification":
            case "temporaryTranslation.approver.task.emailnotification":
                // старый уведомление
            case "salaryRequest.reject":
            case "salaryRequest.approve":
            case "assignment.reject.notification":
            case "assignment.approve.notification":
            case "assignmentSalary.reject.notification":
            case "assignmentSalary.approve.notification":
            case "temporaryTranslation.approve.notification":
            case "temporaryTranslation.reject.notification": {

                String note = "";
                String reasonRu = "";
                String reasonEn = "";
                Class entityClass = getClassByTemplate(templateCode);
                Date startDate = CommonUtils.getSystemDate();
                Entity entity = null;

                if (Objects.requireNonNull(entityClass).equals(AssignmentRequest.class)) {

                    AssignmentRequest assignmentRequest = reloadById(AssignmentRequest.class, entityId, "assignmentRequest-view");
                    note = Objects.requireNonNull(assignmentRequest).getNote() != null ? assignmentRequest.getNote() : "";
                    startDate = assignmentRequest.getDateFrom();
                    entity = assignmentRequest;

                } else if (entityClass.equals(AssignmentSalaryRequest.class)) {

                    AssignmentSalaryRequest assignmentSalaryRequest = reloadById(AssignmentSalaryRequest.class, entityId, "assignmentSalaryRequest-view");
                    note = Objects.requireNonNull(assignmentSalaryRequest).getNote() != null ? assignmentSalaryRequest.getNote() : "";
                    reasonRu = assignmentSalaryRequest.getReason().getLangValue1();
                    reasonEn = assignmentSalaryRequest.getReason().getLangValue3();
                    startDate = assignmentSalaryRequest.getDateFrom();
                    entity = assignmentSalaryRequest;

                } else if (entityClass.equals(SalaryRequest.class)) {

                    SalaryRequest salaryRequest = reloadById(SalaryRequest.class, entityId, "salary-notification.view");
                    note = Objects.requireNonNull(salaryRequest).getNote() != null ? salaryRequest.getNote() : "";
                    reasonRu = salaryRequest.getReason().getLangValue1();
                    reasonEn = salaryRequest.getReason().getLangValue3();
                    startDate = salaryRequest.getStartDate();
                    entity = salaryRequest;

                } else if (entityClass.equals(TemporaryTranslationRequest.class)) {

                    TemporaryTranslationRequest translationRequest = reloadById(TemporaryTranslationRequest.class, entityId, "temporaryTranslationRequest-view");
                    note = Objects.requireNonNull(translationRequest).getNote() != null ? translationRequest.getNote() : "";
                    startDate = translationRequest.getStartDate();
                    entity = translationRequest;

                }

                personGroupExt = assignmentSalaryService.getPersonGroupByRequestId(entityId, "personGroup.browse", entityClass);

                OrganizationGroupExt organizationGroupExt = commonService.getEntity(OrganizationGroupExt.class,
                        "select e.organizationGroup from base$AssignmentExt e " +
                                " where e.personGroup.id = :id and e.primaryFlag ='TRUE' " +
                                " and :date between e.startDate and e.endDate " +
                                " and e.assignmentStatus.code <> 'TERMINATED' ",
                        ParamsMap.of("id", personGroupExt.getId(), "date", startDate),
                        "organizationGroupExt.for.attestation.lookup");
                JobGroup jobGroup = commonService.getEntity(JobGroup.class,
                        "select e.jobGroup from base$AssignmentExt e " +
                                " where e.personGroup.id = :id and e.primaryFlag ='TRUE' " +
                                " and :date between e.startDate and e.endDate " +
                                " and e.assignmentStatus.code <> 'TERMINATED' ",
                        ParamsMap.of("id", personGroupExt.getId(), "date", startDate),
                        "jobGroup-name-Ru-En");


                params.put("note", note);
                params.put("reasonRu", reasonRu != null ? reasonRu : "");
                params.put("reasonEn", reasonEn != null ? reasonEn : "");
                params.put("orgRu", getOrganizationName(organizationGroupExt, startDate, "ru"));
                params.put("orgEn", getOrganizationName(organizationGroupExt, startDate, "en"));
                params.put("jobRu", getJobName(jobGroup, startDate, "ru"));
                params.put("jobEn", getJobName(jobGroup, startDate, "en"));
                params.putIfAbsent("item", entity);
                params.putIfAbsent("effectiveDate", dateFormat.format(startDate));

                params.put("tableRu", createTable("ru", entityId, templateCode));
                params.put("tableEn", createTable("en", entityId, templateCode));

                params.putIfAbsent("personFioRu", personGroupExt.getPersonLatinFioWithEmployeeNumber("ru") != null ? personGroupExt.getPersonLatinFioWithEmployeeNumber("ru") : "");
                params.putIfAbsent("personFioEn", personGroupExt.getPersonLatinFioWithEmployeeNumber("en") != null ? personGroupExt.getPersonLatinFioWithEmployeeNumber("en") : "");
                break;
            }
        }

        params.putIfAbsent("statusRu", procTask != null ? nextApprover != null && !procTask.getOutcome().equals("reject")
                ? "На утверждении" : procTask.getOutcome().equals("reject") ? "Отклонено" : "Утверждено" : "Запущен");
        params.putIfAbsent("statusEn", procTask != null ? nextApprover != null && !procTask.getOutcome().equals("reject")
                ? "On approval" : procTask.getOutcome().equals("reject") ? "Cancelled" : "Approved" : "Launched");

        params.putIfAbsent("statusTypeRu", getTypeStatus(procTask, "ru"));
        params.putIfAbsent("statusTypeEn", getTypeStatus(procTask, "en"));
        params.putIfAbsent("initiatorFioRu", startedByUser != null ? startedByUser.getPersonGroup().getPerson().getFullName() : "");
        params.putIfAbsent("initiatorFioEn", startedByUser != null ? startedByUser.getPersonGroup() != null ? startedByUser.getPersonGroup().getPerson().getFullNameLatin() : startedByUser.getFullName() : "");
        params.putIfAbsent("lastUserRu", lastUser != null ? lastUser.getPersonGroup() != null ? lastUser.getPersonGroup().getPerson().getFullName() : lastUser.getFullName() : "");
        params.putIfAbsent("lastUserEn", lastUser != null ? lastUser.getPersonGroup() != null ? lastUser.getPersonGroup().getPerson().getFullNameLatin() : lastUser.getFullName() : "");
        params.putIfAbsent("approveTableRu", getTableTask("ru", bpmProcInstanceId, true));
        params.putIfAbsent("approveTableEn", getTableTask("en", bpmProcInstanceId, true));
        params.putIfAbsent("approveTableNonCommentRu", getTableTask("ru", bpmProcInstanceId, false));
        params.putIfAbsent("approveTableNonCommentEn", getTableTask("en", bpmProcInstanceId, false));

        params.putIfAbsent("nextApproverRu", nextApprover != null && (procTask == null || !procTask.getOutcome().equals("reject")) ? "Следующий утверждающий : " + nextApprover.getPersonGroup() != null ? nextApprover.getPersonGroup().getPerson().getFullName() : nextApprover.getFullName() : "");
        params.putIfAbsent("nextApproverEn", nextApprover != null && (procTask == null || !procTask.getOutcome().equals("reject")) ? "Next approver : " + nextApprover.getPersonGroup() != null ? nextApprover.getPersonGroup().getPerson().getFullNameLatin() : nextApprover.getFullName() : "");

        params.putIfAbsent("comment", procTask != null ? procTask.getComment() != null ? procTask.getComment() : " " : " ");
        return params;
    }


    @Nullable
    public UserExt getNextApprover(ProcActor procActor) {
        UserExt nextApprover;
        try (Transaction transaction = persistence.getTransaction()) {
            EntityManager em = persistence.getEntityManager();
            nextApprover = em.createQuery(" select u from bpm$ProcActor e " +
                    "   join tsadv$UserExt u on u.id = e.user.id " +
                    " where e.procInstance.id = :procInstanceId " +
                    "   and e.procRole.id = :procRoleId " +
                    "   and e.order > :order " +
                    " order by e.order ", UserExt.class)
                    .setParameter("procInstanceId", procActor.getProcInstance().getId())
                    .setParameter("procRoleId", procActor.getProcRole().getId())
                    .setParameter("order", procActor.getOrder())
                    .setViewName("userExt.edit")
                    .getFirstResult();
            if (nextApprover == null) {
                nextApprover = em.createQuery(" select u from bpm$ProcActor e " +
                        " join e.procRole r on r.order > :procRoleOrder " +
                        " join tsadv$UserExt u on u.id = e.user.id " +
                        " where e.procInstance.id = :procInstanceId " +
                        " order by r.order, e.order ", UserExt.class)
                        .setParameter("procInstanceId", procActor.getProcInstance().getId())
                        .setParameter("procRoleOrder", procActor.getProcRole().getOrder())
                        .setViewName("userExt.edit")
                        .getFirstResult();
            }
            if (nextApprover != null) {
                String idBpmUserSubstitution = bpmUserSubstitutionService.getCurrentBpmUserSubstitution(nextApprover, false);
                if (idBpmUserSubstitution != null) {
                    nextApprover = em.createQuery(" select u from tsadv$UserExt u " +
                            "where u.id = :userId", UserExt.class)
                            .setParameter("userId", UUID.fromString(idBpmUserSubstitution))
                            .setViewName("userExt.edit")
                            .getFirstResult();
                }
            }
            transaction.commit();
        }
        return nextApprover;
    }


    protected String getTypeStatus(ProcTask procTask, String lang) {
        if (procTask == null || procTask.getOutcome() == null) return "";
        switch (procTask.getOutcome()) {
            case "reassign": {
                return !lang.equals("en") ? "переназначил(а)" : "reassigned";
            }
            case "approve": {
                return !lang.equals("en") ? "утвердил(а)" : "approved";
            }
            case "reject": {
                return !lang.equals("en") ? "отклонил(а)" : "rejected";
            }
        }
        return "";
    }

    protected String createTableAbsence(AbsenceRequest absenceRequest, String lang, UserExt startedByUser) {
        StrBuilder strBuilder = new StrBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        boolean isRussian = !lang.equals("en");
        strBuilder.append(getStyle());
        strBuilder.append("<table>")
                .append("<tr  class=\"tableHeader\" >")
                .append("<td>").append("</td>")
                .append("<td>").append(isRussian ? "Сведение об отсутствии" : "Information about the absence").append("</td>")
                .append("</tr>")
                .append("<tr class=\"color\" >")
                .append("<td>").append(isRussian ? "Работник" : "Employee").append("</td>")
                .append("<td>").append(startedByUser != null ? startedByUser.getPersonGroup() != null ? lang.equals("ru") ? startedByUser.getPersonGroup().getPerson().getFullName() : startedByUser.getPersonGroup().getPerson().getFullNameLatin() : startedByUser.getFullName() : "").append("</td>")
                .append("</tr>")
                .append("<tr>")
                .append("<td>").append(isRussian ? "Тип отсутствия" : "Type of absence").append("</td>")
                .append("<td>").append(isRussian ? absenceRequest.getType().getLangValue1() : absenceRequest.getType().getLangValue3()).append("</td>")
                .append("</tr>")
                .append("<tr class=\"color\" >")
                .append("<td>").append(isRussian ? "Дата с" : "Date from").append("</td>")
                .append("<td>").append(dateFormat.format(absenceRequest.getDateFrom())).append("</td>")
                .append("</tr>")
                .append("<tr>")
                .append("<td>").append(isRussian ? "Дата по" : "Date to").append("</td>")
                .append("<td>").append(dateFormat.format(absenceRequest.getDateTo())).append("</td>")
                .append("</tr>")
                .append("<tr class=\"color\" >")
                .append("<td>").append(isRussian ? "Дни" : "Days").append("</td>")
                .append("<td>").append(absenceRequest.getAbsenceDays()).append("</td>")
                .append("</tr>")
                .append("<tr>")
                .append("<td>").append(isRussian ? "Комментарий" : "Comment").append("</td>")
                .append("<td>").append(Optional.ofNullable(absenceRequest.getComment()).orElse("")).append("</td>")
                .append("</tr>");
        strBuilder.append("</table>");
        return strBuilder.toString();
    }

    protected String getStyle() {
        return "<style> table {\n" +
                "    font-family: arial, serif;\n" +
                "    border-collapse: collapse;\n" +
                "    widht: 100%;\n" +
                "}\n" +
                ".tableHeader { background: #b9b9b9;}\n" +
                ".color { background: #eeeeee;\n}\n" +
                ".center { text-align: center; }\n" +
                "\n" +
                "td, td {\n" +
                "    border: 1px solid #dddddd;\n" +
                "    text-align: left;\n" +
                "    padding: 8px;\n" +
                "} </style>";
    }

    protected Class<? extends StandardEntity> getClassByTemplate(String templateCode) {
        if (templateCode.matches("^assignment\\.(.+)")) {
            return AssignmentRequest.class;
        } else if (templateCode.matches("^assignmentSalary\\.(.+)")) {
            return AssignmentSalaryRequest.class;
        } else if (templateCode.matches("^salaryRequest\\.(.+)")) {
            return SalaryRequest.class;
        } else if (templateCode.matches("^temporaryTranslation\\.(.+)")) {
            return TemporaryTranslationRequest.class;
        }
        return null;
    }

    protected String getTableTask(String lang, UUID bpmProcInstanceId, boolean comment) {
        StrBuilder strBuilder = new StrBuilder();
        strBuilder.setNullText("");
        boolean isRussian = StringUtils.isBlank(lang) || !lang.equals("en");
        strBuilder.append(getStyle())
                .append(" <table style=\"width:100%\"> ")
                .append("<tr  class=\"tableHeader\" >")
                .append("<td>").append(isRussian ? "Роль" : "Role").append("</td>")
                .append("<td>").append(isRussian ? "Утверждающий" : "Approver").append("</td>")
                .append("<td>").append(isRussian ? "Дата начало" : "Start date").append("</td>")
                .append("<td>").append(isRussian ? "Дата окончания" : "End date").append("</td>")
                .append("<td>").append(isRussian ? "Статус" : "Status").append("</td>");
        if (comment) {
            strBuilder.append("<td>").append(isRussian ? "Комментарий" : "Comment").append("</td>");
        }
        strBuilder.append("</tr>");
        List<ProcTask> taskList = bpmUtils.getProcTaskList(bpmProcInstanceId, "procTask-frame-extended");
        taskList.add(0, bpmUtils.createInitiatorTask(reloadById(ProcInstance.class, bpmProcInstanceId, "procInstance-browse")));
        for (int i = 0; i < taskList.size(); i++) {
            createTaskLine(strBuilder, taskList.get(i), lang, i % 2 == 0 ? " class=\"color\" " : "", comment);
        }
        getEndTable(strBuilder);
        return taskList.isEmpty() ? "" : strBuilder.toString();
    }

    protected StrBuilder createTaskLine(StrBuilder strBuilder, ProcTask task, String lang, String trClass, boolean comment) {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        ProcActor procActor = task.getProcActor();
        Locale locale = Locale.forLanguageTag(lang);
        strBuilder.append("<tr ").append(trClass).append(" >")
                .append("<td>")
                .append(task.getActProcessDefinitionId() == null
                        ? messages.getMessage(ProcInstanceV.class, Optional.ofNullable(task.getName()).orElse(""), locale)
                        : processMessagesService.findMessage(task.getActProcessDefinitionId(), procActor.getProcRole().getCode(), locale))
                .append("</td>").append("<td>")
                .append(((UserExt) procActor.getUser()).getFullName()).append(" (").append(procActor.getUser().getLogin()).append(')')
                .append("</td>").append("<td>")
                .append(dateFormat.format(task.getStartDate()))
                .append("</td>").append("<td>")
                .append(task.getEndDate() != null ? dateFormat.format(task.getEndDate()) : "")
                .append("</td>").append("<td>")
                .append(task.getActProcessDefinitionId() != null
                        ? processMessagesService.findMessage(task.getActProcessDefinitionId(),
                        task.getActTaskDefinitionKey() + "." + task.getOutcome(),
                        locale)
                        : messages.getMessage(ProcInstanceV.class, Optional.ofNullable(task.getOutcome()).orElse(""), locale))
                .append("</td>");
        if (comment) {
            strBuilder.append("<td>").append(task.getComment()).append("</td>");
        }
        strBuilder.append("</tr>");
        return strBuilder;
    }

    protected String createTable(String lang, UUID entityId, String templateCode) {
        StrBuilder strBuilder = new StrBuilder();
        getStartTable(strBuilder, lang);

        //      only for temporarily saving data
        AssignmentSalaryRequest assignmentSalaryRequest = metadata.create(AssignmentSalaryRequest.class);

        assignmentSalaryRequest = setChangedValueToAssignmentSalaryRequest(assignmentSalaryRequest, entityId, templateCode);
        templateCode = templateCode.toLowerCase();


        TemporaryTranslationRequest temporaryTranslationRequest = null;

        if (templateCode.contains("temporarytranslation")) {
            temporaryTranslationRequest = commonService.getEntity(TemporaryTranslationRequest.class,
                    "select e from tsadv$TemporaryTranslationRequest e where e.id = :id ",
                    ParamsMap.of("id", entityId), "temporaryTranslationRequest-view");
            assignmentSalaryRequest.setPersonGroup(temporaryTranslationRequest.getPersonGroup());
            assignmentSalaryRequest.setDateFrom(temporaryTranslationRequest.getStartDate());
            assignmentSalaryRequest.setGradeGroup(temporaryTranslationRequest.getGradeGroup());
            assignmentSalaryRequest.setPositionGroup(temporaryTranslationRequest.getPositionGroup());
            assignmentSalaryRequest.setJobGroup(temporaryTranslationRequest.getJobGroup());
            assignmentSalaryRequest.setOrganizationGroup(temporaryTranslationRequest.getOrganizationGroup());
            assignmentSalaryRequest.setSubstitutedEmployee(temporaryTranslationRequest.getSubstitutedEmployee());
            assignmentSalaryRequest.setNote("tsadv$TemporaryTranslationRequest");
        }

        AssignmentExt assignmentExt = employeeService.getAssignmentExt(assignmentSalaryRequest.getPersonGroup().getId(), assignmentSalaryRequest.getDateFrom(), "assignmentExt.bpm.view");
        Salary salary = employeeService.getSalary(assignmentExt.getGroup(), assignmentSalaryRequest.getDateFrom(), "salary.view");//"salary-for-salary-request.view";

        if (templateCode.contains("assignment") || templateCode.contains("temporarytranslation")) {

            getTableTransfer(strBuilder,
                    lang,
                    assignmentExt,
                    assignmentSalaryRequest);
        }
        if (templateCode.contains("salary")) {
            getTableSalary(strBuilder,
                    lang,
                    salary,
                    assignmentSalaryRequest);
        }
        if (templateCode.contains("temporarytranslation")) {
            getTableTemporaryTransfer(strBuilder,
                    lang,
                    assignmentExt,
                    temporaryTranslationRequest);
        }


        getEndTable(strBuilder);

        return strBuilder.toString();
    }

    protected void getEndTable(StrBuilder strBuilderEn) {
        strBuilderEn.append("</table>");
    }

    protected StrBuilder getTableTemporaryTransfer(StrBuilder strBuilder, String lang, AssignmentExt assignmentExt, TemporaryTranslationRequest temporaryTranslationRequest) {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        strBuilder.append("<tr> <td>")
                .append(lang.equalsIgnoreCase("ru") ? "Дата окончания временного перевода" : "End for temporary transfer")
                .append("</td> <td>")
                //.append(assignmentExt.getEndDate())
                .append("</td> <td>")
                .append(temporaryTranslationRequest != null
                        ? temporaryTranslationRequest.getEndDate() != null
                        ? dateFormat.format(temporaryTranslationRequest.getEndDate()) : "" : "")
                .append("</td> </tr> ")
                .append("<tr> <td>")
                .append(lang.equalsIgnoreCase("ru") ? "Причина врем.перевода" : "Reason for temporary transfer")
                .append("</td> <td>")
                //.append("")
                .append("</td> <td>")
                .append(temporaryTranslationRequest != null
                        ? temporaryTranslationRequest.getReason() != null
                        ? temporaryTranslationRequest.getReason() : "" : "")
                .append("</td> <td>")
                .append("</td> </tr> ");
        return strBuilder;
    }

    protected AssignmentSalaryRequest setChangedValueToAssignmentSalaryRequest(AssignmentSalaryRequest assignmentSalaryRequest, UUID entityId, String templateCode) {
        if (templateCode.contains("assignment.")) {
            AssignmentRequest assignmentRequest = commonService.getEntity(
                    AssignmentRequest.class,
                    "select e from tsadv$AssignmentRequest e where e.id = :id",
                    ParamsMap.of("id", entityId), "assignmentRequest-view");
            assignmentSalaryRequest.setDateFrom(assignmentRequest.getDateFrom());
            assignmentSalaryRequest.setOrganizationGroup(assignmentRequest.getOrganizationGroup());
            assignmentSalaryRequest.setJobGroup(assignmentRequest.getJobGroup());
            assignmentSalaryRequest.setPositionGroup(assignmentRequest.getPositionGroup());
            assignmentSalaryRequest.setGradeGroup(assignmentRequest.getGradeGroup());
            assignmentSalaryRequest.setPersonGroup(assignmentRequest.getPersonGroup());
            assignmentSalaryRequest.setSubstitutedEmployee(assignmentRequest.getSubstitutedEmployee());
        } else if (templateCode.contains("assignmentSalary.")) {
            AssignmentSalaryRequest newAssignmentSalaryRequest = commonService.getEntity(
                    AssignmentSalaryRequest.class,
                    "select e from tsadv$AssignmentSalaryRequest e where e.id = :id",
                    ParamsMap.of("id", entityId), "assignmentSalaryRequest-view");
            assignmentSalaryRequest.setDateFrom(newAssignmentSalaryRequest.getDateFrom());
            assignmentSalaryRequest.setOrganizationGroup(newAssignmentSalaryRequest.getOrganizationGroup());
            assignmentSalaryRequest.setJobGroup(newAssignmentSalaryRequest.getJobGroup());
            assignmentSalaryRequest.setPositionGroup(newAssignmentSalaryRequest.getPositionGroup());
            assignmentSalaryRequest.setGradeGroup(newAssignmentSalaryRequest.getGradeGroup());
            assignmentSalaryRequest.setAmount(newAssignmentSalaryRequest.getAmount());
            assignmentSalaryRequest.setChangePercent(newAssignmentSalaryRequest.getChangePercent());
            assignmentSalaryRequest.setReason(newAssignmentSalaryRequest.getReason());
            assignmentSalaryRequest.setType(newAssignmentSalaryRequest.getType());
            assignmentSalaryRequest.setPersonGroup(newAssignmentSalaryRequest.getPersonGroup());
            assignmentSalaryRequest.setSubstitutedEmployee(newAssignmentSalaryRequest.getSubstitutedEmployee());
        } else if (templateCode.contains("salaryRequest.")) {
            SalaryRequest salaryRequest = commonService.getEntity(SalaryRequest.class,
                    "select e from tsadv$SalaryRequest e where e.id = :id",
                    ParamsMap.of("id", entityId), "salary-notification.view");
            assignmentSalaryRequest.setAmount(salaryRequest.getAmount());
            assignmentSalaryRequest.setChangePercent(salaryRequest.getChangePercent());
            assignmentSalaryRequest.setReason(salaryRequest.getReason());
            assignmentSalaryRequest.setType(salaryRequest.getType());
            assignmentSalaryRequest.setDateFrom(salaryRequest.getStartDate());
            assignmentSalaryRequest.setPersonGroup(salaryRequest.getAssignmentGroup().getAssignment().getPersonGroup());
        }
        return assignmentSalaryRequest;
    }

    protected String getImgTag(boolean isEmptyTag) {
        return isEmptyTag ? ""
                : "<img class=\"v-icon\" src=\"http://kchrdev.air-astana.net/aa/VAADIN/themes/base/images/check.png?v=2019_08_20_18_03\">";
    }

    protected StrBuilder getStartTable(StrBuilder strBuilder, String lang) {
        return strBuilder.append(" <style> table { font-family: arial; border-collapse: collapse; widht: 100%;} ")
                .append("td, td{ border: 1px solid #dddddd; text-align: left; padding: 8px;} </style> ")
                .append(" <table style=\"width:100%\"> <tr><td></td><td>")
                .append(lang.equalsIgnoreCase("ru") ? "Текущий" : "Current")
                .append("</td> <td>")
                .append(lang.equalsIgnoreCase("ru") ? "Предложенный" : "Proposed")
                .append("</td><td></td> </tr> ");
    }

    protected StrBuilder getTableSalary(StrBuilder stringBuilder, String lang, Salary salary, AssignmentSalaryRequest assignmentSalaryRequest) {
        if (assignmentSalaryRequest.getPositionGroup() == null)                     //is salary request
            getTableSalary1(stringBuilder, lang, salary, assignmentSalaryRequest);
        return getTableSalary2(stringBuilder, lang, salary, assignmentSalaryRequest);
    }

    protected StrBuilder getTableSalary1(StrBuilder stringBuilder, String lang, Salary salary, AssignmentSalaryRequest assignmentSalaryRequest) {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        return stringBuilder.append("  <tr> <td> ")
                .append(lang.equalsIgnoreCase("ru") ? "Дата назначения зарплаты" : "Salary assignment date")
                .append("</td> <td> ")
                .append(salary != null ? dateFormat.format(salary.getStartDate()) : "")
                .append("</td> <td> ")
                .append(dateFormat.format(assignmentSalaryRequest.getDateFrom()))
                .append("</td> <td> ")
                .append("</td> </tr> ");
    }

    protected StrBuilder getTableSalary2(StrBuilder stringBuilder, String lang, Salary salary, AssignmentSalaryRequest assignmentSalaryRequest) {
        char inf = 0x221E;
        return stringBuilder.append(" <tr> <td>")
                .append(lang.equalsIgnoreCase("ru") ? "Оклад" : "Salary")
                .append("</td> <td>")
                .append(salary != null ? salary.getType() != null ?
                        messages.getMessage(salary.getType(), Locale.forLanguageTag(lang)) : "" : "")
                .append("</td> <td>")
                .append(messages.getMessage(assignmentSalaryRequest.getType(), Locale.forLanguageTag(lang)))
                .append("</td> <td>")
                .append(getImgTag(Objects.equals(salary != null ? salary.getType() : null, assignmentSalaryRequest.getType())))
                .append("</td> </tr> ")
                .append("<tr> <td>")
                .append(lang.equalsIgnoreCase("ru") ? "Ставка" : "Amount")
                .append("</td> <td>")
                .append(salary != null ? salary.getAmount() != null ? salary.getAmount().toString() : "" : "")
                .append("</td> <td>")
                .append(assignmentSalaryRequest.getAmount())
                .append("</td> <td>")
                .append(getImgTag(Objects.equals(salary != null ? salary.getAmount() : null, assignmentSalaryRequest.getAmount())))
                .append("</td> </tr> ")
                .append("<tr> <td>")
                .append(lang.equalsIgnoreCase("ru") ? "% изменения" : "% change")
                .append("</td> <td>")
                .append("</td> <td>")
                .append((salary != null && salary.getAmount() != null) ? ((assignmentSalaryRequest.getAmount() - salary.getAmount()) * 100 / salary.getAmount()) : inf)
                .append("</td> <td>")
                .append("</td> </tr> ");
    }

    protected StrBuilder getTableTransfer(StrBuilder stringBuilder,
                                          String lang,
                                          AssignmentExt assignmentExt,
                                          AssignmentSalaryRequest assignmentSalaryRequest) {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        if (assignmentSalaryRequest.getNote() != null && !assignmentSalaryRequest.getNote().equalsIgnoreCase("tsadv$TemporaryTranslationRequest")) {
            stringBuilder.append("  <tr> <td> ")
                    .append(lang.equalsIgnoreCase("ru") ? "Дата назначения" : "Date of assignment")
                    .append("</td> <td> ")
                    .append(assignmentExt != null ? dateFormat.format(assignmentExt.getStartDate()) : "")
                    .append("</td> <td> ")
                    .append(dateFormat.format(assignmentSalaryRequest.getDateFrom()))
                    .append("</td> <td>  </td> </tr> ");
        }
        stringBuilder.append("<tr> <td>")
                .append(lang.equalsIgnoreCase("ru") ? "Дата с" : "Date from")
                .append("</td> <td>")
                .append(assignmentExt != null ? dateFormat.format(assignmentExt.getStartDate()) : "")
                .append("</td> <td>")
                .append(dateFormat.format(assignmentSalaryRequest.getDateFrom()))
                .append("</td> <td>")
                .append(getImgTag(Objects.equals(assignmentExt != null ? assignmentExt.getStartDate() : null, assignmentSalaryRequest.getDateFrom())))
                .append("</td> </tr> ")
                .append(" <tr> <td>")
                .append(lang.equalsIgnoreCase("ru") ? "Замещаемый сотрудник" : "Substitute employee")
                .append("</td> <td>")
                .append(assignmentExt != null
                        ? assignmentExt.getSubstituteEmployee() != null
                        ? assignmentExt.getSubstituteEmployee().getPerson() != null
                        ? assignmentExt.getSubstituteEmployee().getPerson().getFullNameLatin(lang) : "" : "" : "")
                .append("</td> <td>")
                .append(assignmentSalaryRequest.getSubstitutedEmployee() != null
                        ? assignmentSalaryRequest.getSubstitutedEmployee().getPerson() != null
                        ? assignmentSalaryRequest.getSubstitutedEmployee().getPerson().getFullNameLatin(lang) : "" : "")
                .append("</td> <td>")
                .append(getImgTag(
                        Objects.equals(assignmentExt != null ? assignmentExt.getSubstituteEmployee() : null,
                                assignmentSalaryRequest.getSubstitutedEmployee())))
                .append("</td> </tr> ")
                .append(" <tr> <td>")
                .append(lang.equalsIgnoreCase("ru") ? "Отдел" : "Organization")
                .append("</td> <td>")
                .append(assignmentExt != null ? getOrganizationName(assignmentExt.getOrganizationGroup(), assignmentExt.getStartDate(), lang) : "")
                .append("</td> <td>")
                .append(getOrganizationName(assignmentSalaryRequest.getOrganizationGroup(), assignmentSalaryRequest.getDateFrom(), lang))
                .append("</td> <td>")
                .append(getImgTag(Objects.equals(assignmentExt != null ? assignmentExt.getOrganizationGroup() : null, assignmentSalaryRequest.getOrganizationGroup())))
                .append("</td> </tr> ")
                .append(" <tr> <td>")
                /*.append(lang.equalsIgnoreCase("ru") ? "Должность" : "Job")
                .append("</td> <td>")
                .append(assignmentExt != null ? getJobName(assignmentExt.getJobGroup(), assignmentExt.getStartDate(), lang) : "")
                .append("</td> <td>")
                .append(getJobName(assignmentSalaryRequest.getJobGroup(), assignmentSalaryRequest.getDateFrom(), lang))
                .append("</td> <td>")
                .append(getImgTag(Objects.equals(assignmentExt != null ? assignmentExt.getJobGroup() : null, assignmentSalaryRequest.getJobGroup())))
                .append("</td> </tr> ")
                .append("<tr> <td>")*/
                .append(lang.equalsIgnoreCase("ru") ? "Название штатной единицы" : "Position")
                .append("</td> <td>")
                .append(assignmentExt != null ? getPositionName(assignmentExt.getPositionGroup(), assignmentExt.getStartDate(), lang) : "")
                .append("</td> <td>")
                .append(getPositionName(assignmentSalaryRequest.getPositionGroup(), assignmentSalaryRequest.getDateFrom(), lang))
                .append("</td> <td>")
                .append(getImgTag(Objects.equals(assignmentExt != null ? assignmentExt.getPositionGroup() : null, assignmentSalaryRequest.getPositionGroup())))
                .append("</td> </tr> ")
                .append("<tr> <td>")
                .append(lang.equalsIgnoreCase("ru") ? "Грейд" : "Grade")
                .append("</td> <td>")
                .append(assignmentExt != null ? getGradeName(assignmentExt.getGradeGroup(), assignmentExt.getStartDate()) : "")
                .append("</td> <td>")
                .append(getGradeName(assignmentSalaryRequest.getGradeGroup(), assignmentSalaryRequest.getDateFrom()))
                .append("</td> <td>")
                .append(getImgTag(Objects.equals(assignmentExt != null ? assignmentExt.getGradeGroup() : null, assignmentSalaryRequest.getGradeGroup())))
                .append("</td> </tr> ");
        return stringBuilder;
    }

    protected String getPositionRequestTypeName(PositionChangeRequestType positionChangeRequestType, String lang) {
        switch (positionChangeRequestType) {
            case CLOSE:
                return !lang.equals("en") ? "закрытие ШЕ" : "Close position";
            case NEW:
                return !lang.equals("en") ? "новая ШЕ" : "New position";
            case CHANGE:
                return !lang.equals("en") ? "изменение ШЕ" : "Change position";
            default:
                return "";
        }
    }

    protected String createTableForPositionRequest(PositionChangeRequest positionChangeRequest, String lang) {
        StrBuilder strBuilder = new StrBuilder();

        boolean isEn = lang.equals("en");
        OrganizationExt organizationExt = positionChangeRequest.getOrganizationGroup() != null
                ? positionChangeRequest.getOrganizationGroup().getOrganization() : null;
        PositionExt parentPositionExt = positionChangeRequest.getParentPositionGroup() != null
                ? positionChangeRequest.getParentPositionGroup().getPositionInDate(positionChangeRequest.getEffectiveDate())
                : null;
        strBuilder.append(getStyle());
        strBuilder.append("<table> ");
        getPositionStartTableTr(strBuilder, positionChangeRequest, lang).append(" <tr class=\"color\"> <td>")
                .append(isEn ? "Department/Division" : "Структурное подразделение")
                .append("</td> <td>")
                .append(organizationExt != null ? isEn ? organizationExt.getOrganizationNameLang3() : organizationExt.getOrganizationNameLang1() : "")
                .append("</td> </tr> <tr> <td>")
                .append(isEn ? "Job name ru" : "Должность (рус)")
                .append("</td> <td>")
                .append(positionChangeRequest.getJobNameLang1())
                .append("</td> </tr> <tr class=\"color\"> <td>")
                .append(isEn ? "Job name kz" : "Должность (каз)")
                .append("</td> <td>")
                .append(positionChangeRequest.getJobNameLang2())
                .append("</td> </tr> <tr> <td>")
                .append(isEn ? "Job name en" : "Должность (англ)")
                .append("</td> <td>")
                .append(positionChangeRequest.getJobNameLang3())
                .append("</td> </tr> <tr class=\"color\"> <td>")
                .append(isEn ? "Location" : "База")
                .append("</td> <td>")
                .append(positionChangeRequest.getLocation())
                .append("</td> </tr> <tr> <td>")
                .append(isEn ? "Grade" : "Грейд")
                .append("</td> <td>")
                .append(positionChangeRequest.getGradeGroup() != null ? positionChangeRequest.getGradeGroup().getGrade().getGradeName() : "")
                .append("</td> </tr> <tr class=\"color\"> <td>")
                .append(isEn ? "Cost center" : "Центр-затрат")
                .append("</td> <td>")
                .append(positionChangeRequest.getCostCenter() != null ? positionChangeRequest.getCostCenter().getCode() : "")
                .append("</td> </tr> <tr> <td>")
                .append(isEn ? "Number of positions" : "ЭПЗ")
                .append("</td> <td>")
                .append(positionChangeRequest.getFte() != null ? positionChangeRequest.getFte() : "")
                .append("</td> </tr> <tr class=\"color\"> <td>")
                .append(isEn ? "Parent position" : "Руководящая ШЕ")
                .append("</td> <td>")
                .append(parentPositionExt != null ? isEn ? parentPositionExt.getPositionFullNameLang3() : parentPositionExt.getPositionFullNameLang1() : "")
                .append("</td> </tr> <tr> <td>")
                .append(isEn ? "Comments" : "Комментарий")
                .append("</td> <td>")
                .append(positionChangeRequest.getComments() != null ? positionChangeRequest.getComments() : "")
                .append("</td> </tr>");
        getEndTable(strBuilder);
        return strBuilder.toString();
    }

    protected StrBuilder getPositionStartTableTr(StrBuilder strBuilder, PositionChangeRequest positionChangeRequest, String lang) {
        boolean isEn = lang.equals("en");
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        strBuilder.append("<tr  class=\"color\"><td>")
                .append(isEn ? "Request number" : "Номер заявки")
                .append("</td> <td>")
                .append(positionChangeRequest.getRequestNumber())
                .append("</td> </tr> <tr> <td>")
                .append(isEn ? "Request date" : "Дата заявки")
                .append("</td> <td>")
                .append((positionChangeRequest.getRequestDate() != null ? dateFormat.format(positionChangeRequest.getRequestDate()) : " "))
                .append("</td> </tr> <tr class=\"color\"> <td>")
                .append(isEn ? "Effective from" : "Дата вступления изменения в силу")
                .append("</td> <td>")
                .append(positionChangeRequest.getEffectiveDate() != null ? dateFormat.format(positionChangeRequest.getEffectiveDate()) : "")
                .append("</td> </tr> <tr> <td>")
                .append(isEn ? "Change type" : "Тип заявки")
                .append("</td> <td>")
                .append(getPositionRequestTypeName(positionChangeRequest.getRequestType(), lang))
                .append("</td> </tr>");
        return strBuilder;
    }

    protected String createTableForPositionRequestTypeChange(PositionChangeRequest positionChangeRequest, String lang) {

        boolean isEn = lang.equals("en");
        StrBuilder strBuilder = new StrBuilder(getStyle());

        PositionExt positionExt = positionChangeRequest.getPositionGroup().getPositionInDate(positionChangeRequest.getEffectiveDate());
        positionExt = dataManager.reload(positionExt, "position.edit");
        PositionGroupExt newParentPositionGroup =
                hierarchyService.getParentPosition(positionChangeRequest.getPositionGroup(), "positionGroup.change.request");
        PositionExt newParentPosition = newParentPositionGroup != null
                ? newParentPositionGroup.getPositionInDate(positionChangeRequest.getEffectiveDate())
                : null;
        PositionExt parentPosition = positionChangeRequest.getParentPositionGroup() != null
                ? positionChangeRequest.getParentPositionGroup().getPositionInDate(positionChangeRequest.getEffectiveDate())
                : null;

        OrganizationExt organizationExt = positionChangeRequest.getOrganizationGroup() != null ? positionChangeRequest.getOrganizationGroup().getOrganization() : null;
        OrganizationExt organizationExt1 = positionExt.getOrganizationGroupExt() != null
                ? positionExt.getOrganizationGroupExt().getOrganizationInDate(positionChangeRequest.getEffectiveDate())
                : null;
        Job job = positionExt.getJobGroup() != null
                ? positionExt.getJobGroup().getJobInDate(positionChangeRequest.getEffectiveDate())
                : null;

        strBuilder.append("<table> ");
        getPositionStartTableTr(strBuilder, positionChangeRequest, lang).append(" <tr class=\"color\"> <td>")
                .append(isEn ? "Position" : "Штатная единица")
                .append("</td> <td>")
                .append(isEn ? positionExt.getPositionFullNameLang3() : positionExt.getPositionFullNameLang1())
                .append("</td> </tr></table>");

        getStartTable(strBuilder, lang);
        strBuilder.append("<tr class=\"color\"><td>")
                .append(isEn ? "Department" : "Организация")
                .append("</td> <td>")
                .append(organizationExt1 != null ? isEn ? organizationExt1.getOrganizationNameLang3() : organizationExt1.getOrganizationNameLang1() : "")
                .append("</td> <td>")
                .append(organizationExt != null ? isEn ? organizationExt.getOrganizationNameLang3() : organizationExt.getOrganizationNameLang1() : "")
                .append("</td> <td>")
                .append(getImgTag(Objects.equals(positionExt.getOrganizationGroupExt(), positionChangeRequest.getOrganizationGroup())))
                .append("</td> </tr> <tr> <td>")
                .append(isEn ? "Job name ru" : "Должность (рус)")
                .append("</td> <td>")
                .append(job != null ? job.getJobNameLang1() : "")
                .append("</td> <td>")
                .append(positionChangeRequest.getJobNameLang1())
                .append("</td> <td>")
                .append(getImgTag(Objects.equals(job != null ? job.getJobNameLang1() : null, positionChangeRequest.getJobNameLang1())))
                .append("</td> </tr> <tr class=\"color\"> <td>")
                .append(isEn ? "Job name kaz" : "Должность (каз)")
                .append("</td> <td>")
                .append(job != null ? job.getJobNameLang2() : "")
                .append("</td> <td>")
                .append(positionChangeRequest.getJobNameLang2())
                .append("</td> <td>")
                .append(getImgTag(Objects.equals(job != null ? job.getJobNameLang2() : null, positionChangeRequest.getJobNameLang2())))
                .append("</td> </tr> <tr> <td>")
                .append(isEn ? "Job name en" : "Должность (англ)")
                .append("</td> <td>")
                .append(job != null ? job.getJobNameLang3() : "")
                .append("</td> <td>")
                .append(positionChangeRequest.getJobNameLang3())
                .append("</td> <td>")
                .append(getImgTag(Objects.equals(job != null ? job.getJobNameLang3() : null, positionChangeRequest.getJobNameLang3())))
                .append("</td> </tr> <tr class=\"color\"> <td>")
                .append(isEn ? "Location" : "База")
                .append("</td> <td>")
                .append(positionExt.getBaza())
                .append("</td> <td>")
                .append(positionChangeRequest.getLocation())
                .append("</td> <td>")
                .append(getImgTag(Objects.equals(positionExt.getBaza(), positionChangeRequest.getLocation())))
                .append("</td> </tr> <tr> <td>")
                .append(isEn ? "Cost center" : "Центр-затрат")
                .append("</td> <td>")
                .append(positionExt.getCostCenter() != null ? positionExt.getCostCenter().getCode() : "")
                .append("</td> <td>")
                .append(positionChangeRequest.getCostCenter() != null ? positionChangeRequest.getCostCenter().getCode() : "")
                .append("</td> <td>")
                .append(getImgTag(Objects.equals(positionExt.getCostCenter(), positionChangeRequest.getCostCenter())))
                .append("</td> </tr> <tr class=\"color\"> <td>")
                .append(isEn ? "Grade" : "Грейд")
                .append("</td> <td>")
                .append(positionExt.getGradeGroup() != null ? positionExt.getGradeGroup().getGrade().getGradeName() : "")
                .append("</td> <td>")
                .append(positionChangeRequest.getGradeGroup() != null ? positionChangeRequest.getGradeGroup().getGrade().getGradeName() : "")
                .append("</td> <td>")
                .append(getImgTag(Objects.equals(positionExt.getGradeGroup(), positionChangeRequest.getGradeGroup())))
                .append("</td> </tr> <tr> <td>")
                .append(isEn ? "Number of positions" : "ЭПЗ")
                .append("</td> <td>")
                .append(positionExt.getFte())
                .append("</td> <td>")
                .append(positionChangeRequest.getFte() != null ? positionChangeRequest.getFte() : "")
                .append("</td> <td>")
                .append(getImgTag(Objects.equals(positionExt.getFte(), positionChangeRequest.getFte().doubleValue())))
                .append("</td> </tr> <tr class=\"color\"> <td>")
                .append(isEn ? "Parent position" : "Руководящая ШЕ")
                .append("</td> <td>")
                .append(newParentPosition != null ? isEn ? newParentPosition.getPositionFullNameLang3() : newParentPosition.getPositionFullNameLang1() : "")
                .append("</td> <td>")
                .append(parentPosition != null ? isEn ? parentPosition.getPositionFullNameLang3() : parentPosition.getPositionFullNameLang1() : "")
                .append("</td> <td>")
                .append(getImgTag(Objects.equals(newParentPositionGroup, positionChangeRequest.getParentPositionGroup())))
                .append("</td> </tr>");

        getEndTable(strBuilder);

        return strBuilder.toString();
    }

    protected String createTableForPositionRequest(UUID entityId, String lang) {
        PositionChangeRequest positionChangeRequest = commonService.getEntity(PositionChangeRequest.class,
                "select e from tsadv$PositionChangeRequest e where e.id = :id ",
                ParamsMap.of("id", entityId),
                "positionChangeRequest.edit");

        switch (positionChangeRequest.getRequestType()) {
            case NEW:
            case CLOSE: {
                return createTableForPositionRequest(positionChangeRequest, lang);
            }
            case CHANGE: {
                return createTableForPositionRequestTypeChange(positionChangeRequest, lang);
            }
            default:
                return "";
        }
    }

    @Override
    public void start(UUID entityId, String entityName, UUID bpmProcInstanceId) {
        DicRequestStatus requestStatus = commonService.getEntity(DicRequestStatus.class, "APPROVING");
        updateEntityStatus(entityName, entityId, requestStatus);
    }

    @Override
    public void reject(UUID entityId, String entityName, UUID bpmProcInstanceId) {
        DicRequestStatus requestStatus = commonService.getEntity(DicRequestStatus.class, "CANCELLED");
        updateEntityStatus(entityName, entityId, requestStatus);
    }

    protected void updateEntityStatus(String entityName, UUID entityId, DicRequestStatus status) {
        MetaClass metaClass = metadata.getClass(entityName);
        EntityManager em = persistence.getEntityManager();
        Class<? extends Entity<UUID>> aClass = Objects.requireNonNull(metaClass).getJavaClass();

        View view = new View(aClass).addProperty("status");
        Entity entity = em.find(aClass, entityId, view);
        entity.setValue("status", status);
        em.merge(entity);
    }

    @Override
    public void sendNotifyToInitiator(UUID entityId, UUID bpmProcInstanceId, String emailTemplateCode, String template) {
        UserExt userExt = bpmUtils.getCreatedBy(bpmProcInstanceId, "userExt.edit");
        if (userExt != null)
            sendUserNotification(userExt, userSessionSource.getUserSession().getUser(), entityId, bpmProcInstanceId,
                    "NOTIFICATION", emailTemplateCode, template);
    }

    @Override
    public void approve(UUID entityId, String entityName, UUID bpmProcInstanceId) {
        DicRequestStatus requestStatus = commonService.getEntity(DicRequestStatus.class, "APPROVED");
        updateEntityStatus(entityName, entityId, requestStatus);
    }

    @Override
    public void approve(UUID entityId, String entityName, UUID bpmProcInstanceId, String emailTemplateCode, String notificationTemplateCode) {
        EntityManager em = persistence.getEntityManager();
        DicRequestStatus requestStatus = commonService.getEntity(DicRequestStatus.class, "APPROVED");
        if (requestStatus == null) {
            throw new ItemNotFoundException("Request status with code 'APPROVED' is not found");
        }
        UserExt userExt = null;                             // User that send request

        switch (entityName) {
            case "tsadv$AbsenceRequest": {
                Absence absence = approveAbsence(em, requestStatus, entityId, bpmProcInstanceId);
                //absenceBalanceService.recountBalanceDays(absence.getPersonGroup(), absence, null, null, null);
                String sql = " select bal.refresh_person_balance('" + absence.getPersonGroup().getId().toString() + "')";
                callStoredFunctionService.execCallSqlFunction(sql);
                break;
            }
            case "tsadv$PersonalDataRequest": {
                userExt = approvePersonalDataRequest(entityId, em, requestStatus);
                break;
            }
            case "tsadv$AddressRequest": {
                userExt = approveAddressRequest(entityId, em, requestStatus);
                break;
            }
            case "tsadv$SalaryRequest": {
                userExt = approveSalary(entityId, em, bpmProcInstanceId, requestStatus);
                break;
            }
            case "tsadv$AssignmentRequest": {
                userExt = approveAssignment(entityId, em, bpmProcInstanceId, requestStatus);
                break;
            }
            case "tsadv$AssignmentSalaryRequest": {
                userExt = approveAssignmentSalary(entityId, em, bpmProcInstanceId, requestStatus);
                break;
            }
            case "tsadv$PositionChangeRequest": {
                userExt = approvePosition(entityId, em, bpmProcInstanceId, requestStatus);
                break;
            }
            case "tsadv$TemporaryTranslationRequest": {
                userExt = approveTemporaryTranslation(entityId, em, bpmProcInstanceId, requestStatus);
                break;
            }
        }
        if (userExt != null && StringUtils.isNotBlank(emailTemplateCode) && StringUtils.isNotBlank(notificationTemplateCode)) {
            sendUserNotification(userExt, bpmUtils.getActiveTaskUser(bpmProcInstanceId, "userExt.edit"), entityId, bpmProcInstanceId,
                    "NOTIFICATION", emailTemplateCode, notificationTemplateCode);
        }

    }

    private void callRefreshPersonBalanceSqlFunction() {

    }

    @Override
    @Deprecated
    public void reject(UUID entityId, String entityName, UUID bpmProcInstanceId, String emailTemplateCode, String notificationTemplateCode) {
        DicRequestStatus requestStatus = commonService.getEntity(DicRequestStatus.class, "CANCELLED");
        if (requestStatus == null) {
            throw new ItemNotFoundException("Request status with code 'CANCELED' is not found");
        }

        updateEntityStatus(entityName, entityId, requestStatus);

        UserExt userExt = bpmUtils.getCreatedBy(bpmProcInstanceId, "userExtPersonGroup.edit");

        if (userExt != null)
            sendUserNotification(userExt, bpmUtils.getActiveTaskUser(bpmProcInstanceId, "userExt.edit"),
                    entityId, bpmProcInstanceId, "NOTIFICATION", emailTemplateCode, notificationTemplateCode);
    }

    protected UserExt approveTemporaryTranslation(UUID entityId, EntityManager em, UUID bpmProcInstanceId, DicRequestStatus requestStatus) {
        TemporaryTranslationRequest translationRequest = em.find(TemporaryTranslationRequest.class, entityId, "temporaryTranslationRequest-view");
        Objects.requireNonNull(translationRequest).setStatus(requestStatus);

        AssignmentExt assignmentExt = employeeService.getAssignmentExt(translationRequest.getPersonGroup().getId(), translationRequest.getStartDate(), "assignmentExt.bpm.view");
        assignmentExt.setWriteHistory(true);

        if (!assignmentSalaryService.isLastAssignment(translationRequest.getPersonGroup().getId(), DateUtils.addDays(translationRequest.getStartDate(), -1))) {
            throw new ItemNotFoundException("Assignment has changed in the future");
        }

        PositionGroupExt actualPositionGroup = translationRequest.getActualPositionGroup() != null
                ? em.reloadNN(translationRequest.getActualPositionGroup(), "positionGroup.change.request")
                : null;

        PositionExt actualPosition = actualPositionGroup != null
                ? actualPositionGroup.getPositionInDate(translationRequest.getStartDate())
                : null;

        OrganizationGroupExt newOrg = actualPosition != null
                ? actualPosition.getOrganizationGroupExt()
                : translationRequest.getOrganizationGroup();
        PositionGroupExt newPos = actualPositionGroup != null
                ? actualPositionGroup
                : translationRequest.getPositionGroup();

        assignmentExt.setStartDate(translationRequest.getStartDate());
        assignmentExt.setTemporaryEndDate(translationRequest.getEndDate());

        assignmentExt.setGradeGroup(actualPosition != null
                ? actualPosition.getGradeGroup()
                : translationRequest.getGradeGroup());
        assignmentExt.setJobGroup(actualPosition != null
                ? actualPosition.getJobGroup()
                : translationRequest.getPositionGroup().getPositionInDate(translationRequest.getStartDate()).getJobGroup());
        assignmentExt.setOrganizationGroup(newOrg);
        assignmentExt.setPositionGroup(newPos);
        assignmentExt.setSubstituteEmployee(translationRequest.getSubstitutedEmployee());

        assignmentExt.setCostCenter(newPos.getPositionInDate(translationRequest.getStartDate()) != null
                && newPos.getPositionInDate(translationRequest.getStartDate()).getCostCenter() != null
                ? newPos.getPositionInDate(translationRequest.getStartDate()).getCostCenter()
                : newOrg.getOrganizationInDate(translationRequest.getStartDate()) != null
                ? newOrg.getOrganizationInDate(translationRequest.getStartDate()).getCostCenter()
                : null);

        em.merge(assignmentExt);

        UserExt userExt = bpmUtils.getCreatedBy(bpmProcInstanceId, "userExt.edit");
        return userExt;
    }

    protected UserExt approveAssignmentSalary(UUID entityId, EntityManager em, UUID bpmProcInstanceId, DicRequestStatus requestStatus) {
        AssignmentSalaryRequest assignmentSalaryRequest = em.find(AssignmentSalaryRequest.class, entityId, "assignmentSalaryRequest-view");
        Objects.requireNonNull(assignmentSalaryRequest).setStatus(requestStatus);

        AssignmentExt assignmentExt = employeeService.getAssignmentExt(assignmentSalaryRequest.getPersonGroup().getId(),
                assignmentSalaryRequest.getDateFrom(),
                "assignmentExt.bpm.view");

        Salary oldSalary = employeeService.getSalary(getAssignment(assignmentSalaryRequest.getPersonGroup().getAssignments(),
                assignmentSalaryRequest.getDateFrom()).getGroup(),
                assignmentSalaryRequest.getDateFrom(),
                "salary.view");

        assignmentExt.setSubstituteEmployee(assignmentSalaryRequest.getSubstitutedEmployee());

        assignmentSave(em,
                assignmentSalaryRequest.getActualPositionGroup(),
                assignmentExt,
                assignmentSalaryRequest.getDateFrom(),
                assignmentSalaryRequest.getGradeGroup(),
                assignmentSalaryRequest.getPositionGroup(),
                assignmentSalaryRequest.getOrganizationGroup(),
                assignmentSalaryRequest.getJobGroup());

        salarySave(em,
                assignmentExt.getGroup(),
                oldSalary,
                assignmentSalaryRequest.getDateFrom(),
                assignmentSalaryRequest.getType(),
                assignmentSalaryRequest.getAmount(),
                assignmentSalaryRequest.getCurrency(),
                assignmentSalaryRequest.getNetGross(),
                assignmentSalaryRequest.getReason());

        em.merge(assignmentSalaryRequest);

        UserExt userExt = bpmUtils.getCreatedBy(bpmProcInstanceId, "userExt.edit");
        return userExt;
    }

    protected AssignmentExt getAssignment(List<AssignmentExt> assignments, Date dateFrom) {
        return assignments.stream()
                .filter(a -> a.getDeleteTs() == null
                        && !dateFrom.before(a.getStartDate())
                        && !dateFrom.after(a.getEndDate())
                        && a.getPrimaryFlag()
                        && a.getAssignmentStatus().getCode().matches("ACTIVE|SUSPENDED")
                )
                .findFirst()
                .orElse(null);
    }

    //DO not remove - overridden in AA
    protected void handleNewEntity(Entity<UUID> entity, UUID bpmProcInstanceId) {

    }

    protected Salary salarySave(EntityManager em, AssignmentGroupExt assignmentGroupExt, Salary oldSalary, Date date, SalaryType type, Double amount, DicCurrency currency, GrossNet netGross, DicSalaryChangeReason reason) {
        if (!assignmentSalaryService.isLastSalary(assignmentGroupExt.getId(), date)) {
            throw new ItemNotFoundException("Salary has been changed in the future");
        }
        boolean salaryCreated = oldSalary == null || !date.equals(oldSalary.getStartDate());
        Salary salary = initSalary(salaryCreated, oldSalary);

        salary.setType(type);
        salary.setAmount(amount);
        salary.setCurrency(currency);
        salary.setStartDate(date);
        salary.setEndDate(CommonUtils.getEndOfTime());
        salary.setAgreement(oldSalary != null ? oldSalary.getAgreement() : null);
        salary.setAssignmentGroup(assignmentGroupExt);
        salary.setNetGross(netGross);
        salary.setOrdAssignment(oldSalary != null ? oldSalary.getOrdAssignment() : null);
        salary.setReason(reason);
        salary.setOrderGroup(oldSalary != null ? oldSalary.getOrderGroup() : null);
        salary.setOrdAssignment(oldSalary != null ? oldSalary.getOrdAssignment() : null);

        if (salaryCreated && oldSalary != null) {
            oldSalary.setEndDate(DateUtils.addDays(salary.getStartDate(), -1));
            em.merge(oldSalary);
        }
        em.merge(salary);
        return salary;
    }

    protected Salary initSalary(boolean salaryCreated, Salary oldSalary) {
        return salaryCreated ? metadata.create(Salary.class) : oldSalary;
    }

    protected UserExt approveAssignment(UUID entityId, EntityManager em, UUID bpmProcInstanceId, DicRequestStatus requestStatus) {
        AssignmentRequest assignmentRequest = em.find(AssignmentRequest.class, entityId, "assignmentRequest-view");
        AssignmentExt assignmentExt = employeeService.getAssignmentExt(Objects.requireNonNull(assignmentRequest).getPersonGroup().getId(), assignmentRequest.getDateFrom(), "assignmentExt.bpm.view");

        assignmentExt.setSubstituteEmployee(assignmentRequest.getSubstitutedEmployee());
        assignmentSave(em,
                assignmentRequest.getActualPositionGroup(),
                assignmentExt,
                assignmentRequest.getDateFrom(),
                assignmentRequest.getGradeGroup(),
                assignmentRequest.getPositionGroup(),
                assignmentRequest.getOrganizationGroup(),
                assignmentRequest.getJobGroup());

        assignmentRequest.setStatus(requestStatus);
        em.merge(assignmentRequest);
        handleNewEntity(assignmentExt, bpmProcInstanceId);

        UserExt userExt = bpmUtils.getCreatedBy(bpmProcInstanceId, "userExt.edit");
        return userExt;
    }

    protected void assignmentSave(EntityManager em,
                                  PositionGroupExt actualPositionGroup,
                                  AssignmentExt assignmentExt,
                                  Date dateFrom,
                                  GradeGroup gradeGroup,
                                  PositionGroupExt positionGroupExt,
                                  OrganizationGroupExt organizationGroupExt,
                                  JobGroup jobGroup) {

        actualPositionGroup = actualPositionGroup != null
                ? em.reloadNN(actualPositionGroup, "positionGroup.change.request")
                : null;

        PositionExt actualPosition = actualPositionGroup != null
                ? actualPositionGroup.getPositionInDate(dateFrom)
                : null;

        assignmentSave(em,
                assignmentExt,
                dateFrom,
                actualPosition != null
                        ? actualPosition.getGradeGroup()
                        : gradeGroup,
                actualPositionGroup != null
                        ? actualPositionGroup
                        : positionGroupExt,
                actualPosition != null
                        ? actualPosition.getOrganizationGroupExt()
                        : organizationGroupExt,
                actualPosition != null
                        ? actualPosition.getJobGroup()
                        : positionGroupExt.getPositionInDate(dateFrom).getJobGroup());
    }

    protected void assignmentSave(EntityManager em,
                                  AssignmentExt assignmentExt,
                                  Date dateFrom,
                                  GradeGroup gradeGroup,
                                  PositionGroupExt positionGroupExt,
                                  OrganizationGroupExt organizationGroupExt,
                                  JobGroup jobGroup) {
        if (!assignmentSalaryService.isLastAssignment(assignmentExt.getPersonGroup().getId(), dateFrom)) {
            throw new ItemNotFoundException("Assignment has changed in the future");
        }

        assignmentExt.setWriteHistory(true);
        assignmentExt.setStartDate(dateFrom);
        assignmentExt.setEndDate(CommonUtils.getEndOfTime());
        assignmentExt.setGradeGroup(gradeGroup);
        assignmentExt.setPositionGroup(positionGroupExt);
        assignmentExt.setCostCenter(positionGroupExt.getPositionInDate(dateFrom) != null
                && positionGroupExt.getPositionInDate(dateFrom).getCostCenter() != null
                ? positionGroupExt.getPositionInDate(dateFrom).getCostCenter()
                : organizationGroupExt.getOrganizationInDate(dateFrom) != null
                && organizationGroupExt.getOrganizationInDate(dateFrom) != null
                ? organizationGroupExt.getOrganizationInDate(dateFrom).getCostCenter()
                : null);
        assignmentExt.setOrganizationGroup(organizationGroupExt);
        assignmentExt.setJobGroup(jobGroup);

        em.merge(assignmentExt);
    }

    @Override
    public void notifyManagersAboutAccessInf(Date date,
                                             PersonGroupExt personGroup,
                                             OrganizationGroupExt newOrganizationGroup,
                                             OrganizationGroupExt oldOrganizationGroup,
                                             PositionGroupExt newPositionGroup,
                                             PositionGroupExt oldPositionGroup,
                                             boolean isScheduler) {

        if (!needToNotifyManagersAboutAccessInf(date, newOrganizationGroup, oldOrganizationGroup, isScheduler)) return;

        OrganizationExt newOrganization = newOrganizationGroup != null ? newOrganizationGroup.getOrganizationInDate(date) : null;
        OrganizationExt oldOrganization = oldOrganizationGroup != null ? oldOrganizationGroup.getOrganizationInDate(date) : null;

        Set<UserExt> userExts = new HashSet<>();
        if (newPositionGroup != null)
            userExts.addAll(employeeService.recursiveFindManager(newPositionGroup.getId()));
        if (oldPositionGroup != null)
            userExts.addAll(employeeService.recursiveFindManager(oldPositionGroup.getId()));

        Map<String, Object> params = new HashMap<>();

        params.put("fioRu", personGroup.getPersonFioWithEmployeeNumber());
        params.put("fioEn", personGroup.getPersonLatinFioWithEmployeeNumber());
        params.put("newOrgRu", newOrganization != null ? newOrganization.getOrganizationNameLang1() : "");
        params.put("newOrgEn", newOrganization != null ? newOrganization.getOrganizationNameLang3() : "");
        params.put("oldOrgRu", oldOrganization != null ? oldOrganization.getOrganizationNameLang1() : "");
        params.put("oldOrgEn", oldOrganization != null ? oldOrganization.getOrganizationNameLang3() : "");

        userExts.forEach(userExt ->
                notificationService.sendNotification("notify.manager.access.inf",
                        userExt.getEmail(),
                        params,
                        userExt.getLanguage()));

    }

    @Override
    public void schedulerNotifyManagersAboutAccessInf() {
        List<AssignmentExt> list = commonService.getEntities(AssignmentExt.class,
                " select e from base$AssignmentExt e " +
                        " where e.startDate = :dateS or e.endDate = :dateE ",
                ParamsMap.of("dateE", CommonUtils.getSystemDate(),
                        "dateS", DateUtils.addDays(CommonUtils.getSystemDate(), 1)), "assignmentExt.bpm.view");
        Set<AssignmentGroupExt> groupExtSet = list.stream().map(AssignmentExt::getGroup).collect(Collectors.toSet());
        for (AssignmentGroupExt assignmentGroupExt : groupExtSet) {
            List<AssignmentExt> assignmentExts = list.stream().filter(assignmentExt -> assignmentExt.getGroup().equals(assignmentGroupExt))
                    .collect(Collectors.toList());
            if (assignmentExts.size() == 2) {
                assignmentExts.sort(Comparator.comparing(AbstractTimeBasedEntity::getStartDate));
                AssignmentExt newAssignmentExt = assignmentExts.get(1);
                AssignmentExt oldAssignmentExt = assignmentExts.get(0);
                notifyManagersAboutAccessInf(newAssignmentExt.getStartDate(),
                        newAssignmentExt.getPersonGroup(),
                        newAssignmentExt.getOrganizationGroup(),
                        oldAssignmentExt.getOrganizationGroup(),
                        newAssignmentExt.getPositionGroup(),
                        oldAssignmentExt.getPositionGroup(),
                        true);
            }
        }
    }

    public boolean needToNotifyManagersAboutAccessInf(Date date,
                                                      OrganizationGroupExt newOrganizationGroup,
                                                      OrganizationGroupExt oldOrganizationGroup,
                                                      boolean isScheduler) {
        return (isScheduler || !date.after(CommonUtils.getSystemDate()))
                && !newOrganizationGroup.equals(oldOrganizationGroup);
    }

    protected UserExt approvePosition(UUID entityId, EntityManager em, UUID bpmProcInstanceId, DicRequestStatus requestStatus) {
        PositionChangeRequest positionChangeRequest = em.find(PositionChangeRequest.class, entityId, "positionChangeRequest.edit");
        Objects.requireNonNull(positionChangeRequest).setStatus(requestStatus);
        em.persist(positionChangeRequest);

        UserExt userExt = bpmUtils.getCreatedBy(bpmProcInstanceId, "userExt.edit");
        return userExt;
    }

    protected UserExt approveSalary(UUID entityId, EntityManager em, UUID bpmProcInstanceId, DicRequestStatus requestStatus) {
        SalaryRequest salaryRequest = em.find(SalaryRequest.class, entityId, "salary-request.view");
        Salary oldSalary = employeeService.getSalary(Objects.requireNonNull(salaryRequest).getAssignmentGroup(), salaryRequest.getStartDate(), "salary.view");

        Salary salary = salarySave(em,
                salaryRequest.getAssignmentGroup(),
                oldSalary,
                salaryRequest.getStartDate(),
                salaryRequest.getType(),
                salaryRequest.getAmount(),
                salaryRequest.getCurrency(),
                salaryRequest.getNetGross(),
                salaryRequest.getReason()
        );

        salaryRequest.setStatus(requestStatus);
        em.merge(salaryRequest);
        handleNewEntity(salary, bpmProcInstanceId);

        UserExt userExt = bpmUtils.getCreatedBy(bpmProcInstanceId, "userExt.edit");
        return userExt;
    }

    protected UserExt approveAddressRequest(UUID entityId, EntityManager em, DicRequestStatus requestStatus) {
        AddressRequest addressRequest = em.find(AddressRequest.class, entityId, "addressRequest-view");

        if (Objects.requireNonNull(addressRequest).getBaseAddress() == null) {
            Address address = commonService.getEntity(Address.class, " select e from tsadv$Address e " +
                            " where :sysDate between e.startDate and e.endDate " +
                            "       and e.addressType.code = :code " +
                            "       and e.personGroup.id = :personGroup",
                    ParamsMap.of("sysDate", Objects.requireNonNull(addressRequest).getStartDate(),
                            "code", addressRequest.getAddressType().getCode(),
                            "personGroup", addressRequest.getPersonGroup().getId())
                    , "address.view");

            createAddress(address, addressRequest, em);
        } else {
            Address address = em.reloadNN(addressRequest.getBaseAddress(), "address.view");
            copyAddressRequest(address, addressRequest, false);
            em.merge(address);
        }
        addressRequest.setStatus(requestStatus);
        em.merge(addressRequest);

        return getUserByKey("personGroup.id", addressRequest.getPersonGroup().getId());
    }

    protected void createAddress(Address address, AddressRequest addressRequest, EntityManager em) {
        boolean isCreated = address == null;

        if (address != null && address.getStartDate() != null && !address.getStartDate().equals(addressRequest.getStartDate())) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(addressRequest.getStartDate());
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            address.setEndDate(calendar.getTime());
            em.merge(address);
            isCreated = true;
        }

        if (isCreated) {
            Address newAddress = metadata.create(Address.class);
            newAddress.setId(UUID.randomUUID());
            copyAddressRequest(newAddress, addressRequest, true);
            em.persist(newAddress);
        } else {
            copyAddressRequest(address, addressRequest, true);
            em.merge(address);
        }
    }

    protected Address copyAddressRequest(Address address, AddressRequest addressRequest, boolean changeDate) {
        address.setAddress(addressRequest.getAddress());
        address.setAddressType(addressRequest.getAddressType());
        address.setCountry(addressRequest.getCountry());
        if (addressRequest.getPostalCode() != null)
            address.setPostalCode(addressRequest.getPostalCode());
        if (addressRequest.getCity() != null)
            address.setCity(addressRequest.getCity());
        if (changeDate) {
            address.setStartDate(addressRequest.getStartDate());
            address.setEndDate(addressRequest.getEndDate());
        }
        address.setPersonGroup(addressRequest.getPersonGroup());
        return address;
    }

    protected Absence approveAbsence(EntityManager em, DicRequestStatus requestStatus, UUID entityId, UUID bpmProcInstanceId) {
        AbsenceRequest absenceRequest = em.find(AbsenceRequest.class, entityId, "absenceRequest.view");
        Absence absence = metadata.create(Absence.class);

        absence = setAbsenceFromAbsenceRequest(absence, absenceRequest);
        if (AppBeans.get(DatesService.class).getFullDaysCount(absenceRequest.getDateFrom(), absenceRequest.getDateTo()) >= 30
                && isAbsenceTypeLong(absenceRequest.getId())) {
            absence.setUseInBalance(true);
        }

        Objects.requireNonNull(absenceRequest).setStatus(requestStatus);

        em.merge(absenceRequest);
        em.persist(absence);

        return absence;
    }

    protected <T extends Absence> T setAbsenceFromAbsenceRequest(T absence, AbsenceRequest absenceRequest) {

        absence.setNotificationDate(absenceRequest.getRequestDate());
        absence.setAbsenceDays(absenceRequest.getAbsenceDays());
        absence.setAbsenceRequest(absenceRequest);
        absence.setPersonGroup(absenceRequest.getAssignmentGroup().getList().get(0).getPersonGroup());
        absence.setDateFrom(absenceRequest.getDateFrom());
        absence.setDateTo(absenceRequest.getDateTo());
        absence.setType(absenceRequest.getType());

        return absence;
    }

    protected UserExt approvePersonalDataRequest(UUID entityId, EntityManager em, DicRequestStatus requestStatus) {

        PersonalDataRequest personalDataRequest = em.find(PersonalDataRequest.class, entityId, "personalDataRequest-view");

        PersonExt personExt = Objects.requireNonNull(personalDataRequest).getPersonGroup().getPerson();
        // FIO
        personExt.setFirstName(personalDataRequest.getFirstName());
        personExt.setMiddleName(personalDataRequest.getMiddleName());
        personExt.setLastName(personalDataRequest.getLastName());
        // FIO Latin
        personExt.setFirstNameLatin(personalDataRequest.getFirstNameLatin());
        personExt.setMiddleNameLatin(personalDataRequest.getMiddleNameLatin());
        personExt.setLastNameLatin(personalDataRequest.getLastNameLatin());
        // birth_date and marital_status
        personExt.setDateOfBirth(personalDataRequest.getDateOfBirth());
        personExt.setMaritalStatus(personalDataRequest.getMaritalStatus());

        personExt.setWriteHistory(true);

        em.merge(personExt);

        personalDataRequest.setStatus(requestStatus);
        em.merge(personalDataRequest);

        return getUserByKey("personGroup.id", personExt.getGroup().getId());
    }

    protected SendingNotification sendNotification(String notificationCode, UserExt user, Map<String, Object> templateParams, EmailAttachment... emailAttachments) {
        if (templateParams != null)
            templateParams.put("user", user);
        return notificationSender.sendParametrizedNotification(notificationCode, user, templateParams, emailAttachments);
    }

    protected UserExt getUserByKey(String keyName, Object keyValue) {
        return commonService.getEntity(UserExt.class, String.format("select e from base$UserExt e " +
                "join tsadv$UserExtPersonGroup u on u.userExt.id = e.id " +
                "where u.%s = :keyValue", keyName), Collections.singletonMap("keyValue", keyValue), "user.edit");
    }

    protected String getGradeName(GradeGroup gradeGroup, Date date) {
        return Optional.ofNullable(gradeGroup)
                .map(g -> g.getGradeInDate(date))
                .map(Grade::getGradeName)
                .orElse("");
    }

    protected String getPositionName(PositionGroupExt positionGroup, Date date, String lang) {
        PositionExt positionExt = positionGroup.getPositionInDate(date);
        if (positionExt != null) {
            if (lang.equalsIgnoreCase("ru") && positionExt.getPositionFullNameLang1() != null) {
                return positionExt.getPositionFullNameLang1();
            } else if (lang.equalsIgnoreCase("en") && positionExt.getPositionFullNameLang3() != null) {
                return positionExt.getPositionFullNameLang3();
            }
        }
        return "";
    }

    protected String getJobName(JobGroup jobGroup, Date date, String lang) {
        if (jobGroup == null) return "";
        Job job = jobGroup.getJobInDate(date);
        if (job != null) {
            if (lang.equalsIgnoreCase("ru") && job.getJobNameLang1() != null) {
                return job.getJobNameLang1();
            } else if (lang.equalsIgnoreCase("en") && job.getJobNameLang3() != null) {
                return job.getJobNameLang3();
            }
        }
        return "";
    }

    protected String getOrganizationName(OrganizationGroupExt organizationGroup, Date date, String lang) {
        if (organizationGroup == null) return "";
        OrganizationExt organizationExt = organizationGroup.getOrganizationInDate(date);
        if (organizationExt != null) {
            if (lang.equalsIgnoreCase("ru") && organizationExt.getOrganizationNameLang1() != null) {
                return organizationExt.getOrganizationNameLang1();
            } else if (lang.equalsIgnoreCase("en") && organizationExt.getOrganizationNameLang3() != null) {
                return organizationGroup.getOrganization().getOrganizationNameLang3();
            }
        }
        return "";
    }

    /**
     * Create procActor with order = currentProcActor.getOrder() + 1
     *
     * @param currentProcTask  Current ProcTask
     * @param currentProcActor Current ProcActor
     * @param items            Collection of procActors (all list ProcActors where current ProcRole(in currentProcActor) equals list.get(i).procRole )
     * @param userExt          Reassign user
     */
    public void commitReassignProcActor(ProcTask currentProcTask, ProcActor currentProcActor, Collection<ProcActor> items, UserExt userExt) {
        List<ProcActor> actorList = items.stream().filter(procActor -> procActor.getProcRole().equals(currentProcActor.getProcRole()))
                .sorted(Comparator.comparing(ProcActor::getOrder))
                .collect(Collectors.toList());
        List<ProcActor> newList = new ArrayList<>();
        ProcActor newProcActor = null;

        for (int i = 0; i < actorList.size(); i++) {
            ProcActor procActor = actorList.get(i);
            if (procActor.getOrder() > currentProcActor.getOrder()) {
                procActor.setOrder(procActor.getOrder() + 1);
            }
            if (procActor.equals(currentProcActor)) {
                newProcActor = createNextProcActor(currentProcActor, userExt);
                newList.add(newProcActor);
            }
            newList.add(procActor);
        }

        int order = Objects.requireNonNull(newProcActor).getOrder() - 2;

        try (Transaction transaction = persistence.getTransaction()) {
            EntityManager em = persistence.getEntityManager();
            for (ProcActor actor : newList) {
                em.merge(actor);
            }
            runtimeService.setVariableLocal(currentProcTask.getActExecutionId(), "nrOfInstances", newList.size());
            runtimeService.setVariableLocal(currentProcTask.getActExecutionId(), "loopCounter", order);
            runtimeService.setVariableLocal(currentProcTask.getActExecutionId(), "nrOfCompletedInstances", order);
            transaction.commit();
        }
    }

    protected ProcActor createNextProcActor(ProcActor procActor, UserExt userExt) {
        ProcActor newProcActor = metadata.create(ProcActor.class);
        newProcActor.setProcRole(procActor.getProcRole());
        newProcActor.setProcInstance(procActor.getProcInstance());
        newProcActor.setOrder(procActor.getOrder() + 1);
        newProcActor.setUser(userExt);
        return newProcActor;
    }


    /**
     * @param date   date
     * @param status status
     * @return date - 1 if  {@link DicRequestStatus#getCode()} equals one of the APPROVED|CANCELLED|CANCELED else date
     */
    @Override
    public Date getDate(Date date, DicRequestStatus status) {
        if (date == null) {
            date = new Date();
        }
        if (status == null) {
            throw new ItemNotFoundException("Request of status is null!");
        }
        if (status.getCode().toUpperCase().matches("APPROVED|CANCELLED|CANCELED")) {
            date = DateUtils.addDays(date, -1);
        }
        return date;
    }

    //called in bpm model
    @Override
    public boolean isAbsenceTypeLong(UUID absenceRequestId) {
        return !commonService.getEntities(CategoryAttributeValue.class,
                "select e from sys$CategoryAttributeValue e " +
                        " where e.code = 'ABSENCETYPELONG' " +
                        "    and e.booleanValue = true " +
                        "    and e.entity.entityId in " +
                        " ( select a.type.id from tsadv$AbsenceRequest a where a.id = :eId ) ",
                ParamsMap.of("eId", absenceRequestId),
                View.MINIMAL)
                .isEmpty();
    }

    @Override
    public boolean isExpiredTask(ActivityTask activityTask) {
        return Boolean.TRUE.equals(activityTask.getIsExpiredTask());
    }

    @Override
    public void schedulerExpiredTask() {
        List<UserExt> userExtList = commonService.getEntities(UserExt.class,
                "select distinct u from tsadv$ActivityTask e" +
                        " join e.activity.assignedUser u " +
                        " where e.isExpiredTask = true ",
                ParamsMap.empty(), View.LOCAL);
        for (UserExt userExt : userExtList) {
            if (StringUtils.isBlank(userExt.getEmail())) continue;

            List<ActivityTask> tasks = commonService.getEntities(ActivityTask.class,
                    "select e from tsadv$ActivityTask e " +
                            " where e.isExpiredTask = true " +
                            " and e.activity.assignedUser.id = :assignedUserId",
                    ParamsMap.of("assignedUserId", userExt.getId()),
                    "activityTask.view");

            if (tasks.isEmpty()) continue;
            StrBuilder tableRu = new StrBuilder(getStyle()),
                    tableEn = new StrBuilder(getStyle());
            tableRu.append("<table>").append("<tr  class=\"tableHeader\" ><td>Тип заявки</td><td>Описание</td><td>Ссылка на заявку</td></tr>");
            tableEn.append("<table>").append("<tr  class=\"tableHeader\" ><td>Request type</td><td>Details</td><td>Request link</td></tr>");

            for (int i = 0; i < tasks.size(); i++) {
                addToTableExpiredTasks(tasks.get(i), tableRu, tableEn, i);
            }

            tableRu.append("</table>");
            tableEn.append("</table>");
            notifyAboutExpiredTasks(userExt, ParamsMap.of("tableRu", tableRu, "tableEn", tableEn));
        }
    }

    protected void addToTableExpiredTasks(ActivityTask activityTask, StrBuilder tableRu, StrBuilder tableEn, int i) {
        Activity activity = activityTask.getActivity();
        String tagA = "<a href=\"" + getRequestUrl(activity, null) + "\" target=\"_blank\">" + activityTask.getOrderCode() + "</a>";
        tableRu.append("<tr").append(i % 2 == 0 ? "" : " class=\"color\"").append(">")
                .append("<td>").append(activityTask.getProcessRu()).append("</td>")
                .append("<td>").append(activityTask.getDetailRu()).append("</td>")
                .append("<td class=\"center\">").append(tagA)
                .append("</td>").append("</tr>");
        tableEn.append("<tr").append(i % 2 == 0 ? "" : " class=\"color\"").append(">")
                .append("<td>").append(activityTask.getProcessEn()).append("</td>")
                .append("<td>").append(activityTask.getDetailEn()).append("</td>")
                .append("<td class=\"center\">").append(tagA)

                .append("</td>").append("</tr>");
    }

    private void notifyAboutExpiredTasks(User user, Map<String, Object> params) {
        notificationService.sendNotification("bpm.request.notify.expired.tasks",
                user.getEmail(),
                params,
                user.getLanguage());
    }

    @Nullable
    @Override
    public String getRequestUrl(Activity activity, @Nullable ProcInstance procInstance) {
        if (!"NOTIFICATION".equals(activity.getType().getCode())) {
            ActivityType activityType = activity.getType();
            if (activityType.getWindowProperty() != null) {
                return globalConfig.getWebAppUrl() + "/open?screen=" + activity.getType().getWindowProperty().getScreenName() +
                        "&item=" + activityType.getWindowProperty().getEntityName() + "-" + activity.getReferenceId() +
                        "&params=activity:" + activity.getId();
            }
        }
        if (procInstance != null) {
            String screenId = getEditorScreenId(procInstance.getEntityName(), procInstance.getEntity().getEntityId());
            return globalConfig.getWebAppUrl() + "/open?screen=" + screenId +
                    "&item=" + procInstance.getEntityName() + "-" + procInstance.getEntity().getEntityId();
        }
        return null;
    }

    protected String getEditorScreenId(String entityName, UUID entityId) {
        List<WindowProperty> windowProperties = commonService.getEntities(WindowProperty.class,
                "select e from uactivity$WindowProperty e",
                ParamsMap.empty(), View.LOCAL);

        MetaClass aClass = metadata.getClass(entityName);
        if (Objects.equals(aClass, metadata.getClass(PositionChangeRequest.class))) {
            PositionChangeRequest positionChange = commonService.getEntity(PositionChangeRequest.class, entityId, View.LOCAL);
            if (PositionChangeRequestType.CHANGE.equals(positionChange.getRequestType())) {
                return "tsadv$PositionChangeRequestTypeChange.edit";
            } else {
                return "tsadv$PositionChangeRequest.edit";
            }
        }

        for (WindowProperty windowProperty : windowProperties) {
            if (aClass.equals(metadata.getClass(windowProperty.getEntityName()))) return windowProperty.getScreenName();
        }

        return null;
    }

    @Nonnull
    @Override
    public Activity bpmRequestAskAnswerNotification(@Nonnull BpmRequestMessage bpmRequestMessage) {
        UserExt userExt = bpmRequestMessage.getAssignedUser();
        if (StringUtils.isBlank(userExt.getEmail())) throw new ItemNotFoundException("email /not.found");
        Map<String, Object> param = new HashMap<>();
        param.put("requestNumber", bpmRequestMessage.getEntityRequestNumber());
        param.put("fioRu", bpmRequestMessage.getAssignedBy().getPersonGroup() != null ? bpmRequestMessage.getAssignedBy().getPersonGroup().getPerson().getFullName() : bpmRequestMessage.getAssignedBy().getFullName());
        param.put("fioEn", bpmRequestMessage.getAssignedBy().getPersonGroup() != null ? bpmRequestMessage.getAssignedBy().getPersonGroup().getPerson().getFullNameLatin() : bpmRequestMessage.getAssignedBy().getFullName());
        param.put("askAnswerTextRu", bpmRequestMessage.getParent() == null ? "запросил информацию по заявке" : "ответил на запрос информации по заявке");
        param.put("askAnswerTextEn", bpmRequestMessage.getParent() == null ? "requested information on request" : "responded to a request for information on request");
        param.put("typeRu", bpmRequestMessage.getParent() == null ? "задал" : "ответил на");
        param.put("typeEn", bpmRequestMessage.getParent() == null ? "asked a" : "answered the");
        param.put("message", bpmRequestMessage.getMessage());

        param.put("tableRu", getTable(bpmRequestMessage.getScreenName(), bpmRequestMessage.getEntityId(), bpmRequestMessage.getProcInstance().getId(), "ru"));
        param.put("tableEn", getTable(bpmRequestMessage.getScreenName(), bpmRequestMessage.getEntityId(), bpmRequestMessage.getProcInstance().getId(), "en"));

        String url = getRequestUrl(bpmRequestMessage);

        param.put("urlRu", String.format(url, "Открыть заявку"));
        param.put("urlEn", String.format(url, "Open request"));

        if (bpmRequestMessage.getParent() != null && bpmRequestMessage.getParent().getActivity() != null) {
            Activity activity = bpmRequestMessage.getParent().getActivity();
            if (!StatusEnum.done.equals(activity.getStatus())) {
                activity.setStatus(StatusEnum.done);
                try (Transaction transaction = persistence.getTransaction()) {
                    persistence.getEntityManager().merge(activity);
                    transaction.commit();
                }
            }
        }

        boolean hasParent = Optional.ofNullable(bpmRequestMessage.getLvl()).orElse(0) != 0;
        String activityTypeCode = !hasParent
                ? getActivityCodeFromTableName(bpmRequestMessage.getEntityId(), bpmRequestMessage.getEntityName())
                : "NOTIFICATION";

        Activity activity = activityService.createActivity(
                bpmRequestMessage.getAssignedUser(),
                bpmRequestMessage.getAssignedBy(),
                commonService.getEntity(ActivityType.class, activityTypeCode),
                StatusEnum.active,
                "description",
                null,
                new Date(),
                null,
                null,
                bpmRequestMessage.getEntityId(),
                hasParent ? "bpm.request.ask.answer.email" : "bpm.request.ask.answer",
                param);

        notificationService.sendNotification("bpm.request.ask.answer.email",
                userExt.getEmail(),
                param,
                userExt.getLanguage());
        return activity;
    }

    protected boolean isActivityCreated(BpmRequestMessage bpmRequestMessage) {
        return commonService.getCount(Activity.class,
                "select e from uactivity$Activity e " +
                        "   where e.assignedUser.id = :userId" +
                        "           and e.referenceId = :entityId ",
                ParamsMap.of("userId", bpmRequestMessage.getAssignedUser().getId(),
                        "entityId", bpmRequestMessage.getEntityId())) > 0;
    }

    protected String getRequestUrl(BpmRequestMessage bpmRequestMessage) {
        return "<a href=\"" + globalConfig.getWebAppUrl() + "/open?screen=" + bpmRequestMessage.getScreenName() +
                "&item=" + bpmRequestMessage.getEntityName() + "-" + bpmRequestMessage.getEntityId() +
                "&params=bpmRequestMessage:" + bpmRequestMessage.getId() +
                "\" target=\"_blank\">%s " + bpmRequestMessage.getEntityRequestNumber() + "</a>";
    }

    protected String getTable(String screenName, UUID entityId, UUID procInstanceId, String lang) {
        switch (screenName) {
            case "tsadv$AbsenceRequest.edit":
                return createTableAbsence(entityId, procInstanceId, lang);
            case "tsadv$PositionChangeRequest.edit":
            case "tsadv$PositionChangeRequestTypeChange.edit":
                return createTableForPositionRequest(entityId, lang);
            case "tsadv$SalaryRequest.edit":
                return createTable(lang, entityId, "salaryRequest.template.email");
            case "tsadv$AssignmentRequest.edit":
                return createTable(lang, entityId, "assignment.approver.task.emailnotification");
            case "tsadv$AssignmentSalaryRequest.edit":
                return createTable(lang, entityId, "assignmentSalary.approver.task.emailnotification");
            case "tsadv$TemporaryTranslationRequest.edit":
                return createTable(lang, entityId, "temporaryTranslation.approver.task.emailnotification");
            case "tsadv$AddressRequest.edit":
                return createTableAddress(lang, entityId);
            case "tsadv$PersonalDataRequest.bpm":
                return createTablePersonalData(lang, entityId);
            default:
                return "";
        }
    }

    protected String createTableAddress(String lang, UUID entityId) {
        AddressRequest addressRequest = commonService.getEntity(AddressRequest.class, entityId, "addressRequest-view");
        PersonExt person = employeeService.getPersonByPersonGroup(addressRequest.getPersonGroup().getId(), CommonUtils.getSystemDate(), "person.full");

        boolean isRussian = !lang.equals("en");
        StrBuilder strBuilder = new StrBuilder();
        strBuilder.append(getStyle());
        strBuilder.append("<table>")
                .append("<tr  class=\"tableHeader\" >")
                .append("<td>").append("</td>")
                .append("<td>").append(isRussian ? "Информация об адресе" : "Address information").append("</td>")
                .append("</tr>")
                .append("<tr class=\"color\" >")
                .append("<td>").append(isRussian ? "Работник" : "Employee").append("</td>")
                .append("<td>").append(person.getFioWithEmployeeNumber()).append("</td>")
                .append("</tr>")
                .append("<tr>")
                .append("<td>").append(isRussian ? "Тип адреса" : "Type of address").append("</td>")
                .append("<td>").append(isRussian ? addressRequest.getAddressType().getLangValue1() : addressRequest.getAddressType().getLangValue3()).append("</td>")
                .append("</tr>")
                .append("<tr>")
                .append("<td class=\"color\" >").append(isRussian ? "Адрес" : "Address").append("</td>")
                .append("<td>").append(addressRequest.getAddress()).append("</td>")
                .append("</tr>")
                .append("<tr>")
                .append("<td>").append(isRussian ? "Страна" : "Country").append("</td>")
                .append("<td>").append(isRussian ? addressRequest.getCountry().getLangValue1() : addressRequest.getCountry().getLangValue3()).append("</td>")
                .append("</tr>")
                .append("<tr class=\"color\" >")
                .append("<td>").append(isRussian ? "Почтовый индекс" : "Postcode").append("</td>")
                .append("<td>").append(addressRequest.getPostalCode()).append("</td>")
                .append("</tr>")
                .append("<tr>")
                .append("<td>").append(isRussian ? "Город" : "City").append("</td>")
                .append("<td>").append(addressRequest.getCity()).append("</td>")
                .append("</tr>");
        strBuilder.append("</table>");
        return strBuilder.toString();
    }

    protected String createTablePersonalData(String lang, UUID entityId) {
        PersonalDataRequest personalDataRequest = commonService.getEntity(PersonalDataRequest.class, entityId, "personalDataRequest-view");
        PersonExt person = employeeService.getPersonByPersonGroup(personalDataRequest.getPersonGroup().getId(),
                this.getDate(personalDataRequest.getUpdateTs() != null ? personalDataRequest.getUpdateTs() : personalDataRequest.getCreateTs(), personalDataRequest.getStatus()), "person.full");
        boolean isRussian = !lang.equals("en");
        int cnt = 0;
        StrBuilder strBuilder = new StrBuilder();
        strBuilder.append(getStyle());
        strBuilder.append("<table>")
                .append("<tr  class=\"tableHeader\" >")
                .append("<td>").append("</td>")
                .append("<td>").append(isRussian ? "Текущий" : "Current").append("</td>")
                .append("<td>").append(isRussian ? "Новый" : "New")
                .append("</tr>");
        if (Objects.equals(personalDataRequest.getFirstName(), person.getFirstName()))
            strBuilder.append("<tr ").append(cnt++ % 2 == 0 ? "class=\"color\" " : "").append(">")
                    .append("<td>").append(isRussian ? "Имя" : "Name").append("</td>")
                    .append("<td>").append(personalDataRequest.getFirstName()).append("</td>")
                    .append("<td>").append(person.getFirstName()).append("</td>")
                    .append("</tr>");
        if (Objects.equals(person.getMiddleName(), personalDataRequest.getMiddleName())) {
            strBuilder.append("<tr ").append(cnt++ % 2 == 0 ? "class=\"color\" " : "").append(">")
                    .append("<td>").append(isRussian ? "Отчество" : "Middle name").append("</td>")
                    .append("<td>").append(personalDataRequest.getMiddleName()).append("</td>")
                    .append("<td>").append(person.getMiddleName()).append("</td>")
                    .append("</tr>");
        }
        if (Objects.equals(person.getLastName(), personalDataRequest.getLastName())) {
            strBuilder.append("<tr ").append(cnt++ % 2 == 0 ? "class=\"color\" " : "").append(">")
                    .append("<td>").append(isRussian ? "Фамилия" : "Surname").append("</td>")
                    .append("<td>").append(personalDataRequest.getLastName()).append("</td>")
                    .append("<td>").append(person.getLastName()).append("</td>")
                    .append("</tr>");
        }
        if (Objects.equals(person.getFirstNameLatin(), personalDataRequest.getFirstNameLatin())) {
            strBuilder.append("<tr ").append(cnt++ % 2 == 0 ? "class=\"color\" " : "").append(">")
                    .append("<td>").append(isRussian ? "Имя на латинице" : "Latin name").append("</td>")
                    .append("<td>").append(personalDataRequest.getFirstNameLatin()).append("</td>")
                    .append("<td>").append(person.getFirstNameLatin()).append("</td>")
                    .append("</tr>");
        }
        if (Objects.equals(person.getMiddleNameLatin(), personalDataRequest.getMiddleNameLatin())) {
            strBuilder.append("<tr ").append(cnt++ % 2 == 0 ? "class=\"color\" " : "").append(">")
                    .append("<td>").append(isRussian ? "Отчество на латинице" : "Latin middle name").append("</td>")
                    .append("<td>").append(personalDataRequest.getMiddleNameLatin()).append("</td>")
                    .append("<td>").append(person.getMiddleNameLatin()).append("</td>")
                    .append("</tr>");
        }
        if (Objects.equals(person.getLastNameLatin(), personalDataRequest.getLastNameLatin())) {
            strBuilder.append("<tr ").append(cnt++ % 2 == 0 ? "class=\"color\" " : "").append(">")
                    .append("<td>").append(isRussian ? "Фамилия на латинице" : "Latin surname").append("</td>")
                    .append("<td>").append(personalDataRequest.getLastNameLatin()).append("</td>")
                    .append("<td>").append(person.getLastNameLatin()).append("</td>")
                    .append("</tr>");
        }
        if (Objects.equals(person.getDateOfBirth(), personalDataRequest.getDateOfBirth())) {
            strBuilder.append("<tr ").append(cnt++ % 2 == 0 ? "class=\"color\" " : "").append(">")
                    .append("<td>").append(isRussian ? "Дата рождения" : "Date of Birth").append("</td>")
                    .append("<td>").append(personalDataRequest.getDateOfBirth()).append("</td>")
                    .append("<td>").append(person.getDateOfBirth()).append("</td>")
                    .append("</tr>");
        }
        if (Objects.equals(person.getMaritalStatus(), personalDataRequest.getMaritalStatus())) {
            strBuilder.append("<tr ").append(cnt % 2 == 0 ? "class=\"color\" " : "").append(">")
                    .append("<td>").append(isRussian ? "Семейное положение" : "Family status").append("</td>")
                    .append("<td>").append(personalDataRequest.getMaritalStatus()).append("</td>")
                    .append("<td>").append(person.getMaritalStatus()).append("</td>")
                    .append("</tr>");
        }
        strBuilder.append("</table>");
        return strBuilder.toString();
    }

    protected String createTableAbsence(UUID entityId, UUID procInstanceId, String lang) {
        AbsenceRequest absenceRequest = commonService.getEntity(AbsenceRequest.class, entityId, "absenceRequest.view");
        UserExt startedByUser = bpmUtils.getCreatedBy(procInstanceId, "userExt.edit");
        return createTableAbsence(absenceRequest, lang, startedByUser);
    }

}