package kz.uco.tsadv.service;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.entity.Category;
import com.haulmont.cuba.core.entity.CategoryAttribute;
import com.haulmont.cuba.core.entity.CategoryAttributeValue;
import com.haulmont.cuba.core.entity.contracts.Id;
import com.haulmont.cuba.core.global.*;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.entity.dictionary.DicCompany;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.dictionary.DicAbsenceType;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.modules.personal.enums.VacationDurationType;
import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.Absence;
import kz.uco.tsadv.modules.personal.model.AbsenceRequest;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.VacationConditions;
import kz.uco.tsadv.modules.timesheet.model.Calendar;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service(AbsenceService.NAME)
public class AbsenceServiceBean implements AbsenceService {

    @Inject
    protected DynamicAttributesService dynamicAttributesService;
    @Inject
    protected CommonService commonService;
    @Inject
    protected TimesheetService timesheetService;
    @Inject
    protected CallStoredFunctionService callStoredFunctionService;
    @Inject
    protected CalendarService calendarService;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected EmployeeService employeeService;
    @Inject
    private Persistence persistence;
    @Inject
    private Metadata metadata;

    @Inject
    private UserSessionSource userSessionSource;

    @Override
    public boolean isLongAbsence(Absence absence) {
        if (absence == null) {
            return false;
        }
        Category category = dynamicAttributesService.getCategory("ABSENCETYPE", "tsadv$DicAbsenceType");
        List<CategoryAttribute> categoryAttributes = dynamicAttributesService.getCategoryAttributes(category);
        CategoryAttribute categoryAttribute = categoryAttributes.stream().filter(c -> c.getCode().equals("ABSENCETYPELONG")).findFirst().orElse(null);

        CategoryAttributeValue categoryAttributeValue = dynamicAttributesService.getCategoryAttributeValue(absence.getType(), categoryAttribute);
        if (categoryAttributeValue != null) {
            return categoryAttributeValue.getBooleanValue();
        } else {
            return false;
        }
    }

    @Override
    public List<Absence> getAllAbsencesForPerson(PersonGroupExt personGroup) {
        List<Absence> result = new ArrayList<>();
        if (personGroup == null) {
            return result;
        }
        String queryString = "select e from tsadv$Absence e" +
                " where e.personGroup.id = :personGroupId" +
                " and e.type.useInBalance = 'true'" +
                " and e.deleteTs IS NULL" +
                " order by e.dateFrom";
        Map<String, Object> params = new HashMap<>();
        params.put("personGroupId", personGroup);
        result = commonService.getEntities(Absence.class, queryString, params, "absence.view");
        return result;
    }

    @Override
    public int countAbsenceDays(Date dateFrom, Date dateTo, DicAbsenceType absenceType, AssignmentGroupExt assignmentGroup) {
        final long MILLIS_IN_DAY = (24 * 60 * 60 * 1000);
        long diff = (dateTo.getTime() - dateFrom.getTime()) / MILLIS_IN_DAY + 1;
        if (Boolean.TRUE.equals(absenceType.getIgnoreHolidays())) {
            return Math.max((int) diff, 0);
        } else {
            Calendar calendar = calendarService.getCalendar(assignmentGroup);
            if (calendar != null && diff >= 0) {
                int holidaysNumber = timesheetService.getAllHolidays(calendar, dateFrom, dateTo);
                return (int) (diff - holidaysNumber);
            } else
                return Math.max((int) diff, 0);
        }
    }

    @Override
    public int countBusinessDays(Date dateFrom, Date dateTo, DicAbsenceType absenceType, AssignmentGroupExt assignmentGroup) {
        return (countAbsenceDays(dateFrom, dateTo, absenceType, assignmentGroup) - countWeekendDays(dateFrom, dateTo));
    }

    @Override
    public int countDays(Date dateFrom, Date dateTo, UUID absenceTypeId, UUID personGroupId) {

        DicAbsenceType absenceType = dataManager.load(Id.of(absenceTypeId, DicAbsenceType.class)).view(View.LOCAL)
                .optional().orElse(null);

        if (absenceType == null) return 0;

        AssignmentExt assignmentExt = employeeService.getAssignmentExt(personGroupId, dateFrom, "portal-assignment-group");

        return VacationDurationType.WORK.equals(
                this.getVacationDurationType(personGroupId, absenceTypeId, dateFrom))
                ? this.countBusinessDays(dateFrom, dateTo, absenceType, assignmentExt.getGroup())
                : this.countAbsenceDays(dateFrom, dateTo, absenceType, assignmentExt.getGroup());
    }

    @Override
    public int countDaysWithoutHolidays(Date dateFrom, Date dateTo, UUID personGroupId) {
        AssignmentExt assignmentExt = employeeService.getAssignmentExt(personGroupId, dateFrom, "portal-assignment-group");
        DicAbsenceType dicAbsenceType = metadata.create(DicAbsenceType.class);
        dicAbsenceType.setIgnoreHolidays(false);
        return this.countAbsenceDays(dateFrom, dateTo, dicAbsenceType, assignmentExt.getGroup());
    }

    @Override
    public int countWeekendDays(Date dateFrom, Date dateTo) {
        java.util.Calendar c = java.util.Calendar.getInstance();
        int result = 0;
        int oneDay = 24 * 60 * 60 * 1000;
        while (!dateFrom.after(dateTo)) {
            c.setTime(dateFrom);
            int day = c.get(java.util.Calendar.DAY_OF_WEEK);
            if (day == java.util.Calendar.SUNDAY || day == java.util.Calendar.SATURDAY) {
                result++;
            }
            dateFrom.setTime(dateFrom.getTime() + oneDay);
        }
        return result;
    }

/*
    private String intersectionForHr(String personGroupId, Date startDate, Date endDate
            , UUID absenceId, boolean langIsRu, String result, List<AbsenceRequest> requests) {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date localTo, localFrom;
        UUID personGroupUuid = UUID.fromString(personGroupId);
        List<Absence> absenceList = getAbsencesByPersonGroup(personGroupUuid);
        String queryString = "select tbt from tsadv$BusinessTrip tbt " +
                "where tbt.personGroup.id = :personGroupId";
        Map<String, Object> tripParams = new HashMap<>();
        tripParams.put("personGroupId", personGroupUuid);
        List<BusinessTrip> businessTripList = commonService.getEntities(BusinessTrip.class, queryString
                , tripParams, "businessTrip-view_with_type");
        for (BusinessTrip trip : businessTripList) {
            localTo = trip.getDateTo();
            localFrom = trip.getDateFrom();
            if ((startDate.compareTo(localFrom) >= 0 && startDate.compareTo(localTo) <= 0) ||
                    (endDate.compareTo(localFrom) >= 0 && endDate.compareTo(localTo) <= 0) ||
                    (startDate.compareTo(localFrom) <= 0 && endDate.compareTo(localTo) >= 0)) {
                String fromStr = dateFormat.format(localFrom);
                String toStr = dateFormat.format(localTo);
                if (langIsRu) {
                    result += trip.getType().getLangValue1() + " командировка c " + fromStr + " до " + toStr + "\n";
                } else {
                    result += trip.getType().getLangValue3() + " business trip from " + fromStr + " to " + toStr + "\n";
                }
            }
        }
        for (Absence absence : absenceList) {
            boolean isCanceling = false;
            if (absence.getType().getCode().equals("CANCEL") || absence.getType().getCode().equals("RECALL")) {
                isCanceling = true;
            }
            boolean absenceInRequests = false;
            if (!isCanceling) {
                if (requests != null && absence.getAbsenceRequest() != null) {
                    for (AbsenceRequest request : requests) {
                        if (absence.getAbsenceRequest().getId().equals(request.getId())) {
                            absenceInRequests = true;
                        }
                    }

                }
                localTo = absence.getDateTo();
                localFrom = absence.getDateFrom();
                if (((startDate.compareTo(localFrom) >= 0 && startDate.compareTo(localTo) <= 0) ||
                        (endDate.compareTo(localFrom) >= 0 && endDate.compareTo(localTo) <= 0) ||
                        (startDate.compareTo(localFrom) <= 0 && endDate.compareTo(localTo) >= 0))
                        && !absence.getId().equals(absenceId) && !absenceInRequests) {
                    // Проверка на Отмену и отзыв

                    String fromStr = dateFormat.format(localFrom);
                    String toStr = dateFormat.format(localTo);
                    String recallOrCancel = checkForCancel(absence, null, null, langIsRu, startDate, endDate);
                    if (recallOrCancel.equals("")) {
                        if (langIsRu) {
                            result += absence.getType().getLangValue1() + " c " + fromStr + " до " + toStr + "\n";
                        } else {
                            result += absence.getType().getLangValue3() + " from " + fromStr + " to " + toStr + "\n";
                        }
                    }
                    if (!recallOrCancel.equals("null") && !recallOrCancel.equals("")) {
                        result += recallOrCancel;
                    }

                }
            }
        }
        return result;
    }

    private String checkForCancel(Absence absence, AbsenceRequest request, UUID personGroupUuid, boolean langIsRu, Date start, Date end) {
        boolean isCancelled = false;
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        final long MILLIS_IN_DAY = (24 * 60 * 60 * 1000);
        String result = "";
        if (absence == null) {
            List<Absence> absenceList = getAbsencesByPersonGroup(personGroupUuid);
            for (Absence localAbsence : absenceList) {
                if (localAbsence.getAbsenceRequest() != null) {
                    if (localAbsence.getAbsenceRequest().getId().equals(request.getId())) absence = localAbsence;
                }
            }
        }
        if(absence != null) {
            Absence recallAbsence = null;
            List<Absence> childrenAbsences = getAbsencesByParent(absence.getId());
            for (Absence child : childrenAbsences) {
                if (child.getType().getCode().equals("CANCEL")) isCancelled = true;
                if (child.getType().getCode().equals("RECALL")) recallAbsence = child;
            }
            if (recallAbsence != null) {
                String fromDateStr = dateFormat.format(absence.getDateFrom());
                String toDateStr = dateFormat.format(absence.getDateTo());
                String recallTo = dateFormat.format(recallAbsence.getDateTo().getTime() + MILLIS_IN_DAY);
                String recallFrom = dateFormat.format(recallAbsence.getDateFrom().getTime() - MILLIS_IN_DAY);
                if (start.getTime() >= recallAbsence.getDateFrom().getTime() &&
                        end.getTime() <= recallAbsence.getDateTo().getTime()) {
                    return "null";
                }
                if (recallAbsence.getDateFrom() == absence.getDateFrom()
                        && recallAbsence.getDateTo() == absence.getDateTo()) {

                } else if (recallAbsence.getDateFrom() == absence.getDateFrom()) {
                    if (langIsRu) {
                        result += absence.getType().getLangValue1() + " c " + recallTo
                                + " до " + toDateStr + "\n";
                    } else {
                        result += absence.getType().getLangValue3() + " from " + recallTo
                                + " to " + toDateStr + "\n";
                    }
                } else if (recallAbsence.getDateTo() == absence.getDateTo()) {
                    if (langIsRu) {
                        result += absence.getType().getLangValue1() + " c " + fromDateStr
                                + " до " + recallFrom + "\n";
                    } else {
                        result += absence.getType().getLangValue3() + " from " + fromDateStr
                                + " to " + recallFrom + "\n";
                    }
                } else {
                    if (langIsRu) {
                        result += absence.getType().getLangValue1() + " c " + fromDateStr
                                + " до " + recallFrom + "\n" +
                                absence.getType().getLangValue1() + " c " + recallTo
                                + " до " + toDateStr + "\n";
                    } else {
                        result += absence.getType().getLangValue3() + " from " + fromDateStr
                                + " to " + recallFrom + "\n" +
                                absence.getType().getLangValue3() + " from " + recallTo
                                + " to " + toDateStr + "\n";
                    }
                }
            }
        }
        return isCancelled ? "null" : result;
    }
*/

   /* private List<Absence> getAbsencesByPersonGroup(UUID personGroupId) {
        String queryString = "select ta from tsadv$Absence ta " +
                "where ta.personGroup.id = :personGroupId";
        Map<String, Object> params = new HashMap<>();
        params.put("personGroupId", personGroupId);
        List<Absence> absenceList = commonService.getEntities(Absence.class, queryString
                , params, "absence-view-with-person-group");
        return absenceList;
    }

    private List<Absence> getAbsencesByParent(UUID parentId) {
        String queryString = "select ta from tsadv$Absence ta " +
                "where ta.parentAbsence.id = :parentId";
        Map<String, Object> params = new HashMap<>();
        params.put("parentId", parentId);
        List<Absence> absenceList = commonService.getEntities(Absence.class, queryString
                , params, "absence-view-check-parent");
        return absenceList;
    }*/
/*
    @Override
    public String intersectionExists(String personGroupId, Date startDate, Date endDate, String language, UUID absenceId, boolean isHr, UUID assignGroupUuid) {
        Date to, from;
        boolean langIsRu = (language.equals("ru")) ? true : false;
        UUID personGroupUuid = UUID.fromString(personGroupId);
        String result = langIsRu ? "Есть перекрывающие отсутствия:\n"
                : "There are absence overlaps for selected dates\n";
        if (isHr && assignGroupUuid == null) {
            result = intersectionForHr(personGroupId, startDate, endDate, absenceId, langIsRu, result, null);
        } else {
            String queryString = "select absenceRequest from tsadv$AbsenceRequest absenceRequest " +
                    "where absenceRequest.assignmentGroup.id = :assignGroupId";
            Map<String, Object> params = new HashMap<>();
            params.put("assignGroupId", assignGroupUuid);
            List<AbsenceRequest> absenceRequestList = commonService.getEntities(AbsenceRequest.class, queryString
                    , params, "absenceRequest-view-for-intersection");
            for (AbsenceRequest absenceRequest : absenceRequestList) {
                to = absenceRequest.getDateTo();
                from = absenceRequest.getDateFrom();
                boolean isIntersects = false;
                if ((startDate.compareTo(from) >= 0 && startDate.compareTo(to) <= 0) ||
                        (endDate.compareTo(from) >= 0 && endDate.compareTo(to) <= 0) ||
                        (startDate.compareTo(from) <= 0 && endDate.compareTo(to) >= 0)) {
                    String recallOrCancel = "";
                    if (absenceRequest.getStatus().getCode().equals("APPROVED")) {
                        recallOrCancel = checkForCancel(null, absenceRequest, personGroupUuid, langIsRu, absenceRequest.getDateFrom(), absenceRequest.getDateTo());
                        isIntersects = true;
                    }
                    if (!isHr && absenceRequest.getStatus().getCode().equals("APPROVING")) {
                        isIntersects = true;
                    }
                    if (recallOrCancel.equals("")) {
                        if (isIntersects) {
                            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                            String fromStr = dateFormat.format(from);
                            String toStr = dateFormat.format(to);
                            String temp = "";
                            if (langIsRu) {
                                temp += "Заявка";
                                temp += (absenceRequest.getStatus().getCode().equals("Approved")) ? " утверждена" : " на утверждении";
                                result += temp + " c " +
                                        fromStr + " до " + toStr + "\n";
                            } else {
                                temp += "Request";
                                temp += (absenceRequest.getStatus().getCode().equals("Approved")) ? " approved" : " on approval";
                                result += temp + " from " +
                                        fromStr + " to " + toStr + "\n";
                            }
                        }
                    }
                    if (!recallOrCancel.equals("") && !recallOrCancel.equals("null")) {
                        result += recallOrCancel;
                    }
                }
            }
            result = intersectionForHr(personGroupId, startDate, endDate, absenceId,
                    langIsRu, result, absenceRequestList);
        }
        result = result.equals("Есть перекрывающие отсутствия:\n") ||
                result.equals("There are absence overlaps for selected dates\n") ? "" : result;
        return result.equals("") ? "" : result;
    }*/

    public String intersectionExists(String personGroupId,
                                     Date startDate,
                                     Date endDate,
                                     String language,
                                     @Nullable UUID absenceId,
                                     String checkingType) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        String sql = String.format("select tal.absence_has_overlaps('%s', '%s', '%s', '%s', %s, '%s')",
                personGroupId,
                df.format(startDate),
                df.format(endDate),
                language,
                absenceId != null ? "'" + absenceId + "'" : "null",
                checkingType);

        Object res = callStoredFunctionService.execCallSqlFunction(sql);
        return res != null ? res.toString() : "";
    }

    @Override
    public boolean bpmRequiredForAbsence(UUID entityId) {
        return (boolean) callStoredFunctionService.execCallSqlFunction(String.format("select tal.bpmRequiredForAbsence('%s')", entityId));
    }

    @Override
    public boolean isAbsenceTypeLong(UUID absenceTypeId) {
        List<CategoryAttributeValue> list = commonService.getEntities(CategoryAttributeValue.class,
                "select e from sys$CategoryAttributeValue e " +
                        " where e.code = 'ABSENCETYPELONG' " +
                        "    and e.booleanValue = true  " +
                        "    and e.entity.entityId = :eId  ",
                ParamsMap.of("eId", absenceTypeId), View.LOCAL);
        return !list.isEmpty();
    }

    @Override
    public List<DicAbsenceType> getAbsenceTypeLong(@Nullable String viewName) {
        return commonService.getEntities(DicAbsenceType.class,
                " select e from tsadv$DicAbsenceType e " +
                        " join sys$CategoryAttributeValue s " +
                        "       on s.entity.entityId = e.id " +
                        "       and s.code = 'ABSENCETYPELONG' " +
                        "       and s.booleanValue = true ",
                ParamsMap.empty(), Optional.ofNullable(viewName).orElse(View.MINIMAL));
    }

    @Override
    public void createAbsenceFromRequest(String entityId) {
        EntityManager em = persistence.getEntityManager();
        AbsenceRequest absenceRequest = em.find(AbsenceRequest.class, UUID.fromString(entityId), "absenceRequest.view");
        Absence absence = metadata.create(Absence.class);

        absence.setNotificationDate(absenceRequest.getRequestDate());
        absence.setAbsenceDays(absenceRequest.getAbsenceDays());
        absence.setAbsenceRequest(absenceRequest);
        absence.setPersonGroup(absenceRequest.getAssignmentGroup().getList().get(0).getPersonGroup());
        absence.setDateFrom(absenceRequest.getDateFrom());
        absence.setDateTo(absenceRequest.getDateTo());
        absence.setType(absenceRequest.getType());

        if (AppBeans.get(DatesService.class).getFullDaysCount(absenceRequest.getDateFrom(), absenceRequest.getDateTo()) >= 30
                && isAbsenceTypeLong(absenceRequest.getId())) {
            absence.setUseInBalance(true);
        }

        Objects.requireNonNull(absenceRequest).setStatus(commonService.getEntity(DicRequestStatus.class, "APPROVED"));

        em.merge(absenceRequest);
        em.persist(absence);
    }

    public VacationDurationType getVacationDurationType(UUID personGroupId, UUID absenceTypeId, Date date) {
        DicAbsenceType absenceType = dataManager.load(Id.of(absenceTypeId, DicAbsenceType.class)).view(View.LOCAL)
                .optional().orElse(null);

        AssignmentExt assignmentExt = employeeService.getAssignmentExt(personGroupId, date, "portal-assignment-group");

        VacationDurationType vacationDurationType = getVacationDurationType(absenceType);
        if (vacationDurationType != null) return vacationDurationType;

        List<VacationConditions> vacationConditionsList = dataManager.load(VacationConditions.class)
                .query("select v from base$AssignmentExt a" +
                        "   join tsadv$VacationConditions v " +
                        "       on v.positionGroup = a.positionGroup " +
                        " where a.group.id = :assignmentGroupId" +
                        "   and :sysDate between a.startDate and a.endDate " +
                        "   and a.primaryFlag = 'TRUE' " +
                        "   and :sysDate between v.startDate and v.endDate " +
                        " order by v.startDate desc ")
                .parameter("assignmentGroupId", assignmentExt.getGroup().getId())
                .parameter("sysDate", Optional.ofNullable(date).orElse(CommonUtils.getSystemDate()))
                .view(View.LOCAL)
                .list();

        return vacationConditionsList.stream()
                .map(VacationConditions::getVacationDurationType)
                .filter(Objects::nonNull)
                .findAny()
                .orElse(VacationDurationType.CALENDAR);
    }

    protected java.util.Calendar truncCalendar(java.util.Calendar calendar) {
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);
        return calendar;
    }

    @Override
    public Long getReceivedVacationDaysOfYear(UUID personGroupId, UUID absenceTypeId, Date date) {
        try {
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            calendar.setTime(date);

            int year = calendar.get(java.util.Calendar.YEAR);

            calendar.setTime(format.parse("2020-01-01"));
            this.truncCalendar(calendar);

            calendar.set(java.util.Calendar.YEAR, year);
            Date startDate = calendar.getTime();

            calendar.setTime(format.parse("2020-12-31"));
            this.truncCalendar(calendar);

            calendar.set(java.util.Calendar.YEAR, year);
            Date endDate = calendar.getTime();

            //noinspection ConstantConditions
            return persistence.callInTransaction(em ->
                    (Long) em.createNativeQuery("with absences as (select sum(e.ABSENCE_DAYS) as sum from TSADV_ABSENCE e " +
                            " where e.person_group_id = #personGroupId " +
                            "   and e.type_id = #typeId" +
                            "   and e.date_from between (#starDate)::date and (#endDate)::date " +
                            "   and e.delete_ts is null ) ," +
                            "requests as (" +
                            "   select sum(e.ABSENCE_DAYS) as sum from TSADV_ABSENCE_REQUEST e " +
                            " join TSADV_DIC_REQUEST_STATUS s on s.id = e.status_id " +
                            " where e.person_group_id = #personGroupId " +
                            "   and e.type_id = #typeId " +
                            "   and s.code = 'APPROVING' " +
                            "   and e.date_from between (#starDate)::date and (#endDate)::date " +
                            "   and e.delete_ts is null)" +
                            "select (coalesce( (select sum from absences) , 0) + coalesce((select sum from requests),0) ) as sum")
                            .setParameter("personGroupId", personGroupId)
                            .setParameter("typeId", absenceTypeId)
                            .setParameter("endDate", endDate)
                            .setParameter("starDate", startDate)
                            .getFirstResult()
            );

        } catch (Exception e) {
            throw new RuntimeException("", e);
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public Long getRemainingDaysWeekendWork(UUID personGroupId) {
        return persistence.callInTransaction(em ->
                (Long) em.createNativeQuery("with absences_work_on_weekend as (select sum(e.ABSENCE_DAYS) as sum from TSADV_ABSENCE e \n" +
                        "\tjoin tsadv_dic_absence_type t on t.id = e.type_id and t.delete_ts is null and t.work_on_weekend is true\n" +
                        " where e.person_group_id = #personGroupId \n" +
                        "   and e.delete_ts is null ) ,\n" +
                        "absences_is_check_work as (select sum(e.ABSENCE_DAYS) as sum from TSADV_ABSENCE e \n" +
                        "\tjoin tsadv_dic_absence_type t on t.id = e.type_id and t.delete_ts is null and t.is_check_work is true\n" +
                        " where e.person_group_id = #personGroupId \n" +
                        "   and e.delete_ts is null ) ,\n" +
                        "requests as (\n" +
                        "   select sum(e.ABSENCE_DAYS) as sum from TSADV_ABSENCE_REQUEST e \n" +
                        " \tjoin TSADV_DIC_REQUEST_STATUS s on s.id = e.status_id and e.delete_ts is null\n" +
                        "\tjoin tsadv_dic_absence_type t on t.id = e.type_id and t.delete_ts is null and t.is_check_work is true\n" +
                        " where e.person_group_id = #personGroupId \n" +
                        "   and s.code = 'APPROVING' \n" +
                        "   and e.delete_ts is null)\n" +
                        "select (coalesce( (select sum from absences_work_on_weekend) , 0) - coalesce((select sum from absences_is_check_work),0) - coalesce((select sum from requests),0) )  as sum")
                        .setParameter("personGroupId", personGroupId)
                        .getFirstResult()
        );
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    @Override
    public Integer scheduleOffsetDaysBeforeAbsence() {
        final DicCompany userCompany = employeeService.getCompanyByPersonGroupId(userSessionSource.getUserSession().getAttribute(StaticVariable.USER_PERSON_GROUP_ID));

        return persistence.callInTransaction(em ->
                em.createQuery("select a.daysBeforeAbsence " +
                        "from tsadv$DicAbsenceType a " +
                        "where a.company = :userCompany " +
                        "   and a.isScheduleOffsetsRequest = 'TRUE' " +
                        "   and a.active = true", Integer.class)
                        .setParameter("userCompany", userCompany)
                        .getFirstResult());
    }

    @Nullable
    protected VacationDurationType getVacationDurationType(@Nullable DicAbsenceType absenceType) {
        return absenceType != null ? absenceType.getVacationDurationType() : null;
    }
}