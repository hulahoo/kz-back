package kz.uco.tsadv.service;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.KeyValueEntity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.ValueLoadContext;
import kz.uco.base.common.BaseCommonUtils;
import kz.uco.base.notification.NotificationSender;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.administration.UserExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.Dismissal;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.uactivity.entity.ActivityType;
import kz.uco.uactivity.entity.StatusEnum;
import kz.uco.uactivity.service.ActivityService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;

import static kz.uco.base.common.Null.checkNull;
import static kz.uco.base.common.Null.nullReplace;
import static kz.uco.tsadv.modules.personal.dictionary.DicAssignmentStatus.ACTIVE;

@Service(AssignmentService.NAME)
public class AssignmentServiceBean implements AssignmentService {

    @Inject
    protected CommonService commonService;
    @Inject
    protected EmployeeService employeeService;
    @Inject
    protected ActivityService activityService;
    @Inject
    protected NotificationSender notificationSender;
    @Inject
    protected DataManager dataManager;

    @Override
    public AssignmentExt getAssignment(UUID personGroupId, String view) {
        LoadContext<AssignmentExt> loadContext = LoadContext.create(AssignmentExt.class);
        loadContext.setQuery(LoadContext.createQuery(
                "select e from base$AssignmentExt e " +
                        "where :sysDate between e.startDate and e.endDate " +
                        "and e.personGroup.id = :personGroupId " +
                        "and e.primaryFlag = true " +
                        "and e.assignmentStatus.code in ('ACTIVE','SUSPENDED')")
                .setParameter("personGroupId", personGroupId)
                .setParameter("sysDate", CommonUtils.getSystemDate()))
                .setView(view != null ? view : "_minimal");
        return dataManager.load(loadContext);
    }

    @Override
    public AssignmentExt getAssignment(@Nonnull String login, @Nullable String view) {
        LoadContext<AssignmentExt> loadContext = LoadContext.create(AssignmentExt.class);
        loadContext.setQuery(LoadContext.createQuery(
                "select e from base$AssignmentExt e " +
                        "     join tsadv$UserExt u on u.personGroup.id = e.personGroup.id" +
                        "    where :sysDate between e.startDate and e.endDate " +
                        "      and u.login = :login" +
                        "      and e.primaryFlag = true " +
                        "      and e.assignmentStatus.code in ('ACTIVE','SUSPENDED')")
                .setParameter("login", login)
                .setParameter("sysDate", CommonUtils.getSystemDate()))
                .setView(view != null ? view : "_minimal");
        return dataManager.load(loadContext);
    }

    @Override
    public boolean isReHire(AssignmentExt currentAssignmentFirst) {
        List<Dismissal> dismissals = new ArrayList<>();
        String queryString = "SELECT e from tsadv$Dismissal e " +
                "WHERE e.personGroup.id = :personGroupId " +
                "AND e.dismissalDate < :currentAssignmentStartDate";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("personGroupId", currentAssignmentFirst.getPersonGroup().getId());
        queryParams.put("currentAssignmentStartDate", currentAssignmentFirst.getStartDate());
        dismissals = commonService.getEntities(Dismissal.class, queryString, queryParams, "dismissal.view");
        if (!dismissals.isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isNationalIdentifierDuplicate(String nationalIdentifier) {
        if (StringUtils.isBlank(nationalIdentifier)) {
            return false;
        }
        String queryString = "SELECT national_identifier\n" +
                "             FROM base_person\n" +
                "             WHERE national_identifier = ?";
        Map<Integer, Object> params = new HashMap<>();
        params.put(1, nationalIdentifier);
        List<Object[]> resultList = commonService.emNativeQueryResultList(queryString, params);
        if (resultList.size() > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isTransferInFutureExists(PersonGroupExt personGroup, Date checkDate) {
        if (personGroup == null || checkDate == null) {
            return false;
        }
        List<AssignmentExt> futureAssignments;
        String queryString = "SELECT a FROM base$AssignmentExt a\n" +
                "             WHERE a.personGroup.id = :currentPersonGroupId\n" +
                "             AND a.startDate > :checkDate";
        Map<String, Object> params = new HashMap<>();
        params.put("currentPersonGroupId", personGroup.getId());
        params.put("checkDate", checkDate);
        futureAssignments = commonService.getEntities(AssignmentExt.class, queryString, params, "assignment.view");
        if (!futureAssignments.isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isDismissalInFutureExists(Dismissal dismissal, Date checkDate) {
        if (dismissal == null) {
            return false;
        }
        List<Dismissal> futureDismissals;
        String queryString = "SELECT d FROM tsadv$Dismissal d\n" +
                "             WHERE d.assignmentGroup.id = :assignmentGroupId\n" +
                "             AND d.dismissalDate > :checkDate" +
                "             AND d.id <> :dismissalId";
        Map<String, Object> params = new HashMap<>();
        params.put("assignmentGroupId", dismissal.getAssignmentGroup().getId());
        params.put("dismissalId", dismissal.getId());
        params.put("checkDate", checkDate);
        futureDismissals = commonService.getEntities(Dismissal.class, queryString, params, "_minimal");
        if (!futureDismissals.isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public void notifyTemporaryEndDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        List<Date> dates = Arrays.asList(DateUtils.addDays(CommonUtils.getSystemDate(), 3),
                DateUtils.addDays(CommonUtils.getSystemDate(), 7),
                DateUtils.addDays(CommonUtils.getSystemDate(), 30));

        List<AssignmentExt> list = commonService.getEntities(AssignmentExt.class,
                "select e from base$AssignmentExt e where e.temporaryEndDate in :dates",
                ParamsMap.of("dates", dates), "assignmentExt.bpm.view");

        for (AssignmentExt assignmentExt : list) {
            try {
                UUID managerId = employeeService.getImmediateSupervisorByPersonGroup(assignmentExt.getPersonGroup().getId());
                UserExt manager = commonService.getEntity(UserExt.class,
                        " select e from tsadv$UserExt e where e.personGroup.id = :managerId ",
                        ParamsMap.of("managerId", managerId), "userExt.edit");
                if (Objects.nonNull(manager)) {
                    PersonExt employee = employeeService.getPersonByPersonGroup(assignmentExt.getPersonGroup().getId(), CommonUtils.getSystemDate(), "person-edit");

                    Map<String, Object> param = new HashMap<>();
                    param.put("managerRu", Optional.ofNullable(manager.getPersonGroup().getPerson() != null ? manager.getPersonGroup().getPerson().getFullName() : manager.getFullName()).orElse(""));
                    param.put("managerEn", Optional.ofNullable(manager.getPersonGroup().getPerson() != null ? manager.getPersonGroup().getPerson().getFullNameLatin() : manager.getFullName()).orElse(""));

                    param.put("employeeRu", Optional.ofNullable(employee.getFullNameLatin("ru")).orElse(""));
                    param.put("employeeEn", Optional.ofNullable(employee.getFullNameLatin("en")).orElse(""));

                    if (Objects.nonNull(assignmentExt.getPositionGroup())
                            && Objects.nonNull(assignmentExt.getPositionGroup().getPosition())) {
                        param.put("positionNameRu", Optional.ofNullable(assignmentExt.getPositionGroup().getPosition().getPositionFullNameLang1()).orElse(""));
                        param.put("positionNameEn", Optional.ofNullable(assignmentExt.getPositionGroup().getPosition().getPositionFullNameLang3()).orElse(""));
                    }
                    param.put("temporaryEndDate", dateFormat.format(assignmentExt.getTemporaryEndDate()));

                    activityService.createActivity(
                            manager,
                            null,
                            commonService.getEntity(ActivityType.class, "NOTIFICATION"),
                            StatusEnum.active,
                            "description",
                            null,
                            new Date(),
                            null,
                            null,
                            null,
                            "assignment.notify.temporaryEndDate",
                            param);
                    notificationSender.sendParametrizedNotification("assignment.notify.temporaryEndDate", manager, param);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Возвращает Id штатной единицы на которую назначен заданное лицо (работник)
     * Назначение берётся основное на заданный момент времени с заданным статусом
     *
     * @param personGroupId        Id лица (работника)
     * @param moment               Момент времени (в машине времени)
     * @param assignmentStatusCode Код статуса назначения
     */
    @Override
    public UUID getPrimaryCurrentPositionGroupIdForPersonGroupId(
            UUID personGroupId,
            Date moment,
            String assignmentStatusCode
    ) {
        checkNull(personGroupId, "Person Id is undefined in AssignmentServiceBean.getPrimaryCurrentPositionGroupIdForPersonGroupId()");

        ValueLoadContext valueLoadContext = ValueLoadContext.create();
        ValueLoadContext.Query query = ValueLoadContext.createQuery(
                "" +
                        "select a.positionGroup.id " +
                        "  from base$AssignmentExt a " +
                        " where a.personGroup.id = :personGroupId " +
                        "   and :systemDate between a.startDate and a.endDate " +
                        "   and a.assignmentStatus.code = :assignmentStatusCode " +
                        "   and a.primaryFlag = true ");
        valueLoadContext.setQuery(query);
        valueLoadContext.addProperty("positionGroupId");

        query.setParameter("personGroupId", personGroupId);
        query.setParameter("systemDate", nullReplace(moment, BaseCommonUtils.getSystemDate()));
        query.setParameter("assignmentStatusCode", nullReplace(assignmentStatusCode, ACTIVE));

        List<KeyValueEntity> list = dataManager.loadValues(valueLoadContext);
        if (list != null && !list.isEmpty()) {
            return list.get(0).getValue("positionGroupId");
        }
        return null;
    }

    /**
     * Возвращает Id штатной единицы на которую назначен заданное лицо (работник)
     * Назначение берётся основное, на текущий момент времени (в машине времени) выбранный пользователем в системе
     * с Активным статусом назначения
     *
     * @param personGroupId Id лица (работника)
     */
    @Override
    public UUID getPrimaryCurrentPositionGroupIdForPersonGroupId(UUID personGroupId) {
        return getPrimaryCurrentPositionGroupIdForPersonGroupId(
                personGroupId,
                BaseCommonUtils.getSystemDate(),
                ACTIVE
        );
    }
}