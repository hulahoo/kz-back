package kz.uco.tsadv.service;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import javafx.util.Pair;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.exceptions.ItemNotFoundException;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.timesheet.config.TimecardConfig;
import kz.uco.tsadv.modules.timesheet.dictionary.DicScheduleElementType;
import kz.uco.tsadv.modules.timesheet.enums.DayType;
import kz.uco.tsadv.modules.timesheet.enums.MaterialDesignColorsEnum;
import kz.uco.tsadv.modules.timesheet.model.*;
import kz.uco.tsadv.modules.timesheet.model.dto.NightPartDTO;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.*;
import java.util.stream.Collectors;

@Service(TimesheetService.NAME)
public class TimesheetServiceBean implements TimesheetService {

    protected static final Long DAYS_IN_MILLIS = (long) 24 * 60 * 60 * 1000;
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String fieldsDelimiter = "@@";
    public static final String entityDelimiter = "@@@";
    @Inject
    protected TimesheetService timesheetService;

    @Inject
    protected Messages messages;
    @Inject
    protected CommonService commonService;
    @Inject
    protected TimecardConfig timecardConfig;
    @Inject
    protected DatesService datesService;
    @Inject
    protected Metadata metadata;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected Persistence persistence;


    @Override
    public void formSchedule(StandardSchedule schedule, Date month) throws Exception {
        if (schedule.getStartDate().after(month) || (schedule.getEndDate() != null && schedule.getEndDate().before(month))) {
            throw new Exception("datesIssue");
        }
        for (ScheduleHeader header : commonService.getEntities(ScheduleHeader.class,
                "select e " +
                        "    from tsadv$ScheduleHeader e " +
                        "   where e.schedule.id = :scheduleId " +
                        "     and e.month = :month " +
                        "     and e.deleteTs is null ",
                ParamsMap.of("scheduleId", schedule.getId(),
                        "month", month),
                View.LOCAL))
            dataManager.remove(header);

        List<Entity> commitInstances = new ArrayList<>();

        /* 1. FILL HELPFUL INFO START */

        /* month days list */
        List<Date> monthDays = new ArrayList<>();
        for (Date dayDate = datesService.getSecondDate(month, schedule.getStartDate());
             dayDate.before(schedule.getEndDate() == null ? DateUtils.addMonths(month, 1) : datesService.getFirstDate(DateUtils.addMonths(month, 1), DateUtils.addDays(schedule.getEndDate(), 1)));
             dayDate = DateUtils.addDays(dayDate, 1)) {
            monthDays.add(dayDate);
        }

        /* all holidays list from schedule calendar */
        List<CalendarHoliday> calendarHolidays = commonService.getEntities(CalendarHoliday.class,
                "select e from tsadv$CalendarHoliday e where e.calendar.id = :scheduleCalendarId and e.deleteTs is null " +
                        " and e.actionDateTo >= :monthEnd and :monthStart >= e.actionDateFrom ",
                ParamsMap.of("scheduleCalendarId", schedule.getCalendar().getId(),
                        "monthStart", month,
                        "monthEnd", DateUtils.addMonths(month, 1)),
                "calendarHoliday.view");

        /* all weekends set */
        Set<Date> weekends = calendarHolidays
                .stream()
                .filter(h -> h.getDayType() == DayType.TRANSFER
                        && !schedule.getIsHolidayWorkDay()
                        || h.getDayType() == DayType.WEEKEND
                        || h.getDayType() == DayType.OFFICIAL_WEEKEND)
                .map(h -> {
                    ArrayList<Date> dates = new ArrayList<>();
                    Date d = h.getStartDate();
                    while (!d.after(h.getEndDate())) {
                        dates.add(d);
                        d = DateUtils.addDays(d, 1);
                    }
                    return dates;
                })
                .flatMap(List::stream)
                .collect(Collectors.toSet());

        /* all holidays set */
        Set<String> holidays = calendarHolidays
                .stream()
                .filter(h -> h.getDayType() == DayType.HOLIDAY)
                .map(h -> {
                    ArrayList<String> dates = new ArrayList<>();
                    Date d = h.getStartDate();
                    while (!d.after(h.getEndDate())) {
                        DateFormat formatter = new SimpleDateFormat("dd.MM");
                        String ddMM = formatter.format(d);
                        dates.add(ddMM);
                        d = DateUtils.addDays(d, 1);
                    }
                    return dates;
                })
                .flatMap(List::stream)
                .collect(Collectors.toSet());

        /* work days transferred to another date map */
        Map<Date, Date> transferWorkDays = calendarHolidays
                .stream()
                .filter(h -> h.getDayType() == DayType.TRANSFER)
                .map(h -> {
                    Map<Date, Date> dates = new HashMap<>();
                    if (h.getTransferStartDate() != null && h.getTransferEndDate() != null) {
                        Date td = h.getTransferStartDate();
                        Date d = h.getStartDate();
                        while (!td.after(h.getTransferEndDate())) {
                            dates.put(td, d);
                            td = DateUtils.addDays(td, 1);
                            d = DateUtils.addDays(d, 1);
                        }
                    }
                    return dates;
                })
                .flatMap(l -> l.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        /* schedule element types map */
        List<DicScheduleElementType> elementTypes = commonService.getEntities(DicScheduleElementType.class,
                "select e from tsadv$DicScheduleElementType e where e.deleteTs is null",
                null,
                View.LOCAL);
        Map<String, DicScheduleElementType> elementTypeMap = elementTypes.stream().collect(Collectors.toMap(DicScheduleElementType::getCode, e -> e));

        /* schedule standard shifts map */
        List<StandardShift> standardShifts = commonService.getEntities(StandardShift.class,
                "select e from tsadv$StandardShift e where e.standardSchedule.id = :scheduleId and e.deleteTs is null",
                ParamsMap.of("scheduleId", schedule.getId()),
                "standardShift.view");
        Map<Integer, StandardShift> standardShiftMap = standardShifts.stream().collect(Collectors.toMap(StandardShift::getNumberInShift, i -> i));

        /* 1. FILL HELPFUL INFO END */

        /* 2. FILL SCHEDULE START */

        /* 2.1. create schedule header for every standard offset */
        for (StandardOffset offset : commonService.getEntities(StandardOffset.class,
                "select e from tsadv$StandardOffset e where e.standardSchedule.id = :scheduleId  and e.deleteTs is null",
                ParamsMap.of("scheduleId", schedule.getId()),
                "standardOffset.view")) {
            if (offset.getStartDate().after(month) || (offset.getEndDate() != null && offset.getEndDate().before(month))) {
                throw new Exception("datesIssue");
            }
            ScheduleHeader header = metadata.create(ScheduleHeader.class);
            header.setSchedule(schedule);
            header.setMonth(month);
            header.setOffset(offset);
            commitInstances.add(header);

            /* calc offset start date from schedule start date, schedule period and offset days */
            Date offsetStartDate = DateUtils.addDays(schedule.getStartDate(), offset.getOffsetDisplayDays() - schedule.getPeriod());

            List<ScheduleSummary> summaries = new ArrayList<>();

            /* 2.2. create schedule summary for every header and day of month */
            int i = datesService.getDayOfMonth(monthDays.get(0));
            for (Date dayDate : monthDays) {
                if (offset.getEndDate() != null && dayDate.after(offset.getEndDate()))
                    break;

                DateFormat formatter = new SimpleDateFormat("dd.MM");
                String ddMM = formatter.format(dayDate);

                ScheduleSummary summary = metadata.create(ScheduleSummary.class);
                commitInstances.add(summary);
                summaries.add(summary);

                summary.setHeader(header);
                summary.setHours(0d);
                summary.setBaseHours(0d);
                summary.setDayDate(dayDate);
                summary.setDay(i++);

                /* calc number in shift */
                int numInShift = !schedule.getIsHolidayWorkDay() && transferWorkDays.containsKey(dayDate)
                        ? ((Long) (((transferWorkDays.get(dayDate).getTime() - offsetStartDate.getTime()) / DAYS_IN_MILLIS) % schedule.getPeriod())).intValue() + 1
                        : ((Long) (((dayDate.getTime() - offsetStartDate.getTime()) / DAYS_IN_MILLIS) % schedule.getPeriod())).intValue() + 1;
                /* get standard shift by number in shift */
                StandardShift standardShift = standardShiftMap.get(numInShift);
                summary.setStandardShift(standardShift); //metaproperty field
                calcSummaryType(schedule, weekends, holidays, elementTypeMap, dayDate, ddMM, summary, standardShift);
            }

            /* 2.3. create schedule details for every summary based on shift details */
            for (ScheduleSummary summary : summaries) {
                DateFormat formatter = new SimpleDateFormat("dd.MM");
                String ddMM = formatter.format(summary.getDayDate());

                /* check is summary 1st to month and has to have part of spliced  detail*/
                if (summary.getDay() == 1) {
                    /* calc previous day shift number */
                    Date prevDayDate = DateUtils.addDays(summary.getDayDate(), -1);

                    StandardShift standardShiftInPrevDay = null;
                    StandardShift standardShift = summary.getStandardShift();
                    for (Map.Entry<Integer, StandardShift> entry : standardShiftMap.entrySet()) {
                        if (entry.getValue().equals(standardShift)) {
                            int numberInShiftForPrevDay = 1;
                            if (entry.getKey() != standardShiftMap.size()) {
                                numberInShiftForPrevDay = entry.getKey() - 1; //previous - else 1 - first;
                            }
                            standardShiftInPrevDay = standardShiftMap.get(numberInShiftForPrevDay);
                        }
                    }
                    if (standardShiftInPrevDay != null && standardShiftInPrevDay.getShift() != null) {
                        Shift shift = standardShiftInPrevDay.getShift();
                        List<ShiftDetail> shiftDetails = commonService.getEntities(ShiftDetail.class,
                                "select e from tsadv$ShiftDetail e where e.shift.id = :shiftId and e.deleteTs is null",
                                ParamsMap.of("shiftId", shift.getId()),
                                "shiftDetail.view");

                        boolean splitDetail;
                        for (ShiftDetail shiftDetail : shiftDetails) {
                            splitDetail = standardShiftInPrevDay.getShiftDisplayDay() == 0;
                            Date nextDay = summary.getDayDate();
                            Date dateIn = shiftDetail.getDayFrom() == 2 ? DateUtils.addDays(prevDayDate, 1) : prevDayDate;
                            if (standardShiftInPrevDay.getShiftDisplayDay() == 2) // minus one day in case of display in day 2
                                dateIn = DateUtils.addDays(dateIn, -1);

                            Date dateOut = shiftDetail.getDayTo() == 2 ? DateUtils.addDays(prevDayDate, 1) : prevDayDate;
                            if (standardShiftInPrevDay.getShiftDisplayDay() == 2) // minus one day in case of display in day 2
                                dateOut = DateUtils.addDays(dateOut, -1);

                            Date dateTimeIn = datesService.getDateTime(dateIn, shiftDetail.getTimeFrom());
                            Date dateTimeOut = datesService.getDateTime(dateOut, shiftDetail.getTimeTo());

                            if (dateTimeOut.after(nextDay)) { // crosses 00:00
                                if (splitDetail) {
                                    List<ScheduleDetail> scheduleDetails = summary.getDetails() != null ?
                                            summary.getDetails() : new ArrayList<>();
                                    if (shiftDetail.getDayFrom() == 1 && shiftDetail.getDayTo() == 2) {
                                        ScheduleDetail secondScheduleDetail = createScheduleDetail(summary, shiftDetail, nextDay, dateTimeOut);
                                        scheduleDetails.add(secondScheduleDetail);
                                    }
                                    if (shiftDetail.getDayFrom() == 2 && shiftDetail.getDayTo() == 2) {
                                        ScheduleDetail secondScheduleDetail = createScheduleDetail(summary, shiftDetail, dateTimeIn, dateTimeOut);
                                        scheduleDetails.add(secondScheduleDetail);

                                    }
                                    summary.setDetails(scheduleDetails);
                                    scheduleDetails.forEach(d -> commitInstances.add(d));
                                }
                            }
                        }
                    }
                }

                /* before "summary.getShift() != null" because base schedule can have schedule for that day */
                summary.setBaseHours(getBaseSummary(schedule, summary).getHours());

                if (summary.getShift() != null || summary.getDetails() != null) { //summary.getDetails() != null - if summary has part of splited shift
                    List<ScheduleDetail> scheduleDetails = new ArrayList<>();
                    if (summary.getDetails() != null && !summary.getDetails().isEmpty()) {
                        scheduleDetails.addAll(summary.getDetails()); //cause from previous iteration can came list
                    }
                    List<ScheduleDetail> nextDayScheduleDetails = new ArrayList<>();
                    ScheduleSummary nextDaySummary = null;

                    if (summary.getShift() != null) {
                        List<ShiftDetail> shiftDetails = commonService.getEntities(ShiftDetail.class,
                                "select e from tsadv$ShiftDetail e where e.shift.id = :shiftId and e.deleteTs is null",
                                ParamsMap.of("shiftId", summary.getShift().getId()),
                                "shiftDetail.view");


//                        List<ScheduleDetail> nextDayScheduleDetails = new ArrayList<>();
                        boolean splitDetail;
                        for (ShiftDetail shiftDetail : shiftDetails) {
                            splitDetail = summary.getStandardShift().getShiftDisplayDay() == 0;
                            Date nextDay = DateUtils.addDays(summary.getDayDate(), 1);
                            Date dateIn = shiftDetail.getDayFrom() == 2 ? DateUtils.addDays(summary.getDayDate(), 1) : summary.getDayDate();
                            if (summary.getStandardShift().getShiftDisplayDay() == 2) // minus one day in case of display in day 2
                                dateIn = DateUtils.addDays(dateIn, -1);

                            Date dateOut = null;
                            if (shiftDetail.getDayTo() == 2) {
                                dateOut = DateUtils.addDays(summary.getDayDate(), 1);
                            } else if (shiftDetail.getDayTo() == 1) {
                                dateOut = summary.getDayDate();
                            }
                            if (summary.getStandardShift().getShiftDisplayDay() == 2) // minus one day in case of display in day 2
                                dateOut = DateUtils.addDays(dateOut, -1);

                            Date dateTimeIn = datesService.getDateTime(dateIn, shiftDetail.getTimeFrom());
                            Date dateTimeOut = datesService.getDateTime(dateOut, shiftDetail.getTimeTo());

                            if (dateTimeOut.after(nextDay)) { // crosses 00:00
                                if (splitDetail) {
                                    if (summaries.size() >= summary.getDay() + 1) {
                                        nextDaySummary = summaries.get(summary.getDay());
                                    }
                                    if (shiftDetail.getDayFrom() == 1 && shiftDetail.getDayTo() == 2) {
                                        ScheduleDetail firstScheduleDetail = createScheduleDetail(summary, shiftDetail, dateTimeIn, nextDay);
                                        scheduleDetails.add(firstScheduleDetail);
                                        if (nextDaySummary != null) {
                                            ScheduleDetail secondScheduleDetail = createScheduleDetail(nextDaySummary, shiftDetail, nextDay, dateTimeOut);
                                            nextDayScheduleDetails.add(secondScheduleDetail);
                                        }

                                    }
                                    if (shiftDetail.getDayFrom() == 2 && shiftDetail.getDayTo() == 2) {
                                        if (nextDaySummary != null) {
                                            ScheduleDetail secondScheduleDetail = createScheduleDetail(nextDaySummary, shiftDetail, dateTimeIn, dateTimeOut);
                                            nextDayScheduleDetails.add(secondScheduleDetail);
                                        }
                                    }
                                }
                            } else {
                                ScheduleDetail scheduleDetail = createScheduleDetail(summary, shiftDetail, dateTimeIn, dateTimeOut);
                                scheduleDetails.add(scheduleDetail);
                            }

                        }
                    }
                    createWorkHoursDetailsForNightHoursWhenAutoGenIsOff(elementTypeMap, scheduleDetails);

                    scheduleDetails = removeDoubles(scheduleDetails);
                    createNightAndHolidayHours(weekends, holidays, elementTypeMap, scheduleDetails);
                    scheduleDetails = breakWorkHours(scheduleDetails);

                    boolean hasRoster = scheduleDetails.stream().anyMatch(d -> d.getElementType().getCode().equals("R"));
                    if (hasRoster) {
                        summary.setDisplayValue(elementTypeMap.get("R").getCode());
                    }
                    summary.setDetails(scheduleDetails);

                    if (nextDaySummary != null) {
                        nextDaySummary.setDetails(nextDayScheduleDetails);
                    }

                    commitInstances.addAll(scheduleDetails);
                    commitInstances.addAll(nextDayScheduleDetails);

                    /* calc summary hours based on details hours */
                    summary.setStartTime(scheduleDetails.stream().map(d -> d.getTimeIn()).min((d1, d2) -> d1.getTime() - d2.getTime() > 0 ? 1 : -1).get());
                    summary.setEndTime(scheduleDetails.stream().map(d -> d.getTimeOut()).max((d1, d2) -> d1.getTime() - d2.getTime() > 0 ? 1 : -1).get());
                    summary.setHours(scheduleDetails.stream().filter(d -> "WORK_HOURS".equals(d.getElementType().getCode())).mapToDouble(ScheduleDetail::getHours).sum());

                }

                /* calc header days and hours based on summaries and details */
                header.setWeekendDays((int) summaries.stream().filter(s -> "WEEKEND".equals(s.getElementType().getCode())).count());
                header.setHolidayDays((int) summaries.stream().filter(s -> "HOLIDAY".equals(s.getElementType().getCode())).count());
                header.setPlanDays((int) summaries.stream().filter(s -> s.getShift() != null).count());
                header.setHolidayWorkDays((int) summaries.stream().filter(s -> s.getShift() != null && s.getDetails() != null && s.getDetails().stream().filter(d -> "HOLIDAY_HOURS".equals(d.getElementType().getCode())).count() > 0).count());

                header.setNightHours(summaries.stream().filter(s -> s.getShift() != null && s.getDetails() != null).map(s -> s.getDetails()).flatMap(List::stream).filter(d -> "NIGHT_HOURS".equals(d.getElementType().getCode())).mapToDouble(d -> d.getHours()).sum());
                header.setHolidayWorkHours(summaries.stream().filter(s -> s.getShift() != null && s.getDetails() != null).map(s -> s.getDetails()).flatMap(List::stream).filter(d -> "HOLIDAY_HOURS".equals(d.getElementType().getCode())).mapToDouble(d -> d.getHours()).sum());
                header.setPlanHours(summaries.stream().filter(s -> s.getShift() != null && s.getDetails() != null).map(s -> s.getDetails()).flatMap(List::stream).filter(d -> "WORK_HOURS".equals(d.getElementType().getCode())).mapToDouble(d -> d.getHours()).sum());
                header.setPlanHoursMonth(header.getPlanHours());
                header.setPlanHoursPart(header.getPlanHours());

                /* calc base header days and hours */
                if (schedule.getBaseStandardSchedule() != null) {
                    ScheduleHeader baseScheduleHeader = getBaseScheduleHeader(schedule);
                    if (baseScheduleHeader != null) {
                        header.setBaseDays(baseScheduleHeader.getPlanDays());
                        header.setBaseHours(baseScheduleHeader.getPlanHours());
                    }
                } else {
                    header.setBaseDays(header.getPlanDays());
                    header.setBaseHours(header.getPlanHours());
                }
                calcSummaryType(schedule, weekends, holidays, elementTypeMap, summary.getDayDate(), ddMM, summary, summary.getStandardShift());
            }

        }
        /* commit all instances */
        dataManager.commit(new CommitContext(commitInstances));
        /* 2. FILL SCHEDULE END */
    }

    protected List<ScheduleDetail> removeDoubles(List<ScheduleDetail> scheduleDetails) {
        ArrayList<ScheduleDetail> newScheduleDetails = new ArrayList<>();
        for (ScheduleDetail scheduleDetail : scheduleDetails) {
            if (newScheduleDetails.stream().noneMatch(d ->
                    d.getTimeIn().equals(scheduleDetail.getTimeIn()) &&
                            d.getTimeOut().equals(scheduleDetail.getTimeOut()) &&
                            d.getElementType().equals(scheduleDetail.getElementType()))) {
                newScheduleDetails.add(scheduleDetail);
            }

        }
        return newScheduleDetails;
    }

    protected void calcSummaryType(StandardSchedule schedule, Set<Date> weekends, Set<String> holidays, Map<String, DicScheduleElementType> elementTypeMap, Date dayDate, String ddMM, ScheduleSummary summary, StandardShift standardShift) {
        /* calc element type and shift for schedule summary */
        if (standardShift != null) {
            if (standardShift.getShift() == null) {
                if (holidays.contains(ddMM)) {
                    /*log.info(dayDate + " = holiday day");*/
                    summary.setElementType(elementTypeMap.get("HOLIDAY"));
                } else {
                    /*log.info(dayDate + " = day off");*/
                    summary.setElementType(elementTypeMap.get("WEEKEND"));
                }
                if (summary.getDetails() != null) {
                    summary.setElementType(elementTypeMap.get("WORK_HOURS"));
                }
            } else {
                if (holidays.contains(ddMM)) {
                    if (schedule.getIsHolidayWorkDay()) {
                        /*log.info(dayDate + " = work day with holiday hours");*/
                        summary.setElementType(elementTypeMap.get("WORK_HOURS"));
                        summary.setShift(standardShift.getShift());
                    } else {
                        /*log.info(dayDate + " = holiday day");*/
                        summary.setElementType(elementTypeMap.get("HOLIDAY"));
                    }
                } else if (weekends.contains(dayDate)) {
                    if (schedule.getIsHolidayWorkDay()) {
                        /*log.info(dayDate + " = work day with holiday hours");*/
                        summary.setElementType(elementTypeMap.get("WORK_HOURS"));
                        summary.setShift(standardShift.getShift());
                    } else {
                        /*log.info(dayDate + " = day off");*/
                        summary.setElementType(elementTypeMap.get("WEEKEND"));
                    }
                } else {
                    /*log.info(dayDate + " = work day");*/
                    summary.setElementType(elementTypeMap.get("WORK_HOURS"));
                    summary.setShift(standardShift.getShift());
                }
            }
        }
    }


    protected void createWorkHoursDetailsForNightHoursWhenAutoGenIsOff(Map<String, DicScheduleElementType> elementTypeMap, List<ScheduleDetail> scheduleDetails) {
        /* if for night details is not exist work_hours details - it's creates work_hours shift details */
        if (!timecardConfig.getShiftDetailsAutoGeneration()) {
            List<ScheduleDetail> nightDetails = scheduleDetails.stream().filter(sd -> "NIGHT_HOURS".equals(sd.getElementType().getCode())).collect(Collectors.toList());
            if (!nightDetails.isEmpty()) {
                for (ScheduleDetail scheduleDetail : nightDetails) {
                    nightDetails.forEach(n -> {
                        List<ShiftDetail> workHoursElementsToTime = getWorkHoursElementsToTime(scheduleDetail);
                        if (workHoursElementsToTime.size() == 0) {
                            ScheduleDetail scheduleDetail1 = metadata.create(ScheduleDetail.class);
                            scheduleDetail1.setTimeIn(scheduleDetail.getTimeIn());
                            scheduleDetail1.setTimeOut(scheduleDetail.getTimeOut());
                            scheduleDetail1.setElementType(elementTypeMap.get("WORK_HOURS"));
                            scheduleDetail1.setDay(scheduleDetail.getDay());
                            scheduleDetail1.setDayDate(scheduleDetail.getDayDate());
                            scheduleDetail1.setSummary(scheduleDetail.getSummary());
                            scheduleDetail1.setHours(datesService.calculateDifferenceInHours(scheduleDetail1.getTimeOut(), scheduleDetail1.getTimeIn()));
                            scheduleDetails.add(scheduleDetail1);
                        }
                    });

                }
            }

        }
    }

    protected void createNightAndHolidayHours(Set<Date> weekends, Set<String> holidays, Map<String, DicScheduleElementType> elementTypeMap, List<ScheduleDetail> scheduleDetails) {
        /* create night and holiday hours */
        DicScheduleElementType nightHoursElement = elementTypeMap.get("NIGHT_HOURS");
        List<ScheduleDetail> workDetails = scheduleDetails.stream().filter(sd -> "WORK_HOURS".equals(sd.getElementType().getCode())).collect(Collectors.toList());

        for (ScheduleDetail workDetail : workDetails) {
            if (timecardConfig.getShiftDetailsAutoGeneration()) {
                List<NightPartDTO> nightParts = getNightParts(workDetail, nightHoursElement);
                if (!nightParts.isEmpty()) {
                    nightParts.forEach(n -> scheduleDetails.add(createNightDetail(n, nightHoursElement)));
                }
            } /* will not create night hours else*/

            /* holiday hours */
            DateFormat formatter = new SimpleDateFormat("dd.MM");
            String ddMM = formatter.format(workDetail.getDayDate());
            if (holidays.contains(ddMM)/* || weekends.contains(workDetail.getDayDate())*/) {
                ScheduleDetail holidayDetail = metadata.create(ScheduleDetail.class);
                holidayDetail.setTimeIn(workDetail.getTimeIn());
                holidayDetail.setTimeOut(workDetail.getTimeOut());
                holidayDetail.setElementType(elementTypeMap.get("HOLIDAY_HOURS"));
                holidayDetail.setDay(workDetail.getDay());
                holidayDetail.setDayDate(workDetail.getDayDate());
                holidayDetail.setSummary(workDetail.getSummary());
                holidayDetail.setHours(datesService.calculateDifferenceInHours(holidayDetail.getTimeOut(), holidayDetail.getTimeIn()));
                scheduleDetails.add(holidayDetail);
            }
            if (weekends.stream()
                    .anyMatch(date -> date != null
                            && workDetail.getDayDate() != null
                            && formatter.format(date)
                            .equals(formatter.format(workDetail.getDayDate())))) {
                ScheduleDetail weekendDetail = metadata.create(ScheduleDetail.class);
                weekendDetail.setTimeIn(workDetail.getTimeIn());
                weekendDetail.setTimeOut(workDetail.getTimeOut());
                weekendDetail.setElementType(elementTypeMap.get("WEEKEND_HOURS"));
                weekendDetail.setDay(workDetail.getDay());
                weekendDetail.setDayDate(workDetail.getDayDate());
                weekendDetail.setSummary(workDetail.getSummary());
                weekendDetail.setHours(datesService.calculateDifferenceInHours(weekendDetail.getTimeOut(), weekendDetail.getTimeIn()));
                scheduleDetails.add(weekendDetail);
            }
        }
    }

    protected List<ScheduleDetail> breakWorkHours(List<ScheduleDetail> scheduleDetails) {
        /* break work hours details if work hours detail period intersects with break period */
        boolean stop = false;
        List<ScheduleDetail> breaks = scheduleDetails.stream().filter(sd -> "BREAK".equals(sd.getElementType().getCode())).collect(Collectors.toList());
        while (!stop) {

            int n = 0;
            for (ScheduleDetail breakDetail : breaks) {
                List<ScheduleDetail> works = scheduleDetails.stream().filter(sd ->
                        ("WORK_HOURS".equals(sd.getElementType().getCode())
                                || "NIGHT_HOURS".equals(sd.getElementType().getCode())
                                || "HOLIDAY_HOURS".equals(sd.getElementType().getCode())
                                || "WEEKEND_HOURS".equals(sd.getElementType().getCode()))
                                && !breakDetail.getTimeIn().before(sd.getTimeIn())
                                && breakDetail.getTimeIn().before(sd.getTimeOut()))
                        .collect(Collectors.toList());

                if (!works.isEmpty()) {
                    for (ScheduleDetail workDetail : works) {

                        ScheduleDetail newWorkDetail = metadata.create(ScheduleDetail.class);
                        newWorkDetail.setTimeIn(breakDetail.getTimeOut());
                        newWorkDetail.setTimeOut(workDetail.getTimeOut());
                        newWorkDetail.setElementType(workDetail.getElementType());
                        newWorkDetail.setDay(workDetail.getDay());
                        newWorkDetail.setDayDate(workDetail.getDayDate());
                        newWorkDetail.setSummary(workDetail.getSummary());
                        newWorkDetail.setHours(datesService.calculateDifferenceInHours(newWorkDetail.getTimeOut(), newWorkDetail.getTimeIn()));
                        if (newWorkDetail.getHours() != 0) {
                            scheduleDetails.add(newWorkDetail);
                        }
                        workDetail.setTimeOut(breakDetail.getTimeIn());
                        workDetail.setHours(datesService.calculateDifferenceInHours(workDetail.getTimeOut(), workDetail.getTimeIn()));
                    }
                    n++;
                }
            }
            if (n == 0)
                stop = true;
        }
        return scheduleDetails.stream().filter(d -> d.getHours() > 0).collect(Collectors.toList());
    }

    protected ScheduleDetail createScheduleDetail(ScheduleSummary summary, ShiftDetail shiftDetail, Date dateTimeIn, Date dateTimeOut) {
        ScheduleDetail scheduleDetail = metadata.create(ScheduleDetail.class);
        scheduleDetail.setTimeIn(dateTimeIn);
        scheduleDetail.setTimeOut(dateTimeOut);
        scheduleDetail.setElementType(shiftDetail.getElementType());
        scheduleDetail.setDay(summary.getDay());
        scheduleDetail.setDayDate(summary.getDayDate());
        scheduleDetail.setSummary(summary);
        scheduleDetail.setHours(datesService.calculateDifferenceInHours(scheduleDetail.getTimeOut(), scheduleDetail.getTimeIn()));
        return scheduleDetail;
    }

    @Override
    public int getAllHolidays(kz.uco.tsadv.modules.timesheet.model.Calendar calendar, Date absenceStartDate, Date
            absenceEndDate) {
        if (calendar == null) {
            return 0;
        }
        Map<Integer, Object> map = new HashMap();
        map.put(1, calendar.getId());
        List<Object[]> list = new ArrayList<>();
        String query = " SELECT calendarHol.start_date, calendarHol.end_date," +
                " calendarHol.action_date_from, calendarHol.action_date_to FROM tsadv_calendar_holiday calendarHol " +
                " WHERE calendarHol.calendar_id = ?1 " +
                " AND calendarHol.start_date <=calendarHol.end_date and calendarHol.day_type='HOLIDAY'" +
                " AND calendarHol.delete_ts IS NULL";
        list.addAll(commonService.emNativeQueryResultList(query, map));
        return getAllDay(list, absenceStartDate, absenceEndDate);
    }

    protected List<ShiftDetail> getWorkHoursElementsToTime(ScheduleDetail scheduleDetail) {
        List<ShiftDetail> shiftDetails = new ArrayList<>();
        if (scheduleDetail.getSummary().getShift() == null) {
            return shiftDetails;
        }
        shiftDetails = commonService.getEntities(ShiftDetail.class,
                "select e from tsadv$ShiftDetail e where e.shift.id = :shiftId and e.elementType.code = 'WORK_HOURS' " +
                        " and e.dayFrom = :dayFrom and e.dayTo = :dayTo and e.timeFrom =:endDate and e.timeTo = :startDate and e.deleteTs is null",
                ParamsMap.of("shiftId", scheduleDetail.getSummary().getShift().getId(), "dayFrom", scheduleDetail.getDay(), "dayTo", scheduleDetail.getDay(),
                        "startDate", scheduleDetail.getTimeIn(), "endDate", scheduleDetail.getTimeOut()),
                View.LOCAL);
        return shiftDetails;
    }


    public List<Timesheet> getTimesheets(Map<String, Object> params) {
        Date startDate = (Date) params.get("startDate");
        Date endDate = (Date) params.get("endDate");
        boolean loadFullData = (boolean) params.get("loadFullData");
        OrganizationGroupExt organizationGroup = (OrganizationGroupExt) params.get("organizationGroup");
        PositionGroupExt positionGroup = (PositionGroupExt) params.get("positionGroup");
        AssignmentExt assignmentExt = (AssignmentExt) params.get("assignmentExt");
        PersonGroupExt personGroup = (PersonGroupExt) params.get("personGroup");
        Boolean enableInclusions = (Boolean) params.get("enableInclusions");
        return getTimesheets(organizationGroup, positionGroup, personGroup, startDate, endDate,
                0, 0, loadFullData, assignmentExt, enableInclusions);
    }


    public List<Timesheet> getTimesheets(OrganizationGroupExt organizationGroup, PositionGroupExt positionGroup,
                                         PersonGroupExt personGroup, Date startDate, Date endDate, int firstResult, int maxResults,
                                         boolean loadFullData, AssignmentExt assignmentExt, Boolean enableInclusions) {
        List<Timesheet> list = new ArrayList<>();
        List<Object[]> objects;
        String queryString = "";
        Object firstParameter = null;

        if (organizationGroup != null) {
            queryString = getQueryForOrganization(assignmentExt, enableInclusions);
            if (enableInclusions) {
                firstParameter = "%" + organizationGroup.getId().toString() + "%";
            } else {
                firstParameter = organizationGroup.getId();
            }
        }
        if (positionGroup != null) {
            queryString = getQueryForPosition(assignmentExt);
            firstParameter = positionGroup.getId();
        }

        if (personGroup != null) {
            queryString = getQueryForPerson(assignmentExt);
            firstParameter = personGroup.getId();
        }

        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createNativeQuery(queryString);

            if (!loadFullData) {
                query.setFirstResult(firstResult);
                query.setMaxResults(maxResults);
            }

            query.setParameter(1, firstParameter);
            query.setParameter(2, startDate);
            query.setParameter(3, endDate);
            query.setParameter(4, fieldsDelimiter);
            query.setParameter(5, entityDelimiter);
            if (assignmentExt != null) {
                query.setParameter(6, assignmentExt.getId());
            }

            objects = query.getResultList();

            if (objects != null && !objects.isEmpty()) {
                for (Object[] row : objects) {
                    Timesheet timesheet = new Timesheet();
                    DateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
                    ArrayList<AssignmentSchedule> assignmentSchedules = new ArrayList<>();
                    timesheet.setName(row[0] == null ? "" : (String) row[0]);

                    AssignmentGroupExt assignmentGroup = metadata.create(AssignmentGroupExt.class);

                    String assignmentGroupString = (String) row[1];
                    if (assignmentGroupString != null && !assignmentGroupString.isEmpty()) {
                        String[] strings = assignmentGroupString.split(fieldsDelimiter);
                        assignmentGroup.setId(UUID.fromString(strings[0]));
                        assignmentGroup.setLegacyId(strings[1].equals("(none)") ? null : strings[1]);
                    }
                    timesheet.setAssignmentGroup(assignmentGroup);

                    String assignmentSchedulesString = (String) row[2];
                    if (assignmentSchedulesString != null && !assignmentSchedulesString.isEmpty()) {
                        String[] assignmentScheduleEntities = assignmentSchedulesString.split(entityDelimiter);
                        for (String entity : assignmentScheduleEntities) {
                            AssignmentSchedule assignmentSchedule = new AssignmentSchedule();
                            String[] strings = entity.split(fieldsDelimiter);
                            assignmentSchedule.setName(strings[0]);

                            int colorSetId = Integer.parseInt(strings[3]);
                            assignmentSchedule.setColorsSet(MaterialDesignColorsEnum.fromId(colorSetId));

                            UUID assignmentScheduleId = UUID.fromString(strings[4]);
                            assignmentSchedule.setId(assignmentScheduleId);

                            UUID offsetId = UUID.fromString(strings[5]);
                            StandardOffset offset = new StandardOffset();
                            offset.setId(offsetId);
                            offset.setLegacyId(strings[8].equals("(none)") ? null : strings[8]);
                            assignmentSchedule.setOffset(offset);

                            Date asStartDate = null;
                            Date asEndDate = null;
                            try {
                                asStartDate = strings[1] != null ? format.parse(strings[1]) : null;
                                asEndDate = strings[2] != null ? format.parse(strings[2]) : null;
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            StandardSchedule standardSchedule = new StandardSchedule();
                            standardSchedule.setId(UUID.fromString(strings[6]));
                            standardSchedule.setLegacyId(strings[7].equals("(none)") ? null : strings[7]);
                            assignmentSchedule.setSchedule(standardSchedule);

                            assignmentGroup.setLegacyId(strings[9].equals("(none)") ? null : strings[9]);
                            assignmentSchedule.setAssignmentGroup(assignmentGroup);

                            assignmentSchedule.setStartDate(asStartDate);
                            assignmentSchedule.setEndDate(asEndDate);
                            assignmentSchedules.add(assignmentSchedule);
                        }
                    }
                    timesheet.setAssignmentSchedules(assignmentSchedules);
                    list.add(timesheet);
                }
            }
        }

        return list;

    }

    @Override
    public int getTimesheetsCount(OrganizationGroupExt organizationGroup, PositionGroupExt
            positionGroup, PersonGroupExt personGroup, Date startDate, Date endDate, Boolean enableInclusions) {
        return getTimesheets(organizationGroup, positionGroup, personGroup, startDate, endDate, 0, 0, true, null, enableInclusions).size();
    }

    protected String getQueryForOrganization(AssignmentExt assignmentExt, Boolean enableInclusions) {
        return "SELECT " +
                "p.last_name || ' ' || p.first_name || ' ' || coalesce(p.middle_name,'') || '(' || coalesce(p.employee_number,'') || ')',\n" +
                "a.group_id || ?4 || coalesce(a.legacy_id, '(none)'),\n" +
                "string_agg(ss.schedule_name || '-' || tso.offset_display || ?4 || tas.start_date || ?4 || tas.end_date || ?4 || tas.colors_set || ?4 || tas.id || ?4 || tso.id || ?4 || ss.id || ?4 || coalesce(ss.legacy_id, '(none)') || ?4 || coalesce(tso.legacy_id, '(none)') || ?4 || coalesce(a.legacy_id, '(none)'),  ?5)" +
                " FROM base_person p\n" +
                "  JOIN base_assignment a\n" +
                "    ON a.person_group_id = p.group_id\n" +
                (assignmentExt == null ? "" : " AND a.id = ?6 ") +
                "  JOIN tsadv_organization_structure os\n" +
                "    ON os.organization_group_id = a.organization_group_id\n" +
                (enableInclusions ? " AND os.path LIKE ?1\n" : " AND os.organization_group_id = ?1\n") +
                "join tsadv_dic_assignment_status ass on ass.id = a.assignment_status_id\n" +
                "  join tsadv_dic_person_type pt on pt.id = p.type_id\n" +
                "  left join tsadv_assignment_schedule tas on tas.assignment_group_id = a.group_id\n" +
                "                                             and ?2 <= tas.end_date\n" +
                "                                             and ?3 >= tas.start_date\n" +
                "                                             AND tas.delete_ts is NULL\n" +
                "  left join tsadv_standard_schedule ss on ss.id = tas.schedule_id\n" +
                "  left JOIN  tsadv_standard_offset tso on tso.id = tas.offset_id\n" +
                "where ?2 <= p.end_date\n" +
                "and ?3 >= p.start_date\n" +
                "and ?2 <= a.end_date\n" +
                "and ?3 >= a.start_date\n" +
                "and (ass.code = 'ACTIVE' OR ass.code = 'SUSPENDED')\n" +
                "  AND (pt.code = 'EMPLOYEE' or pt.code = 'PAIDINTERN')\n" +
                "group by p.first_name, p.last_name, p.middle_name, p.employee_number, a.group_id, a.legacy_id" +
                " order by p.last_name";
    }

    protected String getQueryForPosition(AssignmentExt assignmentExt) {
        return "SELECT p.last_name || ' ' || p.first_name || ' ' || coalesce(p.middle_name,'') || '(' || coalesce(p.employee_number,'') || ')',\n" +
                "a.group_id || ?4 || coalesce(a.legacy_id, '(none)'),\n" +
                "string_agg(ss.schedule_name || '-' || tso.offset_display || ?4 || tas.start_date || ?4 || tas.end_date || ?4 || tas.colors_set || ?4 || tas.id || ?4 || tso.id || ?4 || ss.id || ?4 || coalesce(ss.legacy_id, '(none)') || ?4 || coalesce(tso.legacy_id, '(none)') || ?4 || coalesce(a.legacy_id, '(none)'),  ?5)" +
                "FROM base_person p\n " +
                "  JOIN base_assignment a" +
                "        ON a.person_group_id = p.group_id\n" +
                (assignmentExt == null ? "" : " AND a.id=?6 ") +
                "  JOIN tsadv_dic_assignment_status ass\n" +
                "    ON ass.id = a.assignment_status_id\n" +
                "  JOIN tsadv_dic_person_type pt \n" +
                "    ON pt.id = p.type_id\n" +
                "  LEFT JOIN tsadv_assignment_schedule tas\n" +
                "    ON tas.assignment_group_id = a.group_id" +
                "                        AND ?2 <= tas.end_date" +
                "                        AND ?3 >= tas.start_date\n" +
                "                        AND tas.delete_ts is NULL\n" +
                "  LEFT JOIN tsadv_standard_schedule ss\n" +
                "    ON ss.id = tas.schedule_id\n" +
                "  LEFT JOIN tsadv_standard_offset tso \n" +
                "    ON tso.id = tas.offset_id\n" +
                "WHERE ?2 <= p.end_date\n" +
                "      AND ?3 >= p.start_date\n" +
                "      AND ?2 <= a.end_date\n" +
                "      AND ?3 >= a.start_date\n" +
                "      AND (ass.code = 'ACTIVE' OR ass.code = 'SUSPENDED')\n" +
                "      AND (pt.code = 'EMPLOYEE' or pt.code = 'PAIDINTERN')\n" +
                "      AND a.position_group_id = ?1\n" +
                "GROUP BY p.first_name, p.last_name, p.middle_name, p.employee_number, a.group_id, a.legacy_id" +
                " order by p.last_name;";
    }

    protected String getQueryForPerson(AssignmentExt assignmentExt) {
        return "SELECT p.last_name || ' ' || p.first_name || ' ' || coalesce(p.middle_name,'') || '(' || coalesce(p.employee_number,'') || ')',\n" +
                "a.group_id || ?4 || coalesce(a.legacy_id, '(none)'),\n" +
                "string_agg(ss.schedule_name || '-' || tso.offset_display || ?4 || tas.start_date || ?4 || tas.end_date || ?4 || tas.colors_set || ?4 || tas.id || ?4 || tso.id || ?4 || ss.id || ?4 || coalesce(ss.legacy_id, '(none)') || ?4 || coalesce(tso.legacy_id, '(none)') || ?4 || coalesce(a.legacy_id, '(none)'),  ?5)" +
                "FROM base_person p\n " +
                "  JOIN base_assignment a" +
                "        ON a.person_group_id = p.group_id\n" +
                "      AND a.person_group_id = ?1\n" +
                (assignmentExt == null ? "" : " AND a.id=?6 ") +
                "  JOIN tsadv_dic_assignment_status ass\n" +
                "    ON ass.id = a.assignment_status_id\n" +
                "  JOIN tsadv_dic_person_type pt \n" +
                "    ON pt.id = p.type_id\n" +
                "  LEFT JOIN tsadv_assignment_schedule tas\n" +
                "    ON tas.assignment_group_id = a.group_id" +
                "                        AND ?2 <= tas.end_date" +
                "                        AND ?3 >= tas.start_date\n" +
                "                        AND tas.delete_ts is NULL\n" +
                "  LEFT JOIN tsadv_standard_schedule ss\n" +
                "    ON ss.id = tas.schedule_id\n" +
                "  LEFT JOIN tsadv_standard_offset tso \n" +
                "    ON tso.id = tas.offset_id\n" +
                " WHERE greatest(a.start_date, ?2) between p.start_date and p.end_date\n " +//todo
                "      AND ?2 <= a.end_date\n" +
                "      AND ?3 >= a.start_date\n" +
                "     and (ass.code = 'ACTIVE' OR ass.code = 'SUSPENDED')\n" +
                "     AND (pt.code = 'EMPLOYEE' or pt.code = 'PAIDINTERN')\n" +
//                "      AND a.position_group_id = ?1\n" +
                "GROUP BY p.first_name, p.last_name, p.middle_name, p.employee_number, a.group_id, a.legacy_id" +
                " order by p.last_name;";
    }


    protected int getAllDay(List<Object[]> list, Date absenceStartDate, Date absenceEndDate) {
        int holidayDays = 0;
        for (Object[] o : list) {
            Date startDate = (Date) o[0], endDate = (Date) o[1];
            Date transferStartDate = (Date) (o[2] != null ? o[2] : o[0]);
            Date transferEndDate = o[3] != null ? (Date) o[3] : CommonUtils.getEndOfTime();

            Pair<Calendar, Calendar> intersectPeriod = datesService.getItersectionPeriod(absenceStartDate, absenceEndDate, transferStartDate, transferEndDate);
            if (intersectPeriod != null) {
                for (int i = intersectPeriod.getKey().get(Calendar.YEAR); i <= intersectPeriod.getValue().get(Calendar.YEAR); i++) {
                    Calendar c1 = Calendar.getInstance();
                    c1.setTime(startDate);
                    c1.set(Calendar.YEAR, i);
                    Calendar c2 = Calendar.getInstance();
                    c2.setTime(endDate);
                    c2.set(Calendar.YEAR, i);
                    holidayDays += datesService.getIntersectionLengthInDays(c1.getTime(), c2.getTime(), intersectPeriod.getKey().getTime(), intersectPeriod.getValue().getTime());
                }
            }
        }
        return holidayDays;
    }

    protected ScheduleDetail createNightDetail(NightPartDTO nightPartDTO, DicScheduleElementType nightHoursElement) {
        ScheduleDetail nightDetail = metadata.create(ScheduleDetail.class);

        Date nightHoursFrom = nightPartDTO.getNightHoursFrom();
        Date nightHoursTo = nightPartDTO.getNightHoursTo();

        Date firstDate = datesService.getFirstDate(nightHoursFrom, nightPartDTO.getScheduleDetail().getTimeIn());
        Date timeInLimit = nightPartDTO.getScheduleDetail().getTimeIn();
        if (!firstDate.after(nightHoursFrom) && !nightHoursFrom.before(timeInLimit)) { //second check - for not out from night hours start time
            nightDetail.setTimeIn(nightHoursFrom);
        } else {
            nightDetail.setTimeIn(timeInLimit);
        }

        Date secondDate = datesService.getSecondDate(nightHoursTo, nightPartDTO.getScheduleDetail().getTimeOut());
        Date timeOutLimit = nightPartDTO.getScheduleDetail().getTimeOut();

        if (timeOutLimit.after(nightHoursTo)) {
            timeOutLimit = nightHoursTo;
        }
        if (!secondDate.after(nightHoursTo) && !secondDate.after(timeOutLimit)) { //second check - for not out from night hours end time
            nightDetail.setTimeOut(secondDate);
        } else {
            nightDetail.setTimeOut(timeOutLimit);
        }
        nightDetail.setElementType(nightHoursElement);
        nightDetail.setDay(nightPartDTO.getScheduleDetail().getDay());
        nightDetail.setDayDate(nightPartDTO.getScheduleDetail().getDayDate());
        nightDetail.setSummary(nightPartDTO.getScheduleDetail().getSummary());
        nightDetail.setHours(datesService.calculateDifferenceInHours(nightDetail.getTimeOut(), nightDetail.getTimeIn()));

        return nightDetail;
    }

    protected ScheduleSummary getBaseSummary(StandardSchedule schedule, ScheduleSummary summary) {
        StandardSchedule baseSchedule = schedule.getBaseStandardSchedule();
        if (baseSchedule == null) {
            return summary;
        }
        ScheduleHeader baseHeader = getBaseScheduleHeader(schedule);
        if (baseHeader != null) {
            List<ScheduleSummary> baseScheduleSummaries = commonService.getEntities(ScheduleSummary.class,
                    "  select e " +
                            "     from tsadv$ScheduleSummary e " +
                            "    where e.header.id = :baseHeaderId " +
                            "      and e.deleteTs is null " +
                            "      and e.dayDate = :date " +
                            " order by e.createTs desc ",
                    ParamsMap.of("baseHeaderId", baseHeader.getId(),
                            "date", summary.getDayDate())
                    , View.LOCAL);

            if (baseScheduleSummaries != null && !baseScheduleSummaries.isEmpty()) {
                return baseScheduleSummaries.get(0);
            }
        }
        return summary;

    }

    protected ScheduleHeader getBaseScheduleHeader(StandardSchedule schedule) {
        ScheduleHeader baseHeader = null;
        List<ScheduleHeader> baseScheduleHeaders = commonService.getEntities(ScheduleHeader.class,
                "  select e " +
                        "     from tsadv$ScheduleHeader e join e.offset o " +
                        "    where e.schedule.id = :baseScheduleId " +
                        "      and e.deleteTs is null " +
                        "      and o.offsetDisplayDays = 0 " +
                        " order by e.createTs desc ",
                ParamsMap.of("baseScheduleId", schedule.getBaseStandardSchedule().getId())
                , View.LOCAL);
        if (baseScheduleHeaders != null && !baseScheduleHeaders.isEmpty()) {
            baseHeader = baseScheduleHeaders.get(0);
        }
        return baseHeader;
    }

    protected List<NightPartDTO> getNightParts(ScheduleDetail detail, DicScheduleElementType nightHoursElement) {
        List<NightPartDTO> nightPartsMap = new ArrayList<>();

        Date nightHoursFrom = datesService.getDateTime(detail.getTimeIn(), nightHoursElement.getTimeFrom());
        Date nightHoursTo = datesService.getDateTime(detail.getTimeIn(), nightHoursElement.getTimeTo());

        /* 1st - we check when start is day before */
        nightHoursFrom = DateUtils.addDays(nightHoursFrom, -1);
        if (datesService.intersectPeriods(nightHoursFrom, nightHoursTo, detail.getTimeIn(), detail.getTimeOut())) {
            nightPartsMap.add(new NightPartDTO(null, detail, nightHoursFrom, nightHoursTo));
        }

        /* 2nd - we check - like as it, but incrementing 2nd day if the same */
        nightHoursFrom = datesService.getDateTime(detail.getTimeIn(), nightHoursElement.getTimeFrom());
        nightHoursTo = datesService.getDateTime(detail.getTimeOut(), nightHoursElement.getTimeTo());

        if (!nightHoursTo.after(nightHoursFrom)) {
            nightHoursTo = DateUtils.addDays(nightHoursTo, 1);
        }
        if (datesService.intersectPeriods(nightHoursFrom, nightHoursTo, detail.getTimeIn(), detail.getTimeOut())) {
            nightPartsMap.add(new NightPartDTO(null, detail, nightHoursFrom, nightHoursTo));
        }
        /* 3rd - we incrementing start date for 1 */
        /* but we check that not same day */
        if (!DateUtils.isSameDay(detail.getTimeIn(), detail.getTimeOut())) {
            nightHoursFrom = datesService.getDateTime(detail.getTimeOut(), nightHoursElement.getTimeFrom());
            nightHoursTo = datesService.getDateTime(detail.getTimeOut(), nightHoursElement.getTimeTo());
            nightHoursTo = DateUtils.addDays(nightHoursTo, 1);
            if (!nightHoursTo.after(nightHoursFrom)) {
                nightHoursTo = DateUtils.addDays(nightHoursTo, 1);
            }
            if (datesService.intersectPeriods(nightHoursFrom, nightHoursTo, detail.getTimeIn(), detail.getTimeOut())) {
                nightPartsMap.add(new NightPartDTO(null, detail, nightHoursFrom, nightHoursTo));
            }
        }
        return nightPartsMap;
    }

    @Override
    public int getDateDiffByCalendar(String calendarCode, Date startDate, Date endDate, Boolean ignoreHolidays) {

        checkIsDatesNull(startDate, endDate);
        kz.uco.tsadv.modules.timesheet.model.Calendar calendar = dataManager.load(kz.uco.tsadv.modules.timesheet.model.Calendar.class)
                .query("select e from tsadv$Calendar e where e.calendar = :code")
                .parameter("code", calendarCode)
                .view("calendar.view")
                .list().stream().findFirst().orElse(null);

        return calcDays(startDate, endDate, ignoreHolidays, calendar);
    }

    protected void checkIsDatesNull(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            throw new NullPointerException("Dates must  not be null");
        }
    }

    @Override
    public int getDateDiffByCalendar(kz.uco.tsadv.modules.timesheet.model.Calendar calendar, Date startDate, Date endDate, Boolean ignoreHolidays) {
        checkIsDatesNull(startDate, endDate);
        return calcDays(startDate, endDate, ignoreHolidays, calendar);
    }


    protected int calcDays(Date startDate, Date endDate, Boolean ignoreHolidays, kz.uco.tsadv.modules.timesheet.model.Calendar calendar) {
        if (calendar == null) {
            throw new NullPointerException("Calendar not exists");
        }
        checkIsDatesNull(startDate, endDate);

        if (ignoreHolidays==null){
            ignoreHolidays=false;
        }

        if (endDate.compareTo(startDate) <= 0) {
            throw new ItemNotFoundException(messages.getMainMessage("startDate.validatorMsg"));
        }

        int days = 0;

        days = datesService.getFullDaysCount(startDate, endDate);

        if (!ignoreHolidays) {
            days = days - timesheetService.getAllHolidays(calendar, startDate, endDate);
        }

        return days;
    }


}