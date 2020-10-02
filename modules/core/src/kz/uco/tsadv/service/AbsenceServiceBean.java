package kz.uco.tsadv.service;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.Category;
import com.haulmont.cuba.core.entity.CategoryAttribute;
import com.haulmont.cuba.core.entity.CategoryAttributeValue;
import com.haulmont.cuba.core.global.View;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.personal.dictionary.DicAbsenceType;
import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.Absence;
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


    //method overridden in AA
    @Override
    public void fillNextTaskIdRestForAA() {

    }

    @Override
    public int countAbsenceDays(Date dateFrom, Date dateTo, DicAbsenceType absenceType, AssignmentGroupExt assignmentGroup) {
        final long MILLIS_IN_DAY = (24 * 60 * 60 * 1000);
        if (Boolean.TRUE.equals(absenceType.getIgnoreHolidays())) {
            long diff = (dateTo.getTime() - dateFrom.getTime()) / MILLIS_IN_DAY;
            if (diff >= 0) {
                return (int) (diff + 1);
            } else {
                return 0;
            }
        } else {
            Calendar calendar = calendarService.getCalendar(assignmentGroup);
            long diff = (dateTo.getTime() - dateFrom.getTime()) / MILLIS_IN_DAY;
            if (calendar != null && diff >= 0) {
                int holidaysNumber = timesheetService.getAllHolidays(calendar, dateFrom, dateTo);
                return (int) (diff + 1 - holidaysNumber);
            } else {
                return 0;
            }
        }
    }

    @Override
    public int countBusinessDays(Date dateFrom, Date dateTo, DicAbsenceType absenceType, AssignmentGroupExt assignmentGroup) {
        return (countAbsenceDays(dateFrom, dateTo, absenceType, assignmentGroup) - countWeekendDays(dateFrom, dateTo));
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
}