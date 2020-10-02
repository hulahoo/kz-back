package kz.uco.tsadv.service;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.chile.core.model.Range;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.dictionary.DicAbsenceType;
import kz.uco.tsadv.modules.personal.dictionary.DicAssignmentStatus;
import kz.uco.tsadv.modules.personal.dictionary.DicBusinessTripType;
import kz.uco.tsadv.modules.personal.enums.BusinessTripOrderStatus;
import kz.uco.tsadv.modules.personal.enums.BusinessTripOrderType;
import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.Absence;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.BusinessTrip;
import kz.uco.tsadv.modules.timesheet.config.TimecardConfig;
import kz.uco.tsadv.modules.timesheet.dictionary.DicScheduleElementType;
import kz.uco.tsadv.modules.timesheet.enums.MaterialDesignColorsEnum;
import kz.uco.tsadv.modules.timesheet.model.*;
import kz.uco.tsadv.modules.timesheet.model.dto.NightPartDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.Temporal;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

//Be aware - because in AA exists overriding bean!
@Service(TimecardService.NAME)
public class TimecardServiceBean implements TimecardService {

    protected static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String NIGHT_HOURS = "NIGHT_HOURS";
    public static final String HOLIDAY_HOURS = "HOLIDAY_HOURS";
    public static final String WEEKEND_HOURS = "WEEKEND_HOURS";
    public static final String WORK_HOURS = "WORK_HOURS";
    protected DateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
    public static final String fieldsDelimiter = "@@";
    public static final String entityDelimiter = "@@@";

    @Inject
    protected CommonService commonService;
    @Inject
    protected DatesService datesService;
    @Inject
    private EmployeeService employeeService;
    @Inject
    protected Metadata metadata;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected Persistence persistence;
    @Inject
    private TimecardConfig timecardConfig;
    @Inject
    protected UserSessionSource userSessionSource;

    @Override
    public void formTimecard(Date monthStart, Collection<Timecard> timecards) throws Exception {
        TimecardLog timecardLog = initTimecardLog(timecards.size());
        monthStart = datesService.getMonthBegin(monthStart);
        Date startDate = monthStart;
        Date endDate = DateUtils.addDays(DateUtils.addMonths(startDate, 1), -1);

        //Long startTime1 = System.currentTimeMillis();
        try {
            if (timecardConfig.getOffsetId() != null) {
                List<AssignmentGroupExt> assignmentGroupsWithNoSchedules = getAssignmentGroupsWithNoSchedules(timecards, startDate, endDate);
                setDefaultSchedule(assignmentGroupsWithNoSchedules, startDate, endDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
            addErrorForTimecardLog(timecardLog, null, e);
            finishTimecardLog(timecardLog);
        }
        //System.out.println("1 getAssignmentGroupsWithNoSchedules time = " + (System.currentTimeMillis() - startTime1));

        //Long startTime2 = System.currentTimeMillis();
//        Map<AssignmentSchedule, Object> assignmentScheduleWithObjectsDtoList = getAssignmentScheduleWithObjectsDto(timecards, startDate, endDate);
        List<AssignmentScheduleWithObjectsDto> assignmentScheduleWithObjectsDtoList = getAssignmentScheduleWithObjectsDto(timecards, startDate, endDate);
        //System.out.println("2 getAssignmentScheduleWithObjectsDto time = " + (System.currentTimeMillis() - startTime2));


        if (assignmentScheduleWithObjectsDtoList.isEmpty()) {
            return;
        }
        List<ScheduleHeader> scheduleHeaders = new ArrayList<>();

        List<StandardOffset> offsets = assignmentScheduleWithObjectsDtoList.stream().map(AssignmentScheduleWithObjectsDto::getAssignmentSchedule).map(AssignmentSchedule::getOffset).collect(Collectors.toList());
        scheduleHeaders.addAll(getScheduleHeaders(offsets, startDate));

        //Long startTime3 = System.currentTimeMillis();
        for (StandardOffset offset : offsets) {
            StandardOffset finalOffset = offset;
            ScheduleHeader headerForOfset = scheduleHeaders.stream().filter(h -> h.getOffset().equals(finalOffset)).findAny().orElse(null);
            if (headerForOfset == null) {
                offset = dataManager.reload(offset, "standardOffset.view");
                throw new Exception("scheduleNotFormed" + offset.getOffsetScheduleName());
            }
        }
        //System.out.println("3 reload offsets ??? time = " + (System.currentTimeMillis() - startTime3));

        List<UUID> assignmentGroupIds = timecards.stream().filter(t -> t.getAssignmentGroupId() != null)
                .map(Timecard::getAssignmentGroupId).collect(Collectors.toList());
        Map<UUID, PersonGroupExt> assignmentGroupForPersonGroups = getPersonGroupsForAssignmentGroup(assignmentGroupIds);

        //Long startTime4 = System.currentTimeMillis();
        List<Absence> absences = getAbsences(assignmentGroupForPersonGroups.values(), startDate, endDate);
        //System.out.println("4 getAbsences time = " + (System.currentTimeMillis() - startTime4));

        //Long startTime5 = System.currentTimeMillis();
        List<BusinessTrip> businessTrips = getBusinessTrips(assignmentGroupForPersonGroups.values(), startDate, endDate);
        //System.out.println("5 getBusinessTrips time = " + (System.currentTimeMillis() - startTime5));

        Set<AssignmentGroupExt> assignmentGroups = new HashSet<>();

        assignmentGroups.addAll(assignmentScheduleWithObjectsDtoList.stream().map(AssignmentScheduleWithObjectsDto::getAssignmentSchedule).map(AssignmentSchedule::getAssignmentGroup).collect(Collectors.toList()));

        //Long startTime6 = System.currentTimeMillis();
        List<TimecardCorrection> timecardCorrections = getTimecardCorrections(assignmentGroups, startDate, endDate);
        //System.out.println("6 getTimecardCorrections time = " + (System.currentTimeMillis() - startTime6));

        //Long startTime7 = System.currentTimeMillis();
        List<TimecardHeader> timecardHeaders = createTimecardHeaders(endDate, assignmentScheduleWithObjectsDtoList, startDate, assignmentGroups, timecardCorrections);
        //System.out.println("7 createTimecardHeaders time = " + (System.currentTimeMillis() - startTime7));

        //Long startTime8 = System.currentTimeMillis();
        ArrayList<WorkedHoursSummary> summaries = createWorkedHoursSummariesForHeaders(timecardHeaders,
                startDate,
                endDate,
                assignmentGroups,
                assignmentScheduleWithObjectsDtoList.stream().map(AssignmentScheduleWithObjectsDto::getAssignmentSchedule).collect(Collectors.toSet()),
                scheduleHeaders,
                absences,
                businessTrips,
                assignmentGroupForPersonGroups,
                timecardLog);
        //System.out.println("8 createWorkedHoursSummariesForHeaders time = " + (System.currentTimeMillis() - startTime8));

        //Long startTime9 = System.currentTimeMillis();
        List<TimecardDeviation> deviationsForMonth = getDeviationsForMonth(new ArrayList<>(timecards), monthStart, endDate);
        if (!deviationsForMonth.isEmpty()) {
            addDeviationsTest(deviationsForMonth, summaries);
        }
        //System.out.println("9 getDeviationsForMonth time = " + (System.currentTimeMillis() - startTime9));
        finishTimecardLog(timecardLog);
    }

    private TimecardLog initTimecardLog(int timecardsCount) {
        TimecardLog timecardLog = metadata.create(TimecardLog.class);
        timecardLog.setInitiatorLogin(userSessionSource.getUserSession().getUser().getLogin());
        Date startDate = new Date();
        timecardLog.setStartDate(startDate);
        timecardLog.setTimecardsCount(timecardsCount);
        timecardLog.setSuccess(true);
        return timecardLog;
    }

    private void finishTimecardLog(TimecardLog timecardLog) {
        timecardLog.setEndDate(new Date());
        timecardLog.setDurationInSeconds((timecardLog.getEndDate().getTime() - timecardLog.getStartDate().getTime()) / 1000);
        dataManager.commit(timecardLog);
    }

    @Override
    public void setCorrective(Boolean corrective, Collection<Timecard> timecards, Date startDate, Date endDate) {
        List<TimecardHeader> timecardHeaders = new ArrayList<>();
        timecards.forEach(t -> timecardHeaders.addAll(t.getTimecardHeaders()));
        Set<AssignmentGroupExt> assignmentGroups = new HashSet<>();

        timecardHeaders.forEach(h -> assignmentGroups.add(h.getAssignmentGroup()));
        if (corrective) {
            createTimecardCorrections(assignmentGroups, startDate, endDate);
        } else {
            removeTimecardCorrections(assignmentGroups, startDate, endDate);
        }
    }

    private void removeTimecardCorrections(Set<AssignmentGroupExt> assignmentGroups, Date startDate, Date endDate) {
        List<UUID> uuidList = assignmentGroups.stream().map(AssignmentGroupExt::getId).collect(Collectors.toList());

        List<TimecardHeader> timecardHeaders = commonService.getEntities(TimecardHeader.class, "select e from tsadv$TimecardHeader e " +
                        "where e.assignmentGroup.id in :uuidList and e.effectiveStartDate >= :startDate and e.effectiveEndDate <= :endDate",
                ParamsMap.of("uuidList", uuidList, "startDate", startDate, "endDate", endDate), "timecardHeader-with-correction");

        CommitContext commitContext = new CommitContext();
        for (TimecardHeader timecardHeader : timecardHeaders) {
            timecardHeader.setTimecardCorrection(null);
            commitContext.addInstanceToCommit(timecardHeader);
        }
        dataManager.commit(commitContext);

        String idsAsString = getIdsAsString(assignmentGroups.stream().map(AssignmentGroupExt::getId).collect(Collectors.toList()));
        String queryString = "delete from TSADV_TIMECARD_CORRECTION tc where tc.ASSIGNMENT_GROUP_ID in " + idsAsString +
                " and tc.DATE_FROM = ? and tc.DATE_TO = ? and tc.DELETE_TS is null";

        persistence.runInTransaction(em -> {
            try {
                Connection connection = em.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(queryString);
                preparedStatement.setDate(1, new java.sql.Date(startDate.getTime()));
                preparedStatement.setDate(2, new java.sql.Date(endDate.getTime()));
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

    }

    private void createTimecardCorrections(Set<AssignmentGroupExt> assignmentGroups, Date startDate, Date endDate) {
        CommitContext commitContext = new CommitContext();
        List<TimecardCorrection> timecardCorrections = new ArrayList<>();
        List<UUID> uuidList = assignmentGroups.stream().map(AssignmentGroupExt::getId).collect(Collectors.toList());

        List<TimecardHeader> timecardHeaders = commonService.getEntities(TimecardHeader.class, "select e from tsadv$TimecardHeader e " +
                        "where e.assignmentGroup.id in :uuidList and e.effectiveStartDate >= :startDate and e.effectiveEndDate <= :endDate",
                ParamsMap.of("uuidList", uuidList, "startDate", startDate, "endDate", endDate), "timecardHeader-with-correction");

        for (AssignmentGroupExt assignmentGroup : assignmentGroups) {
            TimecardCorrection timecardCorrection = metadata.create(TimecardCorrection.class);
            timecardCorrection.setAssignmentGroup(assignmentGroup);
            timecardCorrection.setDateFrom(startDate);
            timecardCorrection.setDateTo(endDate);
            timecardCorrections.add(timecardCorrection);
            commitContext.addInstanceToCommit(timecardCorrection);
        }
        dataManager.commit(commitContext);

        commitContext = new CommitContext();
        for (TimecardHeader timecardHeader : timecardHeaders) {
            TimecardCorrection timecardCorrection = timecardCorrections.stream().filter(c -> c.getAssignmentGroup().getId().equals(timecardHeader.getAssignmentGroup().getId())).findFirst().orElse(null);
            timecardHeader.setTimecardCorrection(timecardCorrection);
            commitContext.addInstanceToCommit(timecardHeader);
        }
        dataManager.commit(commitContext);
    }

    protected List<TimecardHeader> createTimecardHeaders(Date endDate, List<AssignmentScheduleWithObjectsDto> assignmentScheduleWithObjectsDtoList,
                                                         Date startDate, Set<AssignmentGroupExt> assignmentGroups, List<TimecardCorrection> timecardCorrections) {
        List<TimecardHeader> timecardHeaders = new ArrayList<>();
        Date newEndDate = null;
        Date now = new Date();
        for (AssignmentGroupExt assignmentGroup : assignmentGroups) {
            Date newStartDate = startDate;
            TimecardCorrection timecardCorrection = timecardCorrections.stream().filter(c -> c.getAssignmentGroup().getId().equals(assignmentGroup.getId())).findFirst().orElse(null);

            List<AssignmentScheduleWithObjectsDto> assignmentScheduleWithObjectsDtoListForPerson = assignmentScheduleWithObjectsDtoList.stream()
                    .filter(a -> a.getAssignmentSchedule().getAssignmentGroup().getId().equals(assignmentGroup.getId())).collect(Collectors.toList());

            assignmentScheduleWithObjectsDtoListForPerson.sort(Comparator.comparing(d -> d.getAssignmentSchedule().getAssignmentGroup().getAssignment().getStartDate()));
            int size = assignmentScheduleWithObjectsDtoListForPerson.size();

            for (int i = 0; i < size; i++) {
                AssignmentScheduleWithObjectsDto dto = assignmentScheduleWithObjectsDtoListForPerson.get(i);
                AssignmentSchedule assignmentSchedule = dto.getAssignmentSchedule();
                AssignmentExt assignment = assignmentSchedule.getAssignmentGroup().getAssignment();

                if (size > 1 && i > 0) { //not 1st and many of assignmentSchedules
                    newStartDate = assignmentSchedule.getStartDate().after(startDate) ? assignmentSchedule.getStartDate() : startDate;
                    newStartDate = assignment.getStartDate().after(newStartDate) ? assignment.getStartDate() : newStartDate;
                }

                TimecardHeader timecardHeader = metadata.create(TimecardHeader.class);
                timecardHeader.setEffectiveStartDate(newStartDate);
                timecardHeader.setAssignmentSchedule(assignmentSchedule);
                timecardHeader.setAssignmentGroup(assignmentSchedule.getAssignmentGroup());
                timecardHeader.setAttribute2(String.valueOf(assignmentSchedule.getAssignmentGroup().getAssignment().getId()));
                timecardHeader.setTimecardCorrection(timecardCorrection);


                if (newEndDate != null && newEndDate != endDate) {
                    newStartDate = DateUtils.addDays(newEndDate, 1);
                }

                /* if timecardHeader last for month: set endDate - last day of month */
                if (i == size - 1) {
                    timecardHeader.setEffectiveEndDate(endDate);
                } else {
                    newEndDate = assignment.getEndDate().before(assignmentSchedule.getEndDate()) ? assignment.getEndDate() : assignmentSchedule.getEndDate();
                    timecardHeader.setEffectiveEndDate(newEndDate);
                }

                prepareZeroHoursHeader(timecardHeader);
                /* these numbers are filled only at this creation stage*/
                timecardHeader.setBaseWorkDays(0);
                timecardHeader.setBaseWorkHours(0d);
                timecardHeader.setPlanWorkDays(0);
                timecardHeader.setPlanWorkHours(0d);
                timecardHeader.setPlanWorkHoursPart(0d);
                timecardHeader.setCreateTs(now);
                timecardHeaders.add(timecardHeader);
            }
        }
        return timecardHeaders;
    }


    protected ArrayList<WorkedHoursSummary> createWorkedHoursSummariesForHeaders
            (List<TimecardHeader> timecardHeaders, Date startDate, Date endDate,
             Set<AssignmentGroupExt> assignmentGroups, Set<AssignmentSchedule> assignmentSchedules,
             List<ScheduleHeader> scheduleHeaders, List<Absence> absences, List<BusinessTrip> businessTrips,
             Map<UUID, PersonGroupExt> assignmentGroupForPersonGroups, TimecardLog timecardLog) {
        DicScheduleElementType weekendElementType = getElementType("WEEKEND");

        //Long startTime81 = System.currentTimeMillis();
        List<TimecardHeader> oldTimecardHeaders = getTimecardHeaders(assignmentGroups.stream().map(AssignmentGroupExt::getId).collect(Collectors.toList()), startDate, endDate);

        //System.out.println("8.1 getTimecardHeaders time = " + (System.currentTimeMillis() - startTime81));


        List<TimecardHeader> newTimecardHeaders = new ArrayList<>();
        List<Entity> newWorkedDetails = new ArrayList<>();

        newTimecardHeaders.addAll(timecardHeaders);

        ArrayList<WorkedHoursSummary> newWorkedHoursSummaries = new ArrayList<>();
        Set<AbsenceToWorkedHoursSummary> newAbsenceToWorkedHoursSummarySet = new HashSet<>();

//        that's gives us one request to db
        List<ScheduleSummary> scheduleSummaries = new ArrayList<>();
        Set<StandardOffset> offsets = new HashSet<>();
        scheduleHeaders.forEach(h -> offsets.add(h.getOffset()));

        for (StandardOffset offset : offsets) {
            ScheduleHeader scheduleHeader = scheduleHeaders.stream().filter(e -> e.getOffset().equals(offset)).findFirst().orElse(null);
            if (scheduleHeader != null) {
                Map<Integer, ScheduleSummary> summariesMap = scheduleHeader.getSummariesMap();
                scheduleSummaries.addAll(summariesMap.values());
            }
        }

        //Long startTime82 = System.currentTimeMillis();
        List<ScheduleDetail> allScheduleDetails = getAllScheduleDetails(scheduleSummaries, startDate, endDate);
        //System.out.println("8.2 getAllScheduleDetails time = " + (System.currentTimeMillis() - startTime82));

        //Long startTime83 = System.currentTimeMillis();
        for (TimecardHeader timecardHeader : timecardHeaders) {
            try {
                AssignmentExt assignment = timecardHeader.getAssignmentGroup().getAssignment();
                Date assignmentStartDate = assignment.getStartDate();
                Date assignmentEndDate = assignment.getEndDate();
                Date periodStartDate = timecardHeader.getEffectiveStartDate();
                periodStartDate = datesService.getDayBegin(periodStartDate); //can be removed after check that 0:00
                Date periodEndDate = timecardHeader.getEffectiveEndDate();

                long diff = periodEndDate.getTime() - periodStartDate.getTime();
                Long daysCount = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1;

                for (int i = 1; i <= daysCount; i++) {
                    Date date = DateUtils.addDays(timecardHeader.getEffectiveStartDate(), i - 1);
                    date = datesService.getDayBegin(date); //can be removed after check that 0:00

                    boolean dateInAssignment = !date.before(assignmentStartDate)
                            && !date.after(assignmentEndDate)
                            && !"TERMINATED".equals(assignment.getAssignmentStatus().getCode());

                    AssignmentSchedule assignmentScheduleForDay = getAssignmentScheduleForDay(assignmentSchedules, timecardHeader, date);

                    ScheduleSummary scheduleSummary = null;
                    if (assignmentScheduleForDay != null) {
                        StandardOffset offset = assignmentScheduleForDay.getOffset();
                        ScheduleHeader scheduleHeader = scheduleHeaders.stream().filter(e -> e.getOffset().equals(offset)).findFirst().orElse(null);
                        if (scheduleHeader != null) {
                            Map<Integer, ScheduleSummary> summariesMap = scheduleHeader.getSummariesMap();
                            if (summariesMap.containsKey(datesService.getDayOfMonth(date))) {
                                scheduleSummary = summariesMap.get(datesService.getDayOfMonth(date));
                                scheduleSummaries.add(scheduleSummary);
                            }
                        }
                    }

                    if (scheduleSummary != null && scheduleSummary.getHours() > 0d) {
                        timecardHeader.setPlanWorkHours(timecardHeader.getPlanWorkHours() + scheduleSummary.getHours());
                        timecardHeader.setPlanWorkDays(timecardHeader.getPlanWorkDays() + 1);
                    }

//                //Long startTime831 = System.currentTimeMillis();
                    UUID assignmentGroupId = timecardHeader.getAssignmentGroup().getId();
                    PersonGroupExt personGroupExt = assignmentGroupForPersonGroups.get(assignmentGroupId);
                    List<Absence> absencesForDay = null;
                    List<BusinessTrip> businessTripsForDay = null;
                    if (personGroupExt != null) {
                        absencesForDay = getAbsencesForDay(absences, personGroupExt, date);
                        businessTripsForDay = getBusinessTripsForDay(businessTrips, personGroupExt, date);
                    }

                    WorkedHoursSummary workedHoursSummary;
                    boolean fillElementType = true;
                    if (!dateInAssignment) {
                        fillElementType = false;
                        AssignmentExt nextAssignment = getNextAssignment(assignment);
                        if (nextAssignment != null && "SUSPENDED".equalsIgnoreCase(nextAssignment.getAssignmentStatus().getCode())) {
                            fillElementType = true;
                        }
                    }
                    workedHoursSummary = createWorkedHoursSummary(timecardHeader, scheduleSummary,
                            absencesForDay, businessTripsForDay, date, fillElementType);

//                //System.out.println("8.3.1 createWorkedHoursSummary time = " + (System.currentTimeMillis() - startTime831));

                    if (workedHoursSummary != null) {
                        Absence absence = workedHoursSummary.getAbsence();
                        newAbsenceToWorkedHoursSummarySet.addAll(workedHoursSummary.getAbsenceToWorkedHoursSummaryList());
                        newWorkedHoursSummaries.add(workedHoursSummary);
                        if (absence != null && absence.getType().getCode() == null) {
                            try {
                                throw new Exception("There is absence type without code!");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        List<ScheduleDetail> scheduleDetails = getScheduleDetailsForSummaryAndDate(allScheduleDetails, scheduleSummary, date);
                        List<WorkedHoursDetailed> workedHoursDetailedList;
                        boolean businessTripTypeIsWorkingDay = Optional.ofNullable(workedHoursSummary.getBussinessTrip())
                                .map(BusinessTrip::getType)
                                .map(DicBusinessTripType::getWorkingDay)
                                .orElse(false);

                        boolean absenceTypeIsWorkingDay = Optional.ofNullable(workedHoursSummary.getAbsence())
                                .map(Absence::getType)
                                .map(DicAbsenceType::getIsWorkingDay)
                                .orElse(false);
                        boolean multipleAbsencesTypeIsWorkingDay = false;
                        if (!workedHoursSummary.getAbsenceToWorkedHoursSummaryList().isEmpty()) {
                            multipleAbsencesTypeIsWorkingDay = workedHoursSummary.getAbsenceToWorkedHoursSummaryList()
                                    .stream().allMatch(as -> as.getAbsence().getType().getIsWorkingDay() != null && as.getAbsence().getType().getIsWorkingDay()); //if any absence.type.isWorkingDay == false then all false
                        }
                        absenceTypeIsWorkingDay = absenceTypeIsWorkingDay || multipleAbsencesTypeIsWorkingDay;

                        boolean createFactHours = businessTripTypeIsWorkingDay || absenceTypeIsWorkingDay;
                        if (workedHoursSummary.getBussinessTrip() == null && absence == null) {
                            createFactHours = true;
                        }

                        boolean needToSetZeroHoursToSummary = !createFactHours && (workedHoursSummary.getBussinessTrip() != null || absence != null);

                        if (!dateInAssignment) { //can be changed in future - for better code
                            needToSetZeroHoursToSummary = true;
                            createFactHours = false;
                            workedHoursSummary.setDisplayValue(" ");
                        }

                        if ("SUSPENDED".equalsIgnoreCase(timecardHeader.getAssignmentGroup().getAssignment().getAssignmentStatus().getCode())) {
                            createFactHours = false;
                            needToSetZeroHoursToSummary = true;
                            if (absence == null) {
                                workedHoursSummary.setScheduleElementType(weekendElementType);
                            }
                        }
//                    createFactHours = createFactHours && (absence == null || absence.getType().getCode().equals("RECALL") || absence.getType().getCode().equals("CANCEL"));
//                        //Long startTime832 = System.currentTimeMillis();
                        if (dateInAssignment && createFactHours) {
                            workedHoursDetailedList = createWorkedHoursDetailedList(scheduleDetails, workedHoursSummary, true);
                        } else {
                            workedHoursDetailedList = createWorkedHoursDetailedList(scheduleDetails, workedHoursSummary, false);
                        }
//                        //System.out.println("8.3.2 createWorkedHoursDetailedList time = " + (System.currentTimeMillis() - startTime832));
                        newWorkedDetails.addAll(workedHoursDetailedList);

                        if (needToSetZeroHoursToSummary) {
                            workedHoursSummary.setHours(0d);
                        }

                        /* filling timecardHeader with calculated values*/

                        timecardHeader.setFactHoursWithoutOvertime(timecardHeader.getFactHoursWithoutOvertime() + workedHoursSummary.getHours());
                        timecardHeader.setFactWorkDays(timecardHeader.getFactWorkDays() + (workedHoursSummary.getHours() > 0d ? 1 : 0));

                        boolean hasHoidayHours = false;

                        for (WorkedHoursDetailed workedHoursDetailed : workedHoursDetailedList) {
                            DicScheduleElementType elementType = workedHoursDetailed.getScheduleElementType();
                            if (elementType.getCode().equals("NIGHT_HOURS")) {
                                timecardHeader.setNightHours(timecardHeader.getNightHours() + workedHoursDetailed.getHours());
                            }
                            if (elementType.getCode().equals("HOLIDAY_HOURS")) {
                                timecardHeader.setHolidayHours(timecardHeader.getHolidayHours() + workedHoursDetailed.getHours());
                                if (workedHoursSummary.getHours() > 0d) {
                                    hasHoidayHours = true;
                                }
                            }
                            if (elementType.getCode().equals("WEEKEND_HOURS")) {
                                timecardHeader.setWeekendHours(timecardHeader.getWeekendHours() + workedHoursDetailed.getHours());
                            }

                        }

                        if (timecardConfig.getMultipleAbsencesToOneDay() && !workedHoursSummary.getAbsenceToWorkedHoursSummaryList().isEmpty()) {
                            workedHoursSummary.getAbsenceToWorkedHoursSummaryList().forEach(as -> addAbsenceStatistics(as.getAbsence(), timecardHeader));
                        } else if (workedHoursSummary.getAbsence() != null) {
                            addAbsenceStatistics(absence, timecardHeader);
                        }
                        String code = workedHoursSummary.getScheduleElementType().getCode();
                        if ((code.equals("HOLIDAY") || code.equals("WEEKEND")) && absence == null) {
                            timecardHeader.setTotalFreeDays(timecardHeader.getTotalFreeDays() + 1);
                        }
                        if (workedHoursSummary.getBussinessTrip() != null) {
                            timecardHeader.setBusTripDays(timecardHeader.getBusTripDays() + 1);
                        }
                        if (hasHoidayHours) {
                            timecardHeader.setHolidayDays(timecardHeader.getHolidayDays() + 1);
                        }
                        timecardHeader.setBaseWorkHours(timecardHeader.getBaseWorkHours() + scheduleSummary.getBaseHours());
                        timecardHeader.setBaseWorkDays(timecardHeader.getBaseWorkDays() + (scheduleSummary.getBaseHours() > 0d ? 1 : 0));
                        timecardHeader.setTotalWorkedDays(timecardHeader.getFactWorkDays() /*+ overtime*/);
                        timecardHeader.setTotalAbsence(timecardHeader.getAbsenceDays() + timecardHeader.getTotalFreeDays());
                        timecardHeader.setGrandTotalDays(timecardHeader.getTotalAbsence() + timecardHeader.getTotalWorkedDays());
                    }
                }
                setAttribute5(timecardHeader, newWorkedDetails.stream().filter(entity -> ((WorkedHoursDetailed) entity).getTimecardHeader().getId().equals(timecardHeader.getId())).collect(Collectors.toList()));

            } catch (Exception e) {
                e.printStackTrace();
                addErrorForTimecardLog(timecardLog, timecardHeader, e);
            }

        }
        //System.out.println("8.3 some operation with HEADERS time = " + (System.currentTimeMillis() - startTime83));
        //Long startTime84 = System.currentTimeMillis();
        persistence.runInTransaction(em -> {
            try {
                Connection connection = em.getConnection();
                PreparedStatement preparedStatementForDelete1 = fillStatementForDetailsDelete(connection, oldTimecardHeaders, WorkedHoursDetailed.class);
                PreparedStatement preparedStatementForDelete2 = null;
                if (timecardConfig.getMultipleAbsencesToOneDay()) {
                    preparedStatementForDelete2 = fillStatementForAbsenceToWorkedHoursSummaryDelete(connection, oldTimecardHeaders, AbsenceToWorkedHoursSummary.class);
                }
                PreparedStatement preparedStatementForDelete3 = fillStatementForDelete(connection, oldTimecardHeaders, WorkedHoursSummary.class);
//                PreparedStatement preparedStatementForDelete3 = fillStatementForDeviationsDelete(em.getConnection(), oldTimecardHeaders, TimecardDeviation.class);
                PreparedStatement preparedStatementForDelete4 = fillStatementForDelete(connection, oldTimecardHeaders, TimecardHeader.class);

                PreparedStatement preparedStatement1 = fillStatement(connection, timecardHeaders, TimecardHeader.class);
                PreparedStatement preparedStatement2 = fillStatement(connection, newWorkedHoursSummaries, WorkedHoursSummary.class);
                PreparedStatement preparedStatement3 = fillStatement(connection, newWorkedDetails, WorkedHoursDetailed.class);

                PreparedStatement preparedStatement4 = null;
                if (timecardConfig.getMultipleAbsencesToOneDay()) {
                    preparedStatement4 = fillStatement(connection, new ArrayList<>(newAbsenceToWorkedHoursSummarySet), AbsenceToWorkedHoursSummary.class);
                }
                if (preparedStatementForDelete1 != null) {
                    preparedStatementForDelete1.executeUpdate();
                }
                if (preparedStatementForDelete2 != null) {
                    preparedStatementForDelete2.executeUpdate();
                }

                if (preparedStatementForDelete3 != null) {
                    preparedStatementForDelete3.executeUpdate();
                }
                if (preparedStatementForDelete4 != null) {
                    preparedStatementForDelete4.executeUpdate();
                }

                preparedStatement1.executeBatch();
                preparedStatement2.executeBatch();
                preparedStatement3.executeBatch();
                if (preparedStatement4 != null) {
                    preparedStatement4.executeBatch();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        //System.out.println("8.4 SQL native deletes and inserts time = " + (System.currentTimeMillis() - startTime84));
        return newWorkedHoursSummaries;
    }

    private void setAttribute5(TimecardHeader timecardHeader, List<Entity> newWorkedDetails) {
        Double workPlusBreakHours = 0d;
        if (StringUtils.isNotEmpty(timecardHeader.getAttribute5())) {
            workPlusBreakHours = Double.valueOf(timecardHeader.getAttribute5());
        }
        for (Entity entity : newWorkedDetails) {
            String code = ((WorkedHoursDetailed) entity).getScheduleElementType().getCode();
            if (code.equals("WORK_HOURS") || code.equals("BREAK")) {
                workPlusBreakHours += ((WorkedHoursDetailed) entity).getHours();
            }
        }
        timecardHeader.setAttribute5(workPlusBreakHours.toString());
    }

    protected void addErrorForTimecardLog(TimecardLog timecardLog, TimecardHeader timecardHeader, Exception e) {
        String errorFor = null;
        if (timecardHeader != null) {
            AssignmentGroupExt assignmentGroup = timecardHeader.getAssignmentGroup();
            assignmentGroup = dataManager.reload(assignmentGroup, "assignmentGroupExt-with-employee-number");
            String assignmentPersonFioWithEmployeeNumber = assignmentGroup.getAssignmentPersonFioWithEmployeeNumber();
            errorFor = "Timecard can not be formed for " + assignmentPersonFioWithEmployeeNumber;
            System.out.println(errorFor);
        }

        String previousErrorText = timecardLog.getErrorText();
        StringBuilder sb = StringUtils.isNotBlank(previousErrorText) ? new StringBuilder(previousErrorText) : new StringBuilder();
        if (errorFor != null) {
            sb.append(errorFor);
        }
        sb.append("\n");
        sb.append("\n");
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        sb.append(sw.toString());
        sb.append("\n");
        sb.append("\n");
        sb.append("\n");

        timecardLog.setErrorText(sb.toString());
        timecardLog.setSuccess(false);
    }

    private AssignmentExt getNextAssignment(AssignmentExt assignment) {
        List<AssignmentExt> assignments = commonService.getEntities(AssignmentExt.class,
                "select e from base$AssignmentExt e where e.startDate > :previousAssignmentEndDate order by e.startDate asc",
                ParamsMap.of("previousAssignmentEndDate", assignment.getEndDate()), "assignmentExt-with-type");
        if (!assignments.isEmpty()) {
            return assignments.get(0);
        }
        return null;
    }

    protected <T extends Entity> PreparedStatement fillStatement(Connection connection, List<T> entities, Class clazz) throws SQLException {
        Map<MetaProperty, Class> orderedProperties = new LinkedHashMap<>();

        String query = getQueryForInsert(orderedProperties, clazz);

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        for (Entity entity : entities) {
            int i = 1;
            preparedStatement.clearParameters();
            for (Map.Entry<MetaProperty, Class> entry : orderedProperties.entrySet()) {

                Object value = entity.getValue(entry.getKey().getName());
                if (value != null) {
                    if (value instanceof Entity) {
                        value = ((Entity) value).getId();
                        preparedStatement.setObject(i, value);
                    } else if (value instanceof Date) {
                        AnnotatedElement annotatedElement = entry.getKey().getAnnotatedElement();
                        Temporal[] temporals = annotatedElement != null ? annotatedElement.getAnnotationsByType(Temporal.class) : null;
                        if (temporals != null && temporals.length > 0) {
                            preparedStatement.setTimestamp(i, new Timestamp(((Date) value).getTime()));
                        } else {
                            preparedStatement.setDate(i, new java.sql.Date(((Date) value).getTime()));
                        }
                    } else if (value instanceof Integer) {
                        preparedStatement.setInt(i, ((Integer) value));
                    } else if (value instanceof Double) {
                        preparedStatement.setDouble(i, ((Double) value));
                    } else if (value instanceof Enum<?>) {
                        preparedStatement.setInt(i, ((MaterialDesignColorsEnum) value).getId()); //as you see - there is only for that type of enum
                    } else {
                        preparedStatement.setObject(i, value);
                    }
                } else {
                    String className = entry.getValue().getSimpleName();

                    if (className.equals(Double.class.getSimpleName())) {
                        preparedStatement.setDouble(i, 0);
                    } else if (className.equals(Integer.class.getSimpleName())) {
                        preparedStatement.setInt(i, 0);
                    } else {
                        preparedStatement.setObject(i, null);
                    }
                }
                i++;

            }
            preparedStatement.addBatch();
        }

        return preparedStatement;
    }

//    protected <T extends Entity> PreparedStatement fillStatementForDeviationsDelete(Connection connection, List<T> entities, Class clazz) throws SQLException {
//
//        String query = getQueryForDeviationsDelete((List<Entity>) entities, clazz);
//
//        PreparedStatement preparedStatement = query == null ? null : connection.prepareStatement(query);
//        return preparedStatement;
//    }

    protected <T extends Entity> PreparedStatement fillStatementForDetailsDelete(Connection connection, List<T> entities, Class clazz) throws SQLException {

        String query = getQueryForDetailsDelete((List<Entity>) entities, clazz);

        PreparedStatement preparedStatement = query == null ? null : connection.prepareStatement(query);
        return preparedStatement;
    }

    protected <T extends Entity> PreparedStatement fillStatementForAbsenceToWorkedHoursSummaryDelete(Connection connection, List<T> entities, Class clazz) throws SQLException {

        String query = getQueryForAbsenceToWorkedHoursSummaryDelete((List<Entity>) entities, clazz);

        PreparedStatement preparedStatement = query == null ? null : connection.prepareStatement(query);
        return preparedStatement;
    }

    protected <T extends Entity> PreparedStatement fillStatementForDelete(Connection connection, List<T> entities, Class clazz) throws SQLException {

        String query = null;
        try {
            query = getQueryForDelete((List<Entity>) entities, clazz);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        PreparedStatement preparedStatement = query == null ? null : connection.prepareStatement(query);
        return preparedStatement;
    }

    protected String getQueryForInsert(Map<MetaProperty, Class> orderedProperties, Class clazz) {
        MetaClass metaClass = metadata.getClass(clazz);

        Set<String> fieldValues = new LinkedHashSet<>();

        for (MetaProperty metaProperty : metaClass.getProperties()) {
            String propertyName = metaProperty.getName();

            if (propertyName.startsWith("attribute")
                    && !propertyName.startsWith("attribute1")
                    && !propertyName.startsWith("attribute2")
                    && !propertyName.startsWith("attribute3")
                    && !propertyName.startsWith("attribute4")
                    && !propertyName.startsWith("attribute5")) {
                continue;
            }

            AnnotatedElement annotatedElement = metaProperty.getAnnotatedElement();
            if (annotatedElement != null) {
                Range range = metaProperty.getRange();
                String columnAlias = null;

                boolean put = false;
                if (range.isClass()) {
                    if (range.getCardinality().equals(Range.Cardinality.ONE_TO_MANY)) {
                        continue;
                    }

                    JoinColumn joinColumn = annotatedElement.getAnnotation(JoinColumn.class);
                    if (joinColumn != null) {
                        columnAlias = joinColumn.name();
                        put = true;

                    }
                } else {
                    Column column = annotatedElement.getAnnotation(Column.class);
                    if (column != null) {
                        columnAlias = column.name();
                        put = true;
                    }
                }
                if (put) {
                    orderedProperties.put(metaProperty, metaProperty.getJavaType());

                    fieldValues.add(columnAlias);
                }
            }
        }

        StringBuilder sb = new StringBuilder("INSERT INTO ");
        sb.append(metadata.getTools().getDatabaseTable(metaClass));
        sb.append("(");

        sb.append(fieldValues.stream().collect(Collectors.joining(",")));
        sb.append(") values (");


        for (int i = 0; i < fieldValues.size(); i++) {
            sb.append("?");
            if (i != fieldValues.size() - 1) {
                sb.append(",");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    protected String getQueryForDelete(List<Entity> entities, Class clazz) throws IllegalAccessException, InstantiationException {
        if (entities.isEmpty()) {
            return null;
        }
        MetaClass metaClass = metadata.getClass(clazz);
        StringBuilder sb = new StringBuilder("DELETE FROM ");
        sb.append(metadata.getTools().getDatabaseTable(metaClass));
        if (clazz.newInstance() instanceof WorkedHoursSummary) {
            sb.append(" WHERE TIMECARD_HEADER_ID IN ");
        } else {
            sb.append(" WHERE ID IN ");
        }
        sb.append("('");

        sb.append(entities.stream().map(e -> e.getId().toString()).collect(Collectors.joining("', '")));
        sb.append("');");
        return sb.toString();
    }

//    protected String getQueryForDeviationsDelete(List<Entity> entities, Class clazz) {
//        if (entities.isEmpty()) {
//            return null;
//        }
//
//        MetaClass metaClass = metadata.getClass(clazz);
//
//        StringBuilder sb = new StringBuilder("DELETE FROM ");
//        sb.append(metadata.getTools().getDatabaseTable(metaClass));
//        sb.append(" WHERE ASSIGNMENT_GROUP_ID IN ");
//        sb.append("('");
//
//        sb.append(entities.stream().map(e -> e.getId().toString()).collect(Collectors.joining("', '")));
//        sb.append("');");
//        return sb.toString();
//    }

    protected String getQueryForDetailsDelete(List<Entity> entities, Class clazz) {
        if (entities.isEmpty()) {
            return null;
        }

        MetaClass metaClass = metadata.getClass(clazz);

        StringBuilder sb = new StringBuilder("DELETE FROM ");
        sb.append(metadata.getTools().getDatabaseTable(metaClass));
        sb.append(" WHERE TIMECARD_HEADER_ID IN ");
        sb.append("('");

        sb.append(entities.stream().map(e -> e.getId().toString()).collect(Collectors.joining("', '")));
        sb.append("');");
        return sb.toString();
    }

    protected String getQueryForAbsenceToWorkedHoursSummaryDelete(List<Entity> entities, Class clazz) {
        if (entities.isEmpty()) {
            return null;
        }

        MetaClass metaClass = metadata.getClass(clazz);

        StringBuilder sb = new StringBuilder("DELETE FROM ");
        sb.append(metadata.getTools().getDatabaseTable(metaClass));
        sb.append(" WHERE WORKED_HOURS_SUMMARY_ID IN (SELECT ID FROM TSADV_WORKED_HOURS_SUMMARY WHERE TIMECARD_HEADER_ID IN ");
        sb.append("('");

        sb.append(entities.stream().map(e -> e.getId().toString()).collect(Collectors.joining("', '")));
        sb.append("'));");
        return sb.toString();
    }

    public List<Timecard> getTimecards(OrganizationGroupExt organizationGroup,
                                       PositionGroupExt positionGroup,
                                       PersonGroupExt personGroup,
                                       Date startDate,
                                       Date endDate,
                                       int firstResult,
                                       int maxResults,
                                       boolean loadFullData,
                                       AssignmentExt assignmentExt,
                                       Boolean enableInclusions) {
        boolean mergeHeaders = timecardConfig.getMergeTimecards();

        List<Timecard> list = new ArrayList<>();

        List<Object[]> objects;
        String queryString = "";

        Object firstParameter = null;

        if (organizationGroup != null) {
            queryString = getQueryForOrganization(assignmentExt, mergeHeaders, enableInclusions, loadFullData);
//            if (enableInclusions) {
//                firstParameter = "%" + organizationGroup.getId().toString() + "%";
//            } else {
            firstParameter = organizationGroup.getId();
//            }
        }
        if (positionGroup != null) {
            queryString = getQueryForPosition(assignmentExt, mergeHeaders);
            firstParameter = positionGroup.getId();
        }
        if (personGroup != null) {
            queryString = getQueryForPerson(assignmentExt, mergeHeaders);
            firstParameter = personGroup.getId();
        }
        List<DicScheduleElementType> dicScheduleElementTypes = new ArrayList<>();
        List<DicAbsenceType> dicAbsenceTypes = new ArrayList<>();
        List<DicBusinessTripType> dicBusinessTripTypes = new ArrayList<>();

        if (!loadFullData) {
            dicScheduleElementTypes = commonService.getEntities(DicScheduleElementType.class, "select e from tsadv$DicScheduleElementType e", null, View.LOCAL);
            dicAbsenceTypes = commonService.getEntities(DicAbsenceType.class, "select e from tsadv$DicAbsenceType e", null, View.LOCAL);
            dicBusinessTripTypes = commonService.getEntities(DicBusinessTripType.class, "select e from tsadv$DicBusinessTripType e", null, View.LOCAL);
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
            query.setParameter(6, false);
            if (assignmentExt != null) {
                query.setParameter(7, assignmentExt.getId());
            }

            objects = query.getResultList();

            if (objects != null && !objects.isEmpty()) {
                for (Object[] row : objects) {
                    Timecard timecard = new Timecard();
                    HashMap<UUID, TimecardHeader> headerMap = new HashMap<>();
                    ArrayList<TimecardHeader> timecardHeaders = new ArrayList<>();
                    timecard.setName(row[0] == null ? "" : (String) row[0]);

                    String assignmentGroupString = (String) row[1];
                    if (assignmentGroupString != null && !assignmentGroupString.isEmpty()) {
                        String[] strings = assignmentGroupString.split(fieldsDelimiter);
                        timecard.setAssignmentGroupId(UUID.fromString(strings[0]));
                        timecard.setAssignmentGroupLegacyId(strings[1].equals("(none)") ? null : strings[1]);
                        timecard.setCostCenterString(strings[2].equals("(none)") ? null : strings[2]);
                        timecard.setPositionName(strings[3]);
                    }

                    if (row.length >= 5) {
                        timecard.setSchedules(row[4] != null ? String.valueOf(row[4]) : "");
                    }
                    if (!loadFullData) {
                        String timecardHeadersString = (String) row[2];
                        if (timecardHeadersString != null && !timecardHeadersString.isEmpty()) {
                            String[] timcardHeaderEntities = timecardHeadersString.split(entityDelimiter);

                            for (String entity : timcardHeaderEntities) {
                                String[] strings = entity.split(fieldsDelimiter);
                                TimecardHeader timecardHeader = new TimecardHeader();
                                timecardHeader.setId(UUID.fromString(strings[0]));

                                Date effectiveStartDate = null;
                                Date effectiveEndDate = null;
                                try {
                                    effectiveStartDate = format.parse(strings[1]);
                                    effectiveEndDate = format.parse(strings[2]);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                timecardHeader.setEffectiveStartDate(effectiveStartDate);
                                timecardHeader.setEffectiveEndDate(effectiveEndDate);
                                if (strings.length > 3) {
                                    AssignmentGroupExt assignmentGroup = metadata.create(AssignmentGroupExt.class);
                                    assignmentGroup.setId(timecard.getAssignmentGroupId());

                                    timecardHeader.setAssignmentGroup(assignmentGroup);
                                    timecardHeader.setPlanWorkHours(Double.valueOf(strings[3]));
                                    timecardHeader.setBaseWorkHours(Double.valueOf(strings[4]));
                                    timecardHeader.setPlanWorkHoursPart(Double.valueOf(strings[5]));
                                    timecardHeader.setPlanWorkDays(Integer.valueOf(strings[6]));
                                    timecardHeader.setBaseWorkDays(Integer.valueOf(strings[7]));
                                    timecardHeader.setFactHoursWithoutOvertime(Double.valueOf(strings[8]));
                                    timecardHeader.setWeekendHours(Double.valueOf(strings[9]));
                                    timecardHeader.setWeekendDays(Integer.valueOf(strings[10]));
                                    timecardHeader.setFactWorkDays(Integer.valueOf(strings[11]));
                                    timecardHeader.setNightHours(Double.valueOf(strings[12]));
                                    timecardHeader.setHolidayHours(Double.valueOf(strings[13]));
                                    timecardHeader.setHolidayDays(Integer.valueOf(strings[14]));
                                    timecardHeader.setAnnualVacationDays(Integer.valueOf(strings[15]));
                                    timecardHeader.setUnpaidVacationDays(Integer.valueOf(strings[16]));
                                    timecardHeader.setMaternityVacationDays(Integer.valueOf(strings[17]));
                                    timecardHeader.setChildcareVacationDays(Integer.valueOf(strings[18]));
                                    timecardHeader.setSickDays(Integer.valueOf(strings[19]));
                                    timecardHeader.setAbsenceDays(Integer.valueOf(strings[20]));
                                    timecardHeader.setTotalFreeDays(Integer.valueOf(strings[21]));
                                    timecardHeader.setBusTripDays(Integer.valueOf(strings[22]));
                                    timecardHeader.setTotalAbsence(Integer.valueOf(strings[23]));
                                    timecardHeader.setTotalWorkedDays(Integer.valueOf(strings[24]));
                                    timecardHeader.setGrandTotalDays(Integer.valueOf(strings[25]));
                                    timecardHeader.setOvertimeHours(Double.valueOf(strings[26]));

                                    AssignmentSchedule assignmentSchedule = metadata.create(AssignmentSchedule.class);
                                    assignmentSchedule.setId(UUID.fromString(strings[27]));
                                    timecardHeader.setAssignmentSchedule(assignmentSchedule);

                                    UUID timecardCorrectionId = strings[28].equals("null") ? null : UUID.fromString(strings[28]);
                                    if (timecardCorrectionId != null) {
                                        TimecardCorrection timecardCorrection = metadata.create(TimecardCorrection.class);
                                        timecardCorrection.setId(timecardCorrectionId);
                                        timecardHeader.setTimecardCorrection(timecardCorrection);
                                    }

                                    timecardHeader.setDayHours(Double.valueOf(strings[29]));
                                }
                                timecardHeaders.add(timecardHeader);
                                headerMap.put(timecardHeader.getId(), timecardHeader);
                            }
                        }
                        boolean corrective = timecardHeaders.stream().anyMatch(t -> t.getTimecardCorrection() != null);
                        timecard.setCorrective(corrective);
                        timecard.setTimecardHeaders(timecardHeaders);

                        List<WorkedHoursSummary> workedHoursSummaries = new ArrayList<>();
                        String workedSummariesString = (String) row[3];
                        if (workedSummariesString != null && !workedSummariesString.isEmpty()) {
                            String[] workedSummaryEntities = workedSummariesString.split(entityDelimiter);
                            if (workedSummaryEntities.length > 0) {
                                for (String entity : workedSummaryEntities) {
                                    WorkedHoursSummary workedHoursSummary = metadata.create(WorkedHoursSummary.class);
                                    String[] strings = entity.split(fieldsDelimiter);
                                    workedHoursSummary.setId(UUID.fromString(strings[0]));

                                    TimecardHeader timecardHeader = headerMap.get(UUID.fromString(strings[1]));
                                    workedHoursSummary.setTimecardHeader(timecardHeader);

                                    Date workedDate = null;
                                    try {
                                        workedDate = format.parse(strings[2]);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    workedHoursSummary.setWorkedDate(workedDate);

                                    Double hours = Double.valueOf(strings[3]);
                                    workedHoursSummary.setHours(hours);

                                    DicScheduleElementType dicScheduleElementType = metadata.create(DicScheduleElementType.class);
                                    dicScheduleElementType.setId(UUID.fromString(strings[4]));

                                    if (!loadFullData) {
                                        if (dicScheduleElementTypes.contains(dicScheduleElementType)) {
                                            int indexOf = dicScheduleElementTypes.indexOf(dicScheduleElementType);
                                            dicScheduleElementType = dicScheduleElementTypes.get(indexOf);
                                        }
                                    }
                                    workedHoursSummary.setScheduleElementType(dicScheduleElementType);

                                    if (!strings[5].equals("null")) {
                                        Absence absence = metadata.create(Absence.class);
                                        absence.setId(UUID.fromString(strings[5]));
                                        if (!strings[6].equals("null")) {
                                            DicAbsenceType absenceType = metadata.create(DicAbsenceType.class);
                                            absenceType.setId(UUID.fromString(strings[6]));
                                            if (!loadFullData) {
                                                if (dicAbsenceTypes.contains(absenceType)) {
                                                    int indexOf = dicAbsenceTypes.indexOf(absenceType);
                                                    absenceType = dicAbsenceTypes.get(indexOf);
                                                }
                                            }

                                            absence.setType(absenceType);
                                        }
                                        workedHoursSummary.setAbsence(absence);

                                    }

                                    if (!strings[7].equals("null")) {
                                        BusinessTrip businessTrip = metadata.create(BusinessTrip.class);
                                        businessTrip.setId(UUID.fromString(strings[7]));
                                        if (!strings[8].equals("null")) {
                                            DicBusinessTripType businessTripType = metadata.create(DicBusinessTripType.class);
                                            businessTripType.setId(UUID.fromString(strings[8]));
                                            if (!loadFullData) {
                                                if (dicBusinessTripTypes.contains(businessTripType)) {
                                                    int indexOf = dicBusinessTripTypes.indexOf(businessTripType);
                                                    businessTripType = dicBusinessTripTypes.get(indexOf);
                                                }
                                            }

                                            businessTrip.setType(businessTripType);
                                        }
                                        workedHoursSummary.setBussinessTrip(businessTrip);
                                    }

                                    if (!strings[9].equals("null")) {
                                        workedHoursSummary.setDisplayValue(strings[9]);
                                    }

                                    workedHoursSummaries.add(workedHoursSummary);

                                    if (!loadFullData) {
                                        int dayOfMonth = datesService.getDayOfMonth(workedDate);
                                        try {
                                            Method setSMethod = Timecard.class.getMethod("setSummary" + dayOfMonth, WorkedHoursSummary.class);
                                            setSMethod.invoke(timecard, workedHoursSummary);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                }


                            }
                            if (timecardConfig.getMultipleAbsencesToOneDay() && !loadFullData) {
                                String absencesToSummariesString = (String) row[5];
                                if (absencesToSummariesString != null && !absencesToSummariesString.isEmpty()) {
                                    String[] entities = absencesToSummariesString.split(entityDelimiter);
                                    List<String> stringsArray = new ArrayList<>(Arrays.asList(entities));
                                    for (WorkedHoursSummary workedHoursSummary : workedHoursSummaries) {
                                        Set<AbsenceToWorkedHoursSummary> absenceToWorkedHoursSummarySet = new HashSet<>();
                                        for (String s : stringsArray) {
                                            String[] strings = s.split(fieldsDelimiter);
                                            if (s.contains(workedHoursSummary.getId().toString())) {
                                                AbsenceToWorkedHoursSummary absenceToWorkedHoursSummary = metadata.create(AbsenceToWorkedHoursSummary.class);
                                                absenceToWorkedHoursSummary.setId(strings[0].equals("(none)") ? null : UUID.fromString(strings[0]));
                                                absenceToWorkedHoursSummary.setWorkedHoursSummary(workedHoursSummary);

                                                if (!strings[2].equals("null")) {
                                                    Absence absence = metadata.create(Absence.class);
                                                    absence.setId(UUID.fromString(strings[2]));
                                                    if (!strings[3].equals("null")) {
                                                        DicAbsenceType absenceType = metadata.create(DicAbsenceType.class);
                                                        absenceType.setId(UUID.fromString(strings[3]));
                                                        if (!loadFullData) {
                                                            if (dicAbsenceTypes.contains(absenceType)) {
                                                                int indexOf = dicAbsenceTypes.indexOf(absenceType);
                                                                absenceType = dicAbsenceTypes.get(indexOf);
                                                            }
                                                        }

                                                        absence.setType(absenceType);
                                                    }
                                                    absenceToWorkedHoursSummary.setAbsence(absence);
                                                    absenceToWorkedHoursSummarySet.add(absenceToWorkedHoursSummary);
                                                }
                                            }
                                        }
                                        workedHoursSummary.setAbsenceToWorkedHoursSummaryList(new ArrayList<>(absenceToWorkedHoursSummarySet));
                                    }
                                }
                            }

                            timecard.setTimecardHeaders(timecardHeaders);
                            timecard.setWorkedHoursSummaries(workedHoursSummaries);
                            double planWorkHours = timecardHeaders.stream().filter(e -> e.getPlanWorkHours() != null).mapToDouble(TimecardHeader::getPlanWorkHours).sum();
                            Integer planWorkDays = timecardHeaders.stream().filter(e -> e.getPlanWorkDays() != null).mapToInt(TimecardHeader::getPlanWorkDays).sum();
                            Double baseWorkHours = timecardHeaders.stream().filter(e -> e.getBaseWorkHours() != null).mapToDouble(TimecardHeader::getBaseWorkHours).sum();
                            Integer baseWorkDays = timecardHeaders.stream().filter(e -> e.getBaseWorkDays() != null).mapToInt(TimecardHeader::getBaseWorkDays).sum();
                            Double factHoursWithoutOvertime = timecardHeaders.stream().filter(e -> e.getFactHoursWithoutOvertime() != null).mapToDouble(TimecardHeader::getFactHoursWithoutOvertime).sum();
                            Integer factWorkDays = timecardHeaders.stream().filter(e -> e.getFactWorkDays() != null).mapToInt(TimecardHeader::getFactWorkDays).sum();
                            Double nightHours = timecardHeaders.stream().filter(e -> e.getNightHours() != null).mapToDouble(TimecardHeader::getNightHours).sum();
                            Integer annualVacationDays = timecardHeaders.stream().filter(e -> e.getAnnualVacationDays() != null).mapToInt(TimecardHeader::getAnnualVacationDays).sum();
                            Integer unpaidVacationDays = timecardHeaders.stream().filter(e -> e.getUnpaidVacationDays() != null).mapToInt(TimecardHeader::getUnpaidVacationDays).sum();
                            Integer sickDays = timecardHeaders.stream().filter(e -> e.getSickDays() != null).mapToInt(TimecardHeader::getSickDays).sum();
                            Integer absenceDays = timecardHeaders.stream().filter(e -> e.getAbsenceDays() != null).mapToInt(TimecardHeader::getAbsenceDays).sum();
                            Integer totalFreeDays = timecardHeaders.stream().filter(e -> e.getTotalFreeDays() != null).mapToInt(TimecardHeader::getTotalFreeDays).sum();
                            Integer bussinessTrip = timecardHeaders.stream().filter(e -> e.getBusTripDays() != null).mapToInt(TimecardHeader::getBusTripDays).sum();
                            Integer totalAbsence = timecardHeaders.stream().filter(e -> e.getTotalAbsence() != null).mapToInt(TimecardHeader::getTotalAbsence).sum();
                            Integer totalWorkedDays = timecardHeaders.stream().filter(e -> e.getTotalWorkedDays() != null).mapToInt(TimecardHeader::getTotalWorkedDays).sum();
                            Integer grandTotalDays = timecardHeaders.stream().filter(e -> e.getGrandTotalDays() != null).mapToInt(TimecardHeader::getGrandTotalDays).sum();
                            Double overTime = timecardHeaders.stream().filter(e -> e.getOvertimeHours() != null).mapToDouble(TimecardHeader::getOvertimeHours).sum();
                            Double dayHours = timecardHeaders.stream().filter(e -> e.getDayHours() != null).mapToDouble(TimecardHeader::getDayHours).sum();
                            Integer numberOfPaidDowntime = 0, numberOfQuarantineDays = 0;

                            for (WorkedHoursSummary ws : workedHoursSummaries) {
                                if (ws.getAbsence() != null) {
                                    DicAbsenceType type = ws.getAbsence().getType();
                                    if (type != null) {
                                        if ("Quarantine".equals(type.getCode()))
                                            numberOfQuarantineDays++;
                                        else if ("paiddowntime".equals(type.getCode()))
                                            numberOfPaidDowntime++;
                                    }
                                }
                            }

                            timecard.setDowntime(numberOfPaidDowntime);
                            timecard.setQuarantine(numberOfQuarantineDays);
                            timecard.setPlanWorkHours(planWorkHours);
                            timecard.setPlanWorkDays(planWorkDays);
                            timecard.setBaseWorkHours(baseWorkHours);
                            timecard.setBaseWorkDays(baseWorkDays);
                            timecard.setFactHoursWithoutOvertime(factHoursWithoutOvertime);
                            timecard.setFactWorkDays(factWorkDays);
                            timecard.setNightHours(nightHours);
                            timecard.setAnnualVacationDays(annualVacationDays);
                            timecard.setUnpaidVacationDays(unpaidVacationDays);
                            timecard.setSickDays(sickDays);
                            timecard.setAbsenceDays(absenceDays);
                            timecard.setTotalFreeDays(totalFreeDays);
                            timecard.setBussinessTrip(bussinessTrip);
                            timecard.setTotalAbsence(totalAbsence);
                            timecard.setTotalWorkedDays(totalWorkedDays);
                            timecard.setGrandTotalDays(grandTotalDays);
                            timecard.setOvertimeHours(overTime);
                            timecard.setDayHours(dayHours);

                            Boolean containsAssGroup = false;
                            if (!list.isEmpty() && timecard.getAssignmentGroupId() != null) {
                                containsAssGroup = list.stream().anyMatch(t -> t.getAssignmentGroupId() != null && t.getAssignmentGroupId().equals(timecard.getAssignmentGroupId()));
                            }
                            if (containsAssGroup) {
                                if (timecard.getTimecardHeaders().isEmpty()) {  //if there is already exist that person but that instance is empty one
                                    continue;
                                }
                            }
                        }
                    }

                    list.add(timecard);
                }
            }
        }
        return list;
    }

    @Override
    public List<Timecard> getTimecards(OrganizationGroupExt organizationGroup,
                                       PersonGroupExt personGroup, Date startDate, Date endDate, int firstResult, int maxResults, boolean loadFullData, AssignmentExt
                                               assignmentExt, Boolean enableInclusions) {
        boolean mergeHeaders = timecardConfig.getMergeTimecards();

        List<Timecard> list = new ArrayList<>();

        List<Object[]> objects;
        String queryString = "";

        Object firstParameter = null;

        if (organizationGroup != null) {
            queryString = getQueryForOrganization(assignmentExt, mergeHeaders, enableInclusions, loadFullData);
//            if (enableInclusions) {
//                firstParameter = "%" + organizationGroup.getId().toString() + "%";
//            } else {
            firstParameter = organizationGroup.getId();
//            }
        }
        if (personGroup != null) {
            queryString = getQueryForPerson(assignmentExt, mergeHeaders);
            firstParameter = personGroup.getId();
        }
        List<DicScheduleElementType> dicScheduleElementTypes = new ArrayList<>();
        List<DicAbsenceType> dicAbsenceTypes = new ArrayList<>();
        List<DicBusinessTripType> dicBusinessTripTypes = new ArrayList<>();

        if (!loadFullData) {
            dicScheduleElementTypes = commonService.getEntities(DicScheduleElementType.class, "select e from tsadv$DicScheduleElementType e", null, View.LOCAL);
            dicAbsenceTypes = commonService.getEntities(DicAbsenceType.class, "select e from tsadv$DicAbsenceType e", null, View.LOCAL);
            dicBusinessTripTypes = commonService.getEntities(DicBusinessTripType.class, "select e from tsadv$DicBusinessTripType e", null, View.LOCAL);
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
            query.setParameter(6, true);
            if (assignmentExt != null) {
                query.setParameter(7, assignmentExt.getId());
            }

            objects = query.getResultList();

            if (objects != null && !objects.isEmpty()) {
                for (Object[] row : objects) {
                    Timecard timecard = new Timecard();
                    HashMap<UUID, TimecardHeader> headerMap = new HashMap<>();
                    ArrayList<TimecardHeader> timecardHeaders = new ArrayList<>();
                    timecard.setName(row[0] == null ? "" : (String) row[0]);

                    String assignmentGroupString = (String) row[1];
                    if (assignmentGroupString != null && !assignmentGroupString.isEmpty()) {
                        String[] strings = assignmentGroupString.split(fieldsDelimiter);
                        timecard.setAssignmentGroupId(UUID.fromString(strings[0]));
                        timecard.setAssignmentGroupLegacyId(strings[1].equals("(none)") ? null : strings[1]);
                        timecard.setCostCenterString(strings[2].equals("(none)") ? null : strings[2]);
                        timecard.setPositionName(strings[3]);
                    }

                    if (row.length >= 5) {
                        timecard.setSchedules(row[4] != null ? String.valueOf(row[4]) : "");
                    }
                    if (!loadFullData) {
                        String timecardHeadersString = (String) row[2];
                        if (timecardHeadersString != null && !timecardHeadersString.isEmpty()) {
                            String[] timcardHeaderEntities = timecardHeadersString.split(entityDelimiter);

                            for (String entity : timcardHeaderEntities) {
                                String[] strings = entity.split(fieldsDelimiter);
                                TimecardHeader timecardHeader = new TimecardHeader();
                                timecardHeader.setId(UUID.fromString(strings[0]));

                                Date effectiveStartDate = null;
                                Date effectiveEndDate = null;
                                try {
                                    effectiveStartDate = format.parse(strings[1]);
                                    effectiveEndDate = format.parse(strings[2]);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                timecardHeader.setEffectiveStartDate(effectiveStartDate);
                                timecardHeader.setEffectiveEndDate(effectiveEndDate);
                                if (strings.length > 3) {
                                    AssignmentGroupExt assignmentGroup = metadata.create(AssignmentGroupExt.class);
                                    assignmentGroup.setId(timecard.getAssignmentGroupId());

                                    timecardHeader.setAssignmentGroup(assignmentGroup);
                                    timecardHeader.setPlanWorkHours(Double.valueOf(strings[3]));
                                    timecardHeader.setBaseWorkHours(Double.valueOf(strings[4]));
                                    timecardHeader.setPlanWorkHoursPart(Double.valueOf(strings[5]));
                                    timecardHeader.setPlanWorkDays(Integer.valueOf(strings[6]));
                                    timecardHeader.setBaseWorkDays(Integer.valueOf(strings[7]));
                                    timecardHeader.setFactHoursWithoutOvertime(Double.valueOf(strings[8]));
                                    timecardHeader.setWeekendHours(Double.valueOf(strings[9]));
                                    timecardHeader.setWeekendDays(Integer.valueOf(strings[10]));
                                    timecardHeader.setFactWorkDays(Integer.valueOf(strings[11]));
                                    timecardHeader.setNightHours(Double.valueOf(strings[12]));
                                    timecardHeader.setHolidayHours(Double.valueOf(strings[13]));
                                    timecardHeader.setHolidayDays(Integer.valueOf(strings[14]));
                                    timecardHeader.setAnnualVacationDays(Integer.valueOf(strings[15]));
                                    timecardHeader.setUnpaidVacationDays(Integer.valueOf(strings[16]));
                                    timecardHeader.setMaternityVacationDays(Integer.valueOf(strings[17]));
                                    timecardHeader.setChildcareVacationDays(Integer.valueOf(strings[18]));
                                    timecardHeader.setSickDays(Integer.valueOf(strings[19]));
                                    timecardHeader.setAbsenceDays(Integer.valueOf(strings[20]));
                                    timecardHeader.setTotalFreeDays(Integer.valueOf(strings[21]));
                                    timecardHeader.setBusTripDays(Integer.valueOf(strings[22]));
                                    timecardHeader.setTotalAbsence(Integer.valueOf(strings[23]));
                                    timecardHeader.setTotalWorkedDays(Integer.valueOf(strings[24]));
                                    timecardHeader.setGrandTotalDays(Integer.valueOf(strings[25]));
                                    timecardHeader.setOvertimeHours(Double.valueOf(strings[26]));

                                    AssignmentSchedule assignmentSchedule = metadata.create(AssignmentSchedule.class);
                                    assignmentSchedule.setId(UUID.fromString(strings[27]));
                                    timecardHeader.setAssignmentSchedule(assignmentSchedule);

                                    UUID timecardCorrectionId = strings[28].equals("null") ? null : UUID.fromString(strings[28]);
                                    if (timecardCorrectionId != null) {
                                        TimecardCorrection timecardCorrection = metadata.create(TimecardCorrection.class);
                                        timecardCorrection.setId(timecardCorrectionId);
                                        timecardHeader.setTimecardCorrection(timecardCorrection);
                                    }

                                    timecardHeader.setDayHours(Double.valueOf(strings[29]));
                                }
                                timecardHeaders.add(timecardHeader);
                                headerMap.put(timecardHeader.getId(), timecardHeader);
                            }
                        }
                        boolean corrective = timecardHeaders.stream().anyMatch(t -> t.getTimecardCorrection() != null);
                        timecard.setCorrective(corrective);
                        timecard.setTimecardHeaders(timecardHeaders);

                        List<WorkedHoursSummary> workedHoursSummaries = new ArrayList<>();
                        String workedSummariesString = (String) row[3];
                        if (workedSummariesString != null && !workedSummariesString.isEmpty()) {
                            String[] workedSummaryEntities = workedSummariesString.split(entityDelimiter);
                            if (workedSummaryEntities.length > 0) {
                                for (String entity : workedSummaryEntities) {
                                    WorkedHoursSummary workedHoursSummary = metadata.create(WorkedHoursSummary.class);
                                    String[] strings = entity.split(fieldsDelimiter);
                                    workedHoursSummary.setId(UUID.fromString(strings[0]));

                                    TimecardHeader timecardHeader = headerMap.get(UUID.fromString(strings[1]));
                                    workedHoursSummary.setTimecardHeader(timecardHeader);

                                    Date workedDate = null;
                                    try {
                                        workedDate = format.parse(strings[2]);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    workedHoursSummary.setWorkedDate(workedDate);

                                    Double hours = Double.valueOf(strings[3]);
                                    workedHoursSummary.setHours(hours);

                                    DicScheduleElementType dicScheduleElementType = metadata.create(DicScheduleElementType.class);
                                    dicScheduleElementType.setId(UUID.fromString(strings[4]));

                                    if (!loadFullData) {
                                        if (dicScheduleElementTypes.contains(dicScheduleElementType)) {
                                            int indexOf = dicScheduleElementTypes.indexOf(dicScheduleElementType);
                                            dicScheduleElementType = dicScheduleElementTypes.get(indexOf);
                                        }
                                    }
                                    workedHoursSummary.setScheduleElementType(dicScheduleElementType);

                                    if (!strings[5].equals("null")) {
                                        Absence absence = metadata.create(Absence.class);
                                        absence.setId(UUID.fromString(strings[5]));
                                        if (!strings[6].equals("null")) {
                                            DicAbsenceType absenceType = metadata.create(DicAbsenceType.class);
                                            absenceType.setId(UUID.fromString(strings[6]));
                                            if (!loadFullData) {
                                                if (dicAbsenceTypes.contains(absenceType)) {
                                                    int indexOf = dicAbsenceTypes.indexOf(absenceType);
                                                    absenceType = dicAbsenceTypes.get(indexOf);
                                                }
                                            }

                                            absence.setType(absenceType);
                                        }
                                        workedHoursSummary.setAbsence(absence);

                                    }

                                    if (!strings[7].equals("null")) {
                                        BusinessTrip businessTrip = metadata.create(BusinessTrip.class);
                                        businessTrip.setId(UUID.fromString(strings[7]));
                                        if (!strings[8].equals("null")) {
                                            DicBusinessTripType businessTripType = metadata.create(DicBusinessTripType.class);
                                            businessTripType.setId(UUID.fromString(strings[8]));
                                            if (!loadFullData) {
                                                if (dicBusinessTripTypes.contains(businessTripType)) {
                                                    int indexOf = dicBusinessTripTypes.indexOf(businessTripType);
                                                    businessTripType = dicBusinessTripTypes.get(indexOf);
                                                }
                                            }

                                            businessTrip.setType(businessTripType);
                                        }
                                        workedHoursSummary.setBussinessTrip(businessTrip);
                                    }

                                    if (!strings[9].equals("null")) {
                                        workedHoursSummary.setDisplayValue(strings[9]);
                                    }

                                    workedHoursSummaries.add(workedHoursSummary);

                                    if (!loadFullData) {
                                        int dayOfMonth = datesService.getDayOfMonth(workedDate);
                                        try {
                                            Method setSMethod = Timecard.class.getMethod("setSummary" + dayOfMonth, WorkedHoursSummary.class);
                                            setSMethod.invoke(timecard, workedHoursSummary);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                }


                            }
                            if (timecardConfig.getMultipleAbsencesToOneDay() && !loadFullData) {
                                String absencesToSummariesString = (String) row[5];
                                if (absencesToSummariesString != null && !absencesToSummariesString.isEmpty()) {
                                    String[] entities = absencesToSummariesString.split(entityDelimiter);
                                    List<String> stringsArray = new ArrayList<>(Arrays.asList(entities));
                                    for (WorkedHoursSummary workedHoursSummary : workedHoursSummaries) {
                                        Set<AbsenceToWorkedHoursSummary> absenceToWorkedHoursSummarySet = new HashSet<>();
                                        for (String s : stringsArray) {
                                            String[] strings = s.split(fieldsDelimiter);
                                            if (s.contains(workedHoursSummary.getId().toString())) {
                                                AbsenceToWorkedHoursSummary absenceToWorkedHoursSummary = metadata.create(AbsenceToWorkedHoursSummary.class);
                                                absenceToWorkedHoursSummary.setId(strings[0].equals("(none)") ? null : UUID.fromString(strings[0]));
                                                absenceToWorkedHoursSummary.setWorkedHoursSummary(workedHoursSummary);

                                                if (!strings[2].equals("null")) {
                                                    Absence absence = metadata.create(Absence.class);
                                                    absence.setId(UUID.fromString(strings[2]));
                                                    if (!strings[3].equals("null")) {
                                                        DicAbsenceType absenceType = metadata.create(DicAbsenceType.class);
                                                        absenceType.setId(UUID.fromString(strings[3]));
                                                        if (!loadFullData) {
                                                            if (dicAbsenceTypes.contains(absenceType)) {
                                                                int indexOf = dicAbsenceTypes.indexOf(absenceType);
                                                                absenceType = dicAbsenceTypes.get(indexOf);
                                                            }
                                                        }

                                                        absence.setType(absenceType);
                                                    }
                                                    absenceToWorkedHoursSummary.setAbsence(absence);
                                                    absenceToWorkedHoursSummarySet.add(absenceToWorkedHoursSummary);
                                                }
                                            }
                                        }
                                        workedHoursSummary.setAbsenceToWorkedHoursSummaryList(new ArrayList<>(absenceToWorkedHoursSummarySet));
                                    }
                                }
                            }

                            timecard.setTimecardHeaders(timecardHeaders);
                            timecard.setWorkedHoursSummaries(workedHoursSummaries);
                            double planWorkHours = timecardHeaders.stream().filter(e -> e.getPlanWorkHours() != null).mapToDouble(TimecardHeader::getPlanWorkHours).sum();
                            Integer planWorkDays = timecardHeaders.stream().filter(e -> e.getPlanWorkDays() != null).mapToInt(TimecardHeader::getPlanWorkDays).sum();
                            Double baseWorkHours = timecardHeaders.stream().filter(e -> e.getBaseWorkHours() != null).mapToDouble(TimecardHeader::getBaseWorkHours).sum();
                            Integer baseWorkDays = timecardHeaders.stream().filter(e -> e.getBaseWorkDays() != null).mapToInt(TimecardHeader::getBaseWorkDays).sum();
                            Double factHoursWithoutOvertime = timecardHeaders.stream().filter(e -> e.getFactHoursWithoutOvertime() != null).mapToDouble(TimecardHeader::getFactHoursWithoutOvertime).sum();
                            Integer factWorkDays = timecardHeaders.stream().filter(e -> e.getFactWorkDays() != null).mapToInt(TimecardHeader::getFactWorkDays).sum();
                            Double nightHours = timecardHeaders.stream().filter(e -> e.getNightHours() != null).mapToDouble(TimecardHeader::getNightHours).sum();
                            Integer annualVacationDays = timecardHeaders.stream().filter(e -> e.getAnnualVacationDays() != null).mapToInt(TimecardHeader::getAnnualVacationDays).sum();
                            Integer unpaidVacationDays = timecardHeaders.stream().filter(e -> e.getUnpaidVacationDays() != null).mapToInt(TimecardHeader::getUnpaidVacationDays).sum();
                            Integer sickDays = timecardHeaders.stream().filter(e -> e.getSickDays() != null).mapToInt(TimecardHeader::getSickDays).sum();
                            Integer absenceDays = timecardHeaders.stream().filter(e -> e.getAbsenceDays() != null).mapToInt(TimecardHeader::getAbsenceDays).sum();
                            Integer totalFreeDays = timecardHeaders.stream().filter(e -> e.getTotalFreeDays() != null).mapToInt(TimecardHeader::getTotalFreeDays).sum();
                            Integer bussinessTrip = timecardHeaders.stream().filter(e -> e.getBusTripDays() != null).mapToInt(TimecardHeader::getBusTripDays).sum();
                            Integer totalAbsence = timecardHeaders.stream().filter(e -> e.getTotalAbsence() != null).mapToInt(TimecardHeader::getTotalAbsence).sum();
                            Integer totalWorkedDays = timecardHeaders.stream().filter(e -> e.getTotalWorkedDays() != null).mapToInt(TimecardHeader::getTotalWorkedDays).sum();
                            Integer grandTotalDays = timecardHeaders.stream().filter(e -> e.getGrandTotalDays() != null).mapToInt(TimecardHeader::getGrandTotalDays).sum();
                            Double overTime = timecardHeaders.stream().filter(e -> e.getOvertimeHours() != null).mapToDouble(TimecardHeader::getOvertimeHours).sum();
                            Double dayHours = timecardHeaders.stream().filter(e -> e.getDayHours() != null).mapToDouble(TimecardHeader::getDayHours).sum();
                            Integer numberOfPaidDowntime = 0, numberOfQuarantineDays = 0;

                            for (WorkedHoursSummary ws : workedHoursSummaries) {
                                if (ws.getAbsence() != null) {
                                    DicAbsenceType type = ws.getAbsence().getType();
                                    if (type != null) {
                                        if ("Quarantine".equals(type.getCode()))
                                            numberOfQuarantineDays++;
                                        else if ("paiddowntime".equals(type.getCode()))
                                            numberOfPaidDowntime++;
                                    }
                                }
                            }

                            timecard.setDowntime(numberOfPaidDowntime);
                            timecard.setQuarantine(numberOfQuarantineDays);
                            timecard.setPlanWorkHours(planWorkHours);
                            timecard.setPlanWorkDays(planWorkDays);
                            timecard.setBaseWorkHours(baseWorkHours);
                            timecard.setBaseWorkDays(baseWorkDays);
                            timecard.setFactHoursWithoutOvertime(factHoursWithoutOvertime);
                            timecard.setFactWorkDays(factWorkDays);
                            timecard.setNightHours(nightHours);
                            timecard.setAnnualVacationDays(annualVacationDays);
                            timecard.setUnpaidVacationDays(unpaidVacationDays);
                            timecard.setSickDays(sickDays);
                            timecard.setAbsenceDays(absenceDays);
                            timecard.setTotalFreeDays(totalFreeDays);
                            timecard.setBussinessTrip(bussinessTrip);
                            timecard.setTotalAbsence(totalAbsence);
                            timecard.setTotalWorkedDays(totalWorkedDays);
                            timecard.setGrandTotalDays(grandTotalDays);
                            timecard.setOvertimeHours(overTime);
                            timecard.setDayHours(dayHours);

                            Boolean containsAssGroup = false;
                            if (!list.isEmpty() && timecard.getAssignmentGroupId() != null) {
                                containsAssGroup = list.stream().anyMatch(t -> t.getAssignmentGroupId() != null && t.getAssignmentGroupId().equals(timecard.getAssignmentGroupId()));
                            }
                            if (containsAssGroup) {
                                if (timecard.getTimecardHeaders().isEmpty()) {  //if there is already exist that person but that instance is empty one
                                    continue;
                                }
                            }
                        }
                    }

                    list.add(timecard);
                }
            }
        }
        return list;
    }

    @Override
    public int getTimecardsCount(OrganizationGroupExt organizationGroup, PositionGroupExt
            positionGroup, PersonGroupExt personGroup, Date startDate, Date endDate, Boolean enableInclusions) {
        return getTimecards(organizationGroup, positionGroup, personGroup, startDate, endDate, 0, 0, true, null, enableInclusions).size();
    }

    @Override
    public void copyTimecard(Timecard timecard, List<PersonGroupExt> selectedPersonGroups, Date monthFromCopy, Date monthForCopy, Boolean copyDeviationsToo) throws Exception {
        CommitContext commitContext = new CommitContext();
        List<TimecardHeader> timecardHeaders = timecard.getTimecardHeaders();
        int monthDiff = datesService.getMonthsDifference(monthFromCopy, monthForCopy);
        for (TimecardHeader timecardHeader : timecardHeaders) {
            for (PersonGroupExt personGroup : selectedPersonGroups) {
                AssignmentSchedule assignmentSchedule = metadata.create(AssignmentSchedule.class);
                assignmentSchedule.setAssignmentGroup(employeeService.getAssignmentGroupByPersonGroup(personGroup));

                timecardHeader = dataManager.reload(timecardHeader, "timecardHeader-for-copy-timecard");

                Date newStartDate = DateUtils.addMonths(timecardHeader.getAssignmentSchedule().getStartDate(), monthDiff);
                Date newEndDate = DateUtils.addMonths(timecardHeader.getAssignmentSchedule().getEndDate(), monthDiff);
                assignmentSchedule.setStartDate(newStartDate);
                assignmentSchedule.setEndDate(newEndDate);


                assignmentSchedule.setColorsSet(timecardHeader.getAssignmentSchedule().getColorsSet());
                assignmentSchedule.setOffset(timecardHeader.getAssignmentSchedule().getOffset());
                assignmentSchedule.setSchedule(timecardHeader.getAssignmentSchedule().getSchedule());

                commitContext.addInstanceToCommit(assignmentSchedule);

            }

        }
        dataManager.commit(commitContext);

        Map<Timecard, List<TimecardDeviation>> timecards = new HashMap<>();

        for (TimecardHeader timecardHeader : timecardHeaders) {
            List<TimecardDeviation> deviations = getDeviationsForHeader(timecardHeader);

            for (PersonGroupExt personGroup : selectedPersonGroups) {


                Date newStartDate = DateUtils.addMonths(timecardHeader.getEffectiveStartDate(), monthDiff);
                Date newEndDate = DateUtils.addMonths(timecardHeader.getEffectiveEndDate(), monthDiff);

                List<Timecard> timecardsForPerson = getTimecards(null,
                        null,
                        personGroup,
                        newStartDate, newEndDate, 0, 0,
                        true, null, false);

                timecardsForPerson.forEach(e -> timecards.put(e, deviations));

            }

        }
        Date monthEnd = datesService.getMonthEnd(monthForCopy);

        if (copyDeviationsToo) {
            for (Map.Entry<Timecard, List<TimecardDeviation>> entry : timecards.entrySet()) {
                if (!entry.getValue().isEmpty()) {
                    Set<Timecard> reloadedTimecards = timecards.keySet();
                    reloadedTimecards.forEach(e -> {
                        e.setWorkedHoursSummaries(getWorkedHoursSummariesForPeriod(new ArrayList<>(Arrays.asList(e.getAssignmentGroupId())), monthForCopy, monthEnd));
                        e.setTimecardHeaders(getTimecardHeaders(new ArrayList<>(Arrays.asList(e.getAssignmentGroupId())), monthForCopy, monthEnd));
                    });
                    entry.getValue().forEach(d ->
                            insertDeviations(new HashSet<>(Arrays.asList(entry.getKey())), d.getHours(), DateUtils.addMonths(d.getDateFrom(), monthDiff), DateUtils.addMonths(d.getDateTo(), monthDiff), d.getIsChangesFactHours(), d.getIsChangesPlanHours(), d.getIsChangesDetailsFromBegin(), copyDeviationsToo)
                    );
                }

            }
        }
        formTimecard(monthForCopy, timecards.keySet());
    }

    private List<TimecardDeviation> getDeviationsForHeader(TimecardHeader timecardHeader) {
        Date startDate = timecardHeader.getEffectiveStartDate();
        Date endDate = timecardHeader.getEffectiveEndDate();
        timecardHeader = dataManager.reload(timecardHeader, "timecardHeader-with-assignment-group");
        UUID assignmentGroupId = timecardHeader.getAssignmentGroup().getId();
        return commonService.getEntities(TimecardDeviation.class, "select o from tsadv$TimecardDeviation o " +
                        " where o.assignmentGroup.id = :assignmentGroupId " +
                        "     and ((o.dateFrom <= :endDate and o.dateFrom >= :startDate) " +
                        "     or (o.dateTo <= :endDate and o.dateTo >= :startDate) " +
                        "     or (o.dateFrom >= :startDate and o.dateTo <= :endDate) " +
                        "     or (o.dateFrom <= :endDate and o.dateTo >= :endDate)) ",
                ParamsMap.of("assignmentGroupId", assignmentGroupId, "startDate", startDate, "endDate", endDate),
                "timecardDeviation-for-timecard-copy");
    }

    protected List<TimecardDeviation> getDeviationsForMonth(List<Timecard> timecards, Date startDate, Date endDate) {

        List<UUID> assignmentGroupIds = timecards.stream()
                .filter(t -> t.getAssignmentGroupId() != null)
                .map(Timecard::getAssignmentGroupId).collect(Collectors.toList());

        List<AssignmentGroupExt> assignmentGroups = getAssignmentGroupsByIds(assignmentGroupIds);

        String queryString = "select o from tsadv$TimecardDeviation o " +
                "              where o.assignmentGroup.id in :assignmentGroups " +
                "                and o.dateFrom <= :endDate " +
                "                and o.dateTo >= :startDate";

        return commonService.getEntities(TimecardDeviation.class,
                queryString,
                ParamsMap.of("assignmentGroups", assignmentGroups,
                        "startDate", startDate,
                        "endDate", endDate),
                "timecardDeviation-for-form-timecard");
    }


    private List<AssignmentGroupExt> getAssignmentGroupsByIds(List<UUID> assignmentGroupIds) {
        List<AssignmentGroupExt> assignmentGroupExts = new ArrayList<>();
        for (UUID id : assignmentGroupIds) {
            AssignmentGroupExt assignmentGroupExt = metadata.create(AssignmentGroupExt.class);
            assignmentGroupExt.setId(id);
            assignmentGroupExts.add(assignmentGroupExt);
        }
        return assignmentGroupExts;
    }


    protected String getQueryForOrganization(AssignmentExt assignmentExt, boolean mergeHeaders, boolean enableInclusions, boolean loadFullData) {
        /*0*/
        return "SELECT p.last_name || ' ' || p.first_name || ' ' || coalesce(p.middle_name,'') || '(' || coalesce(p.employee_number,'') || ')',\n" +
                /*1*/         "   a.group_id || ?4 || coalesce(a.legacy_id, '(none)') || ?4 || coalesce(tdcc.lang_value1, '(none)') || ?4 || pos.position_full_name_lang1,\n" +
                /*2*/    " string_agg(DISTINCT(th.id) || ?4 || th.effective_start_date || ?4 || th.effective_end_date || ?4" +
                "          || th.plan_work_hours  || ?4 ||  th.base_work_hours || ?4 || th.PLAN_WORK_HOURS_PART || ?4 ||  th.plan_work_days || ?4 " +
                "          || th.base_work_days || ?4 || th.fact_hours_without_overtime || ?4 || th.weekend_hours || ?4 || th.weekend_days || ?4 || th.fact_work_days || ?4 || th.night_hours || ?4" +
                "          || th.HOLIDAY_HOURS || ?4 || th.HOLIDAY_DAYS || ?4" +
                "          || th.annual_vacation_days || ?4 || th.unpaid_vacation_days || ?4 || th.MATERNITY_VACATION_DAYS || ?4 || th.CHILDCARE_VACATION_DAYS || ?4 || th.sick_days || ?4 || th.absence_days || ?4" +
                "          || th.total_free_days || ?4 || th.bus_trip_days || ?4 || th.total_absence || ?4 || th.total_worked_days || ?4" +
                "          || th.grand_total_days || ?4 || th.overtime_hours || ?4 || th.assignment_schedule_id || ?4 ||  coalesce(th.TIMECARD_CORRECTION_ID::TEXT, 'null'),  ?5) as TH\n" +
                (loadFullData ? "" :
                        /*3*/     ",  string_agg(DISTINCT(ws.id) || ?4 || ws.timecard_header_id || ?4 || ws.worked_date || ?4 || ws.hours || ?4 || ws.schedule_element_type_id " +
                        "          || ?4 || coalesce(ws.absence_id::TEXT, 'null') || ?4 || coalesce(abs.type_id::TEXT, 'null')  || ?4 || coalesce(ws.bussiness_trip_id ::TEXT, 'null')" +
                        "          || ?4 || coalesce(bt.type_id ::TEXT, 'null') || ?4 || coalesce(ws.display_value::TEXT, 'null'), ?5) as worked_summaries,\n" +
                        /*4*/    " string_agg(DISTINCT(ss.schedule_name) || '(' || tso.offset_display || ')', ',') as schedules," +
                        /*5*/    "  string_agg(coalesce(atwhs.id ::TEXT, 'null') || ?4 || coalesce(atwhs.worked_hours_summary_id ::TEXT, 'null') || ?4 || coalesce(atoa.id ::TEXT, 'null') || ?4 || coalesce(atoa.type_id ::TEXT, 'null'), ?5) as absencesToSummaries\n") +
                "    FROM base_person p\n" +
                "    JOIN base_assignment a\n" +
                "       ON a.person_group_id = p.group_id\n" +
                "       AND a.primary_flag is true \n" +
                (assignmentExt == null ? "" : " AND a.id = ?6 ") +
                "  JOIN tsadv_organization_structure os\n" +
                "    ON os.organization_group_id = a.organization_group_id\n" +
                (enableInclusions ? " AND os.path LIKE ?1\n" : " AND os.organization_group_id = ?1\n") +
                "   left join base_organization oo on oo.group_id = a.organization_group_id\n" +
                "       left join tsadv_dic_cost_center tdcc on tdcc.id = oo.cost_center_id\n" +
                " join tsadv_dic_assignment_status ass on ass.id = a.assignment_status_id\n" +
                "  join tsadv_dic_person_type pt on pt.id = p.type_id\n" +
                "   LEFT JOIN tsadv_assignment_schedule tas ON tas.assignment_group_id = a.group_id\n" +
                "                                             AND ?2 <= tas.end_date\n" +
                "                                             AND ?3 >= tas.start_date\n" +
                "                                             AND tas.delete_ts IS NULL\n" +
                "  LEFT JOIN TSADV_TIMECARD_HEADER th on th.assignment_group_id = a.group_id\n" +
                "                                        AND ?2 <= th.effective_end_date\n" +
                "                                        AND ?3 >= th.effective_start_date\n" +
                "                                        and th.effective_start_date <= a.end_date\n" +
                "                                        and th.effective_end_date >= a.start_date" +
                "                                        AND th.assignment_schedule_id=tas.id\n" +
                "                                        AND th.delete_ts IS NULL\n" +
                "  LEFT JOIN TSADV_WORKED_HOURS_SUMMARY ws on ws.timecard_header_id = th.id\n" +
                "                                             AND ?2 <= ws.worked_date\n" +
                "                                             AND ?3 >= ws.worked_date\n" +
                "                                             AND ws.delete_ts IS NULL" +
                "  LEFT JOIN tsadv_standard_schedule ss ON ss.id = tas.schedule_id\n" +
                "  LEFT JOIN tsadv_standard_offset tso ON tso.id = tas.offset_id " +
                "  LEFT JOIN tsadv_absence abs ON abs.id = ws.absence_id" +
                "                              and abs.delete_ts is null " +
                "  LEFT JOIN tsadv_absence_to_worked_hours_summary atwhs ON atwhs.worked_hours_summary_id = ws.id" +
                "                              and atwhs.delete_ts is null " +
                "  LEFT JOIN tsadv_absence atoa ON atoa.id = atwhs.absence_id " +
                "                              and atoa.delete_ts is null " +
                "  LEFT JOIN tsadv_business_trip bt ON bt.id = ws.bussiness_trip_id " +
                "                                                   and bt.delete_ts is null " +
//                "                                                   and bt.status != 'PLANNED' " +
                "  LEFT JOIN base_position_group posg ON posg.id = a.position_group_id\n" +
                "                                                    and posg.delete_ts is null\n" +
                "  LEFT JOIN base_position pos ON pos.group_id = posg.id\n" +
                "                                                    and pos.delete_ts is null" +
                "                                                      AND pos.start_date <= ?3\n" +
                "                                                      AND pos.end_date >= ?2 " +
                "      where ?2 <= p.end_date\n" +
                "      and ?3 >= p.start_date\n" +
                "     and ?2 <= a.end_date\n" +
                "     and ?3 >= a.start_date\n" +
                "     and (ass.code = 'ACTIVE' OR ass.code = 'SUSPENDED')\n" +
                "     AND (pt.code = 'EMPLOYEE' or pt.code = 'PAIDINTERN')\n" +
                " group by p.first_name, p.last_name, p.middle_name, p.employee_number, a.group_id, a.legacy_id, tdcc.lang_value1, pos.position_full_name_lang1"
                + (mergeHeaders ? "" : " , th.id ") +
                " order by p.last_name";
    }

    protected String getQueryForPosition(AssignmentExt assignmentExt, boolean mergeHeaders) {
        return "SELECT p.last_name || ' ' || p.first_name || ' ' || coalesce(p.middle_name,'') || '(' || coalesce(p.employee_number,'') || ')',\n" +
                /*1*/         "   a.group_id || ?4 || coalesce(a.legacy_id, '(none)') || ?4 || coalesce(tdcc.lang_value1, '(none)') || ?4 || pos.position_full_name_lang1,\n" +
                /*2*/    " string_agg(DISTINCT(th.id) || ?4 || th.effective_start_date || ?4 || th.effective_end_date || ?4" +
                "          || th.plan_work_hours  || ?4 ||  th.base_work_hours || ?4 || th.PLAN_WORK_HOURS_PART || ?4 ||  th.plan_work_days || ?4 " +
                "          || th.base_work_days || ?4 || th.fact_hours_without_overtime || ?4 || th.weekend_hours || ?4 || th.weekend_days || ?4 || th.fact_work_days || ?4 || th.night_hours || ?4" +
                "          || th.HOLIDAY_HOURS || ?4 || th.HOLIDAY_DAYS || ?4" +
                "          || th.annual_vacation_days || ?4 || th.unpaid_vacation_days || ?4 || th.MATERNITY_VACATION_DAYS || ?4 || th.CHILDCARE_VACATION_DAYS || ?4 || th.sick_days || ?4 || th.absence_days || ?4" +
                "          || th.total_free_days || ?4 || th.bus_trip_days || ?4 || th.total_absence || ?4 || th.total_worked_days || ?4" +
                "          || th.grand_total_days || ?4 || th.overtime_hours || ?4 || th.assignment_schedule_id || ?4 ||  coalesce(th.TIMECARD_CORRECTION_ID::TEXT, 'null'),  ?5) as TH,\n" +
                /*3*/    "  string_agg(DISTINCT(ws.id) || ?4 || ws.timecard_header_id || ?4 || ws.worked_date || ?4 || ws.hours || ?4 || ws.schedule_element_type_id || ?4 || coalesce(ws.absence_id::TEXT, 'null') || ?4 || coalesce(abs.type_id::TEXT, 'null')  || ?4 " +
                " || coalesce(ws.bussiness_trip_id ::TEXT, 'null') || ?4 || coalesce(bt.type_id ::TEXT, 'null') || ?4 || coalesce(ws.display_value::TEXT, 'null'),  ?5) as worked_summaries,\n" +
                /*4*/    "  string_agg(DISTINCT(ss.schedule_name) || '(' || tso.offset_display || ')', ',') as schedules, " +
                /*5*/     "  string_agg(coalesce(atwhs.id ::TEXT, 'null') || ?4 || coalesce(atwhs.worked_hours_summary_id ::TEXT, 'null') || ?4 || coalesce(atoa.id ::TEXT, 'null') || ?4 || coalesce(atoa.type_id ::TEXT, 'null'), ?5) as absencesToSummaries\n" +
                " FROM base_person p\n" +
                "  JOIN base_assignment a\n" +
                "    ON a.person_group_id = p.group_id\n" +
                "       AND a.primary_flag is true \n" +
                (assignmentExt == null ? "" : " AND a.id = ?6 ") +
                "  JOIN tsadv_dic_assignment_status ass\n" +
                "    ON ass.id = a.assignment_status_id\n" +
                "  JOIN tsadv_dic_person_type pt \n" +
                "    ON pt.id = p.type_id\n" +
                "   left join base_organization oo on oo.group_id = a.organization_group_id\n" +
                "       left join tsadv_dic_cost_center tdcc on tdcc.id = oo.cost_center_id\n" +
                "   LEFT JOIN tsadv_assignment_schedule tas ON tas.assignment_group_id = a.group_id\n" +
                "                                             AND ?2 <= tas.end_date\n" +
                "                                             AND ?3 >= tas.start_date\n" +
                "                                             AND tas.delete_ts IS NULL\n" +
                "  LEFT JOIN TSADV_TIMECARD_HEADER th on th.assignment_group_id = a.group_id\n" +
                "                                        AND ?2 <= th.effective_end_date\n" +
                "                                        AND ?3 >= th.effective_start_date\n" +
                "                                        and th.effective_start_date <= a.end_date\n" +
                "                                        and th.effective_end_date >= a.start_date" +
                "                                        AND th.assignment_schedule_id=tas.id\n" +
                "                                        AND th.delete_ts IS NULL\n" +
                "  LEFT JOIN TSADV_WORKED_HOURS_SUMMARY ws on ws.timecard_header_id = th.id\n" +
                "                                             AND ?2 <= ws.worked_date\n" +
                "                                             AND ?3 >= ws.worked_date\n" +
                "                                             AND ws.delete_ts IS NULL" +
                "  LEFT JOIN tsadv_standard_schedule ss ON ss.id = tas.schedule_id\n" +
                "  LEFT JOIN tsadv_standard_offset tso ON tso.id = tas.offset_id " +
                "  LEFT JOIN tsadv_absence abs ON abs.id = ws.absence_id " +
                "                              and abs.delete_ts is null " +
                "  LEFT JOIN tsadv_absence_to_worked_hours_summary atwhs ON atwhs.worked_hours_summary_id = ws.id" +
                "                              and atwhs.delete_ts is null " +
                "  LEFT JOIN tsadv_absence atoa ON atoa.id = atwhs.absence_id" +
                "                              and atoa.delete_ts is null " +
                "  LEFT JOIN tsadv_business_trip bt ON bt.id = ws.bussiness_trip_id " +
                "                                                   and bt.delete_ts is null " +
//                "                                                   and bt.status != 'PLANNED' " +
                "  LEFT JOIN base_position_group posg ON posg.id = ?1\n" +
                "  LEFT JOIN base_position pos ON pos.group_id = posg.id\n" +
                "                                                    and pos.delete_ts is null " +
                "                                                      AND pos.start_date <= ?3\n" +
                "                                                      AND pos.end_date >= ?2 " +
                " WHERE ?2 <= p.end_date\n" +
                "      AND ?3 >= p.start_date\n" +
                "      AND ?2 <= a.end_date\n" +
                "      AND ?3 >= a.start_date\n" +
                "     and (ass.code = 'ACTIVE' OR ass.code = 'SUSPENDED')\n" +
                "      AND (pt.code = 'EMPLOYEE' or pt.code = 'PAIDINTERN')\n" +
                "      AND a.position_group_id = ?1\n" +
                " GROUP BY p.first_name, p.last_name, p.middle_name, p.employee_number, a.group_id, a.legacy_id, tdcc.lang_value1, pos.position_full_name_lang1 "
                + (mergeHeaders ? "" : " , th.id ") +
                " order by p.last_name;";
    }

    protected String getQueryForPerson(AssignmentExt assignmentExt, boolean mergeHeaders) {
        return "SELECT p.last_name || ' ' || p.first_name || ' ' || coalesce(p.middle_name,'') || '(' || coalesce(p.employee_number,'') || ')',\n" +
                /*1*/         "   a.group_id || ?4 || coalesce(a.legacy_id, '(none)') || ?4 || coalesce(tdcc.lang_value1, '(none)') || ?4 || pos.position_full_name_lang1,\n" +
                /*2*/    " string_agg(DISTINCT(th.id) || ?4 || th.effective_start_date || ?4 || th.effective_end_date || ?4" +
                "          || th.plan_work_hours  || ?4 ||  th.base_work_hours || ?4 || th.PLAN_WORK_HOURS_PART || ?4 ||  th.plan_work_days || ?4 " +
                "          || th.base_work_days || ?4 || th.fact_hours_without_overtime || ?4 || th.weekend_hours || ?4 || th.weekend_days || ?4 || th.fact_work_days || ?4 || th.night_hours || ?4" +
                "          || th.HOLIDAY_HOURS || ?4 || th.HOLIDAY_DAYS || ?4" +
                "          || th.annual_vacation_days || ?4 || th.unpaid_vacation_days || ?4 || th.MATERNITY_VACATION_DAYS || ?4 || th.CHILDCARE_VACATION_DAYS || ?4 || th.sick_days || ?4 || th.absence_days || ?4" +
                "          || th.total_free_days || ?4 || th.bus_trip_days || ?4 || th.total_absence || ?4 || th.total_worked_days || ?4" +
                "          || th.grand_total_days || ?4 || th.overtime_hours || ?4 || th.assignment_schedule_id || ?4 ||  coalesce(th.TIMECARD_CORRECTION_ID::TEXT, 'null'),  ?5) as HEADERS,\n" +
                /*3*/     "  string_agg(DISTINCT(ws.id) || ?4 || ws.timecard_header_id || ?4 || ws.worked_date || ?4 || ws.hours || ?4 || ws.schedule_element_type_id " +
                "          || ?4 || coalesce(ws.absence_id::TEXT, 'null') || ?4 || coalesce(abs.type_id::TEXT, 'null')  || ?4 || coalesce(ws.bussiness_trip_id ::TEXT, 'null')" +
                "          || ?4 || coalesce(bt.type_id ::TEXT, 'null') || ?4 || coalesce(ws.display_value::TEXT, 'null'), ?5) as worked_summaries,\n" +
                /*4*/    "   string_agg(DISTINCT(ss.schedule_name) || '(' || tso.offset_display || ')', ',') as schedules, " +
                /*5*/    "   string_agg(coalesce(atwhs.id ::TEXT, 'null') || ?4 || coalesce(atwhs.worked_hours_summary_id ::TEXT, 'null') || ?4 || coalesce(atoa.id ::TEXT, 'null') || ?4 || coalesce(atoa.type_id ::TEXT, 'null'), ?5) as absencesToSummaries\n" +
                "    FROM base_person p\n" +
                "    JOIN base_assignment a\n" +
                "       ON a.person_group_id = p.group_id\n" +
                "       AND a.person_group_id = ?1 \n" +
                "       AND a.primary_flag is true \n" +
                (assignmentExt == null ? "" : " AND a.id = ?6 ") +
                "join tsadv_dic_assignment_status ass on ass.id = a.assignment_status_id\n" +
                "  join tsadv_dic_person_type pt on pt.id = p.type_id\n" +
                "   left join base_organization oo on oo.group_id = a.organization_group_id\n" +
                "       left join tsadv_dic_cost_center tdcc on tdcc.id = oo.cost_center_id\n" +
                "   LEFT JOIN tsadv_assignment_schedule tas ON tas.assignment_group_id = a.group_id\n" +
                "                                             AND ?2 <= tas.end_date\n" +
                "                                             AND ?3 >= tas.start_date\n" +
                "                                             AND tas.delete_ts IS NULL\n" +
                "  LEFT JOIN TSADV_TIMECARD_HEADER th on th.assignment_group_id = a.group_id\n" +
                "                                        AND ?2 <= th.effective_end_date\n" +
                "                                        AND ?3 >= th.effective_start_date\n" +
                "                                        and th.effective_start_date <= a.end_date\n" +
                "                                        and th.effective_end_date >= a.start_date" +
                "                                        AND th.assignment_schedule_id=tas.id\n" +
                "                                        AND th.delete_ts IS NULL\n" +
                "  LEFT JOIN TSADV_WORKED_HOURS_SUMMARY ws on ws.timecard_header_id = th.id\n" +
                "                                             AND ?2 <= ws.worked_date\n" +
                "                                             AND ?3 >= ws.worked_date\n" +
                "                                             AND ws.delete_ts IS NULL" +
                "  LEFT JOIN tsadv_standard_schedule ss ON ss.id = tas.schedule_id\n" +
                "  LEFT JOIN tsadv_standard_offset tso ON tso.id = tas.offset_id " +
                "  LEFT JOIN tsadv_absence abs ON abs.id = ws.absence_id " +
                "                              and abs.delete_ts is null " +
                "  LEFT JOIN tsadv_absence_to_worked_hours_summary atwhs ON atwhs.worked_hours_summary_id = ws.id" +
                "                              and atwhs.delete_ts is null " +
                "  LEFT JOIN tsadv_absence atoa ON atoa.id = atwhs.absence_id" +
                "                              and atoa.delete_ts is null " +
                "  LEFT JOIN tsadv_business_trip bt ON bt.id = ws.bussiness_trip_id " +
                "                                                   and bt.delete_ts is null " +
//                "                                                   and bt.status != 'PLANNED' " +
                "  LEFT JOIN base_position_group posg ON posg.id = a.position_group_id\n" +
                "                                                    and posg.delete_ts is null\n" +
                " LEFT JOIN base_position pos ON pos.group_id = posg.id\n" +
                "                                                    and pos.delete_ts is null" +
                "                                                      AND pos.start_date <= ?3\n" +
                "                                                      AND pos.end_date >= ?2 " +
                "      where ?2 <= p.end_date\n" +
                "      and ?3 >= p.start_date\n" +
                "     and ?2 <= a.end_date\n" +
                "     and ?3 >= a.start_date\n" +
                "     and (ass.code = 'ACTIVE' OR ass.code = 'SUSPENDED')\n" +
                "     AND (pt.code = 'EMPLOYEE' or pt.code = 'PAIDINTERN')\n" +
                " group by p.first_name, p.last_name, p.middle_name, p.employee_number, a.group_id, a.legacy_id, tdcc.lang_value1, pos.position_full_name_lang1" +
                (mergeHeaders ? "" : " , th.id order by th.effective_start_date ");
    }


    @Override
    public void checkAndCreateSpecialHours(WorkedHoursDetailed workedHoursDetailed) {
        List<Entity> commitInstances = new ArrayList<>();

        List<NightPartDTO> nightParts = getNightParts(workedHoursDetailed, getElementType("NIGHT_HOURS"));
        if (!nightParts.isEmpty()) {
            nightParts.forEach(n -> commitInstances.add(createNightDetail(n)));
        }

        if (containsHolidayOrWeekendHours(workedHoursDetailed, "HOLIDAY")) {
            commitInstances.addAll(createElementDetails(workedHoursDetailed, getElementType("HOLIDAY_HOURS")));
        }

        if (containsHolidayOrWeekendHours(workedHoursDetailed, "WEEKEND")) {
            commitInstances.addAll(createElementDetails(workedHoursDetailed, getElementType("WEEKEND_HOURS")));
        }

        dataManager.commit(new CommitContext(commitInstances));
    }

    @Override
    public void calculateStatistic(TimecardHeader header) {
        TimecardHeader timecardHeader = getFullTimecardHeader(header);
        if (header.getOvertimeHours() >= 0d) {
            timecardHeader.setOvertimeHours(header.getOvertimeHours());
        }
        prepareZeroHoursHeader(timecardHeader);
        List<WorkedHoursSummary> summaries = getWorkedHoursSummaries(timecardHeader);
        timecardHeader.setPlanWorkHours(0d);
        timecardHeader.setPlanWorkDays(0);
        timecardHeader.setFactHoursWithoutOvertime(0d);

        summaries.forEach(summary -> {
            String code = summary.getScheduleElementType().getCode();
            Absence absence = summary.getAbsence();
//            int detailWithPlanHoursForWeekendsAndHolidays = 0; //wtf?
            List<WorkedHoursDetailed> details = getWorkedHoursDetailed(summary);
            boolean summaryContainsHours = false;
            boolean summaryContainsPlanHours = false;
            for (WorkedHoursDetailed detail : details) {
                DicScheduleElementType elementType = detail.getScheduleElementType();
                if (detail.getHours() > 0d && elementType.getCode().equals("WORK_HOURS")) {
                    summaryContainsHours = true;
                }

                if (detail.getPlanHours() > 0d && elementType.getCode().equals("WORK_HOURS")) {
                    summaryContainsPlanHours = true;
                }
                if (elementType.getCode().equals("WORK_HOURS")) {
                    timecardHeader.setPlanWorkHours(timecardHeader.getPlanWorkHours() + detail.getPlanHours());
                    timecardHeader.setFactHoursWithoutOvertime(timecardHeader.getFactHoursWithoutOvertime() + detail.getHours());
                }
                if (elementType.getCode().equals("NIGHT_HOURS")) {
                    timecardHeader.setNightHours(timecardHeader.getNightHours() + detail.getHours());
                } else if (elementType.getCode().equals("HOLIDAY_HOURS")) {
                    timecardHeader.setHolidayHours(timecardHeader.getHolidayHours() + detail.getHours());
                } else if (elementType.getCode().equals("WEEKEND_HOURS")) {
                    timecardHeader.setWeekendHours(timecardHeader.getWeekendHours() + detail.getHours());
                }
//                if ((code.equals("HOLIDAY") || code.equals("WEEKEND")) && absence == null && summary.getHours() > 0d) {
//                    if (detail.getPlanHours() > 0) {
//                        detailWithPlanHoursForWeekendsAndHolidays++;
//                        if (detailWithPlanHoursForWeekendsAndHolidays == 1) {
//                            timecardHeader.setPlanWorkDays(timecardHeader.getPlanWorkDays() + 1);
//                        }
//                    }
//                }
            }
//            timecardHeader.setFactHoursWithoutOvertime(timecardHeader.getFactHoursWithoutOvertime() + summary.getHours());
            timecardHeader.setFactWorkDays(timecardHeader.getFactWorkDays() + (summaryContainsHours ? 1 : 0));
            timecardHeader.setPlanWorkDays(timecardHeader.getPlanWorkDays() + (summaryContainsPlanHours ? 1 : 0));

            /* if summary has no hours */
            if (absence != null && summary.getHours() == 0d) {
                addAbsenceStatistics(absence, timecardHeader);
            }
            if ((code.equals("HOLIDAY") || code.equals("WEEKEND")) && absence == null && summary.getHours() == 0d) {
                timecardHeader.setTotalFreeDays(timecardHeader.getTotalFreeDays() + 1);
            }
            if (summary.getBussinessTrip() != null) {
                timecardHeader.setBusTripDays(timecardHeader.getBusTripDays() + 1);
            }
            timecardHeader.setTotalWorkedDays(timecardHeader.getFactWorkDays() /*+ overtime*/);
            timecardHeader.setTotalAbsence(timecardHeader.getAbsenceDays() + timecardHeader.getTotalFreeDays());
            timecardHeader.setGrandTotalDays(timecardHeader.getTotalAbsence() + timecardHeader.getTotalWorkedDays());
        });
        dataManager.commit(new CommitContext(timecardHeader));
    }

    @Override
    public void insertDeviations(Set<Timecard> selectedTimecards, double hours, Date dateFrom, Date dateTo, boolean fact, boolean plan, boolean changeHoursFromBegin, Boolean changesWeekends) {
        for (Timecard timecard : selectedTimecards) {
            AssignmentGroupExt assignmentGroupExt = metadata.create(AssignmentGroupExt.class);
            assignmentGroupExt.setId(timecard.getAssignmentGroupId());
            assignmentGroupExt.setLegacyId(timecard.getAssignmentGroupLegacyId());
            TimecardDeviation deviation = createDeviation(assignmentGroupExt, hours, dateFrom, dateTo, fact, plan, changeHoursFromBegin, changesWeekends);
            dataManager.commit(deviation);
        }
    }

    public void addDeviationsTest(List<TimecardDeviation> deviations, List<WorkedHoursSummary> summaries) throws ParseException {
        CommitContext commitContext = new CommitContext();
        List<DicScheduleElementType> dicScheduleElementTypeList = commonService.getEntities(DicScheduleElementType.class,
                "select e from tsadv$DicScheduleElementType e",
                ParamsMap.empty(),
                View.LOCAL);

        List<TimecardHeader> headersForStatisticRenew = new ArrayList<>();
        for (TimecardDeviation deviation : deviations) {

            List<WorkedHoursSummary> workedHoursSummaries = summaries.stream()
                    .filter(whs -> isRelated(deviation, whs))
                    .collect(Collectors.toList());
            workedHoursSummaries = reloadSummaries(workedHoursSummaries);


            for (WorkedHoursSummary summary : workedHoursSummaries) {

                TimecardHeader timecardHeader;
                if (headersForStatisticRenew.stream().anyMatch(th -> th.getId().equals(summary.getTimecardHeader().getId()))) {
                    timecardHeader = headersForStatisticRenew.stream()
                            .filter(th -> th.getId().equals(summary.getTimecardHeader().getId()))
                            .findAny().get();
                } else {
                    timecardHeader = summary.getTimecardHeader();
                    headersForStatisticRenew.add(summary.getTimecardHeader());
                }

                List<WorkedHoursDetailed> workedHoursDetailedList = getWorkedHoursDetailed(summary);
                if (workedHoursDetailedList.isEmpty()) {
                    WorkedHoursDetailed workedHoursDetailed = metadata.create(WorkedHoursDetailed.class);
                    workedHoursDetailed.setTimeIn(new SimpleDateFormat("HH:mm").parse("00:00"));
                    workedHoursDetailed.setActualTimeIn(new SimpleDateFormat("HH:mm").parse("00:00"));
                    workedHoursDetailed.setActualTimeOut(new SimpleDateFormat("HH:mm").parse("00:00"));
                    workedHoursDetailed.setActualTimeOut(new SimpleDateFormat("HH:mm").parse("00:00"));
                    workedHoursDetailed.setHours(0d);
                    workedHoursDetailed.setPlanHours(0d);
                    workedHoursDetailed.setWorkedHoursSummary(summary);
                    workedHoursDetailed.setTimecardHeader(summary.getTimecardHeader());

                    DicScheduleElementType dicScheduleElementType = null;
                    if (summary.getScheduleElementType() != null) {
                        if ("WEEKEND".equals(summary.getScheduleElementType().getCode())) {
                            dicScheduleElementType = dicScheduleElementTypeList.stream()
                                    .filter(dst -> WEEKEND_HOURS.equals(dst.getCode()))
                                    .findFirst().orElse(null);
                        }
                        if ("HOLIDAY".equals(summary.getScheduleElementType().getCode())) {
                            dicScheduleElementType = dicScheduleElementTypeList.stream()
                                    .filter(dst -> HOLIDAY_HOURS.equals(dst.getCode()))
                                    .findFirst().orElse(null);
                        }
                    }
                    if (dicScheduleElementType == null) {
                        dicScheduleElementType = dicScheduleElementTypeList.stream()
                                .filter(dst -> WORK_HOURS.equals(dst.getCode()))
                                .findFirst().orElse(null);
                    }
                    workedHoursDetailed.setScheduleElementType(dicScheduleElementType);
                    workedHoursDetailed.setWorkedDate(summary.getWorkedDate());
                    workedHoursDetailed.setIsNeedToCheckAndCreateAdditionalHours(false);
                    workedHoursDetailedList.add(workedHoursDetailed);
                    if (!dicScheduleElementType.getCode().equals("WORK_HOURS")) {
                        WorkedHoursDetailed workedHoursDetailed1 = metadata.create(WorkedHoursDetailed.class);
                        workedHoursDetailed1.setTimeIn(new SimpleDateFormat("HH:mm").parse("00:00"));
                        workedHoursDetailed1.setActualTimeIn(new SimpleDateFormat("HH:mm").parse("00:00"));
                        workedHoursDetailed1.setActualTimeOut(new SimpleDateFormat("HH:mm").parse("00:00"));
                        workedHoursDetailed1.setActualTimeOut(new SimpleDateFormat("HH:mm").parse("00:00"));
                        workedHoursDetailed1.setHours(0d);
                        workedHoursDetailed1.setPlanHours(0d);
                        workedHoursDetailed1.setWorkedHoursSummary(summary);
                        workedHoursDetailed1.setTimecardHeader(summary.getTimecardHeader());

                        DicScheduleElementType dicScheduleElementType1 = commonService.getEntity(DicScheduleElementType.class, "WORK_HOURS");
                        workedHoursDetailed1.setScheduleElementType(dicScheduleElementType1);
                        workedHoursDetailed1.setWorkedDate(summary.getWorkedDate());
                        workedHoursDetailed1.setIsNeedToCheckAndCreateAdditionalHours(false);
                        workedHoursDetailedList.add(workedHoursDetailed1);
                    }
                }
                List<WorkedHoursDetailed> nonWorkedHoursDetailedList = workedHoursDetailedList.stream()
                        .filter(whd -> NIGHT_HOURS.equals(whd.getScheduleElementType().getCode())
                                || HOLIDAY_HOURS.equals(whd.getScheduleElementType().getCode())
                                || WEEKEND_HOURS.equals(whd.getScheduleElementType().getCode()))
                        .collect(Collectors.toList());

                Double deviationHours = deviation.getHours();
                Double deviationPlanHours = deviation.getHours();

                if (deviation.getHours() < 0) {

                    Boolean isChangesDetailsFromBegin = deviation.getIsChangesDetailsFromBegin();
                    Comparator<WorkedHoursDetailed> orderByActualTimeIn =
                            getWorkedHoursDetailedComparator(isChangesDetailsFromBegin);
                    workedHoursDetailedList.sort(orderByActualTimeIn);

                    for (WorkedHoursDetailed workedHoursDetailed : workedHoursDetailedList) {

                        if (!WORK_HOURS.equals(workedHoursDetailed.getScheduleElementType().getCode())) {
                            continue;
                        }

                        List<WorkedHoursDetailed> currentNonWorkedHoursDetailedList = null;

                        if (deviationHours != 0 && deviation.getIsChangesFactHours()) {
                            currentNonWorkedHoursDetailedList = nonWorkedHoursDetailedList.stream()
                                    .filter(whd -> whd.getActualTimeIn().equals(workedHoursDetailed.getActualTimeIn()))
                                    .collect(Collectors.toList());
                            for (WorkedHoursDetailed nonWorkedHoursDetailed : currentNonWorkedHoursDetailedList) {
                                deviateFactHours(nonWorkedHoursDetailed, deviationHours, isChangesDetailsFromBegin);
                                commitContext.addInstanceToCommit(nonWorkedHoursDetailed);
                            }

                            deviationHours = deviateFactHours(workedHoursDetailed, deviationHours, isChangesDetailsFromBegin);

                            commitContext.addInstanceToCommit(workedHoursDetailed);
                        }

                        if (deviationPlanHours != 0 && deviation.getIsChangesPlanHours()) {
                            if (currentNonWorkedHoursDetailedList == null) {
                                currentNonWorkedHoursDetailedList = nonWorkedHoursDetailedList.stream()
                                        .filter(whd -> whd.getActualTimeIn().equals(workedHoursDetailed.getActualTimeIn()))
                                        .collect(Collectors.toList());
                            }
                            for (WorkedHoursDetailed nonWorkedHoursDetailed : currentNonWorkedHoursDetailedList) {
                                deviatePlanHours(nonWorkedHoursDetailed, deviationPlanHours);
                                //т.к. внутри commitContext LinkedHashSet один и тот же елемент можно добавлять несколько раз
                                commitContext.addInstanceToCommit(nonWorkedHoursDetailed);
                            }

                            deviationPlanHours = deviatePlanHours(workedHoursDetailed, deviationPlanHours);

                            commitContext.addInstanceToCommit(workedHoursDetailed);
                        }
                    }

                } else if (!workedHoursDetailedList.isEmpty()) {

                    workedHoursDetailedList.sort((o1, o2) -> o2.getActualTimeIn().compareTo(o1.getActualTimeIn()));

                    List<WorkedHoursDetailed> tempList = new ArrayList<>();
                    WorkedHoursDetailed workedHoursDetailed = workedHoursDetailedList.stream()
                            .filter(whd -> WORK_HOURS.equals(whd.getScheduleElementType().getCode()))
                            .findFirst().orElse(null);

                    if (workedHoursDetailed != null) {

                        tempList.add(workedHoursDetailed);
                        tempList.addAll(workedHoursDetailedList.stream().
                                filter(whd -> whd.getActualTimeIn().equals(workedHoursDetailed.getActualTimeIn())
                                        && (NIGHT_HOURS.equals(whd.getScheduleElementType().getCode())
                                        || HOLIDAY_HOURS.equals(whd.getScheduleElementType().getCode())
                                        || WEEKEND_HOURS.equals(whd.getScheduleElementType().getCode())))
                                .collect(Collectors.toList()));

                        for (WorkedHoursDetailed hoursDetailed : tempList) {

                            if (deviation.getIsChangesFactHours()) {

                                deviateFactHours(hoursDetailed, deviationHours, null);
                                commitContext.addInstanceToCommit(hoursDetailed);

                            }

                            if (deviation.getIsChangesPlanHours()) {

                                deviatePlanHours(hoursDetailed, deviationHours);
                                commitContext.addInstanceToCommit(hoursDetailed);

                            }
                        }
                    }
                }

                summary.setHours(getDifferenceNoLessThanZero(summary.getHours(), deviation.getHours()));
                if (summary.getHours() == 0) {
                    summary.setScheduleElementType(dicScheduleElementTypeList.stream()
                            .filter(dst -> "WEEKEND".equals(dst.getCode()))
                            .findFirst().orElse(null));
                } else {
                    summary.setScheduleElementType(dicScheduleElementTypeList.stream()
                            .filter(dst -> WORK_HOURS.equals(dst.getCode()))
                            .findFirst().orElse(null));
                }
                if (deviation.getIsChangesFactHours() && !deviation.getIsChangesPlanHours() && deviation.getHours() > 0) { // если меняему факт часы И не меняем плановые час И часы отклонения положительное число
                    timecardHeader.setOvertimeHours(timecardHeader.getOvertimeHours() + deviation.getHours());
                }
                commitContext.addInstanceToCommit(summary);
            }

            dataManager.commit(commitContext);
            commitContext.getCommitInstances().clear();
        }

        headersForStatisticRenew.forEach(timecardHeader -> {
            if (timecardHeader != null) {
                calculateStatistic(timecardHeader);
            }
        });
    }

    protected Double deviatePlanHours(WorkedHoursDetailed workedHoursDetailed, Double deviationPlanHours) {
        Double planHoursAfterDeviation = getDifferenceNoLessThanZero(
                workedHoursDetailed.getPlanHours(),
                deviationPlanHours);
        if ("WORK_HOURS".equals(workedHoursDetailed.getScheduleElementType().getCode())) {
            deviationPlanHours = getDifferenceNoMoreThanZero(
                    workedHoursDetailed.getPlanHours(),
                    deviationPlanHours);
        }
        workedHoursDetailed.setPlanHours(planHoursAfterDeviation);
        return deviationPlanHours;
    }

    protected Double deviateFactHours(WorkedHoursDetailed workedHoursDetailed,
                                      Double deviationHours,
                                      Boolean isChangesDetailsFromBegin) {
        if (deviationHours == 0) return deviationHours;

        Double hoursAfterDeviation = getDifferenceNoLessThanZero(
                workedHoursDetailed.getHours(),
                deviationHours);

        if (deviationHours > 0) {
            Date newActualTimeOut = addMinutes(workedHoursDetailed.getActualTimeOut(), calculateMinutes(deviationHours));
            workedHoursDetailed.setActualTimeOut(newActualTimeOut); //двигаем верхний предел промежутка времени вперед
        } else {
            boolean isIntervalCollapsed = hoursAfterDeviation == 0;
            int subtractedMinutes = calculateMinutes(workedHoursDetailed.getHours() - hoursAfterDeviation);
            if (isChangesDetailsFromBegin) {
                Date newActualTimeIn = isIntervalCollapsed
                        ? workedHoursDetailed.getActualTimeOut()
                        : addMinutes(workedHoursDetailed.getActualTimeIn(), subtractedMinutes);
                workedHoursDetailed.setActualTimeIn(newActualTimeIn); //двигаем нижний предел промежутка времени вперед
            } else {
                Date newActualTimeOut = isIntervalCollapsed
                        ? workedHoursDetailed.getActualTimeIn()
                        : addMinutes(workedHoursDetailed.getActualTimeOut(), -subtractedMinutes);
                workedHoursDetailed.setActualTimeOut(newActualTimeOut); //двигаем верхний предел промежутка времени назад
            }
        }
        if ("WORK_HOURS".equals(workedHoursDetailed.getScheduleElementType().getCode())) {
            deviationHours = getDifferenceNoMoreThanZero(
                    workedHoursDetailed.getHours(),
                    deviationHours);
        }
        workedHoursDetailed.setHours(hoursAfterDeviation);
        return deviationHours;
    }


    protected Comparator<WorkedHoursDetailed> getWorkedHoursDetailedComparator(Boolean isChangesDetailsFromBegin) {
        Comparator<WorkedHoursDetailed> orderByActualTimeIn;
        if (isChangesDetailsFromBegin) {
            orderByActualTimeIn = Comparator.comparing(WorkedHoursDetailed::getActualTimeIn);
        } else {
            orderByActualTimeIn = (o1, o2) -> o2.getActualTimeIn().compareTo(o1.getActualTimeIn());
        }
        return orderByActualTimeIn;
    }


    protected Date addMinutes(Date date, int minutes) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MINUTE, minutes);
        return c.getTime();
    }

    protected Double getDifferenceNoMoreThanZero(Double hours, Double deviationHours) {
        Double difference = hours + deviationHours;
        return difference > 0 ? 0 : difference;
    }

    protected Double getDifferenceNoLessThanZero(Double hours, Double deviationHours) {
        Double difference = hours + deviationHours;
        return difference < 0 ? 0 : difference;
    }

    private int calculateMinutes(Double hours) {
        Double minutes = hours * 60;
        return minutes.intValue();
    }

    protected boolean isRelated(TimecardDeviation deviation, WorkedHoursSummary summary) {
        return summary.getTimecardHeader().getAssignmentGroup().getId().equals(deviation.getAssignmentGroup().getId())
                && !summary.getWorkedDate().after(deviation.getDateTo())
                && !summary.getWorkedDate().before(deviation.getDateFrom())
                && (deviation.getChangesWeekends()
                || "WORK_HOURS".equals(summary.getScheduleElementType().getCode()));
    }

    protected void changeSpecialDetails(TimecardDeviation deviation, WorkedHoursSummary whs) {
        whs = dataManager.reload(whs, "workedHoursSummary-with-type");
        List<WorkedHoursDetailed> specialDetailsWithHours = getSpecialDetailsWithHours(whs, deviation.getIsChangesDetailsFromBegin());

        for (WorkedHoursDetailed specialDetailsWithHour : specialDetailsWithHours) {
            changeDetailAndSummaryForDeviation(deviation, whs, specialDetailsWithHour);
        }
        whs.setHours(whs.getHours() + deviation.getHours());
        dataManager.commit(whs);
    }

    protected void changeDetailAndSummaryForDeviation(TimecardDeviation deviation, WorkedHoursSummary whs, WorkedHoursDetailed detail) {
        List<WorkedHoursDetailed> detailsWithHours;
        Date actualTimeIn = null;
        Date actualTimeOut = null;
        if (deviation.getIsChangesDetailsFromBegin()) {
            actualTimeIn = DateUtils.addMilliseconds(detail.getActualTimeIn(), (int) (-deviation.getHours() * 60 * 60 * 1000));
            Date nearestTimeIn = datesService.toNearestWholeMinute(actualTimeIn);
            detail.setActualTimeIn(nearestTimeIn);
        } else {
            actualTimeOut = DateUtils.addMilliseconds(detail.getActualTimeOut(), (int) (deviation.getHours() * 60 * 60 * 1000));
            Date nearestTimeOut = datesService.toNearestWholeMinute(actualTimeOut);
            detail.setActualTimeOut(nearestTimeOut);
        }
        if (deviation.getIsChangesPlanHours() && detail.getPlanHours() > 0d && !isWeekendOrHoliday(whs.getScheduleElementType())) {
            detail.setPlanHours(detail.getPlanHours() + deviation.getHours());
        }
//        if (deviation.getIsChangesPlanHours() && !deviation.getIsChangesFactHours()) {
//            dataManager.commit(detail);
//            commitContext.addInstanceToCommit(detail);
//        }
        actualTimeIn = actualTimeIn == null ? detail.getActualTimeIn() : actualTimeIn;
        actualTimeOut = actualTimeOut == null ? detail.getActualTimeOut() : actualTimeOut;
        if (deviation.getIsChangesFactHours()) {
//            double diff = datesService.calculateDifferenceInHours(detail.getActualTimeOut(), detail.getActualTimeIn());
            double diff = datesService.calculateDifferenceInHours(actualTimeOut, actualTimeIn);
//            double diff = deviation.getHours() + whs.getHours();

            if (diff > 0) {
                /* normal way */
                if (!isWeekendOrHoliday(whs.getScheduleElementType())) {
                    //that's because we have listener to detail and at weekend and holidays it will adds hours to whs
                    whs.setHours(whs.getHours() + deviation.getHours());
                }
                detail.setHours(diff);
                whs.setCorrectionFlag(true);
                dataManager.commit(whs);
                dataManager.commit(detail);
//                        commitContext.addInstanceToCommit(detail);
//                        commitContext.addInstanceToCommit(whs);
            } else if (diff == 0) {
                detail.setHours(0d);
                dataManager.commit(detail);
//                        commitContext.addInstanceToCommit(detail);
                if (!isWeekendOrHoliday(whs.getScheduleElementType())) {
                    //that's because we have listener to detail and at weekend and holidays it will adds hours to whs
                    List<WorkedHoursDetailed> thisSummaryDetails = getWorkedHoursDetailed(whs);
                    double newFactHours = thisSummaryDetails.stream().filter(e -> e.getScheduleElementType().getCode().equals("WORK_HOURS") && !e.equals(detail)).map(WorkedHoursDetailed::getHours).mapToDouble(e -> e).sum();
                    whs.setHours(newFactHours);
                    whs.setCorrectionFlag(true);
                    dataManager.commit(whs);
                }
//                        dataManager.commit(detail);
//                        commitContext.addInstanceToCommit(whs);
            }

            /* when minus hours bigger than one detail with hours*/
            else if (diff < 0) {
                whs.setHours(whs.getHours() - detail.getHours());
                detail.setHours(0d);
                if (deviation.getIsChangesPlanHours()) {
                    detail.setPlanHours(0d);
                }
                detail.setActualTimeOut(detail.getActualTimeIn());
                dataManager.commit(detail);
                WorkedHoursDetailed specialDetailForStartDate = getSpecialDetailForStartDate(detail);
                if (specialDetailForStartDate != null) {
                    specialDetailForStartDate.setActualTimeOut(detail.getActualTimeOut());
                    specialDetailForStartDate.setHours(detail.getHours());
                    if (deviation.getIsChangesPlanHours()) {
                        specialDetailForStartDate.setPlanHours(detail.getPlanHours());
                    }
                    dataManager.commit(specialDetailForStartDate);
                }
//                        commitContext.addInstanceToCommit(detail);
                detailsWithHours = getDetailsWithHours(whs, deviation.getIsChangesDetailsFromBegin());
                if (detailsWithHours.size() > 0) {
                    WorkedHoursDetailed nextDetail = detailsWithHours.get(0);
                    changeNextDetail(deviation, whs, detail, detailsWithHours, diff, nextDetail);
                    List<WorkedHoursDetailed> thisSummaryDetails = getWorkedHoursDetailed(whs);  //think about this
                    boolean thisSummaryHasWorkHours = thisSummaryDetails.stream().filter(e -> e.getScheduleElementType().getCode().equals("WORK_HOURS")).anyMatch(e -> e.getHours() > 0d);
                    if (!thisSummaryHasWorkHours) {
                        thisSummaryDetails.forEach(e -> {
                                    e.setActualTimeOut(e.getActualTimeIn());
                                    e.setHours(0d);
                                    if (deviation.getIsChangesPlanHours()) {
                                        e.setPlanHours(0d);
                                    }
                                    dataManager.commit(e);
//                                            commitContext.addInstanceToCommit(e);
                                }
                        );
                        whs.setHours(0d);
                    } else {
//                                double newFactHours = thisSummaryDetails.stream().filter(e -> e.getScheduleElementType().getCode().equals("WORK_HOURS")).map(WorkedHoursDetailed::getHours).mapToDouble(e -> e).sum();
//                                whs.setHours(newFactHours);
                        if (detailsWithHours.size() >= 2) {//The kostyl'
                            nextDetail = detailsWithHours.get(1);
                            changeNextDetail(deviation, whs, detail, detailsWithHours, diff, nextDetail); //The kostyl'
                        }
                    }
                    whs.setCorrectionFlag(true);
                    dataManager.commit(whs);
//                  changeSpecialDetails(deviation, whs);
//                  commitContext.addInstanceToCommit(whs);
                }

            }

        } else {
            dataManager.commit(detail);
        }
    }

    private void changeNextDetail(TimecardDeviation deviation, WorkedHoursSummary whs, WorkedHoursDetailed detail, List<WorkedHoursDetailed> detailsWithHours, double diff, WorkedHoursDetailed nextDetail) {
        Date actualTimeOut;
        if (nextDetail != null && nextDetail.getScheduleElementType().getCode().equals("WORK_HOURS")) {
            whs.setHours(whs.getHours() - nextDetail.getHours());
            nextDetail.setHours(nextDetail.getHours() + diff);
            if (deviation.getIsChangesPlanHours()) {
                nextDetail.setPlanHours(nextDetail.getHours() < 0 ? 0 : nextDetail.getHours());
            }
            whs.setHours(whs.getHours() + nextDetail.getHours());

            actualTimeOut = DateUtils.addMilliseconds(nextDetail.getActualTimeOut(), (int) diff * 60 * 60 * 1000);
            actualTimeOut = datesService.toNearestWholeMinute(actualTimeOut);
            nextDetail.setActualTimeOut(actualTimeOut);
        } else {
            if (detailsWithHours.size() > 1) {
                nextDetail = detailsWithHours.get(1);
                if (nextDetail != null && nextDetail.getScheduleElementType().getCode().equals("WORK_HOURS")) {
                    nextDetail.setHours(nextDetail.getHours() + diff);
                    if (deviation.getIsChangesPlanHours()) {
                        nextDetail.setPlanHours(nextDetail.getHours());
                    }
                    whs.setHours(whs.getHours() - nextDetail.getHours());
                }
            }
        }
        if (nextDetail != null) {
            if (nextDetail.getHours() <= 0) {
                nextDetail.setActualTimeOut(detail.getActualTimeIn());
                nextDetail.setHours(0d);
                if (deviation.getIsChangesPlanHours()) {
                    nextDetail.setPlanHours(0d);
                }
                whs.setHours(whs.getHours() - nextDetail.getHours());
            }
            dataManager.commit(nextDetail);
//                                commitContext.addInstanceToCommit(nextDetail);

        }
        if (whs.getHours() < 0) {
            whs.setHours(0d);
        }
    }

    public boolean isWeekendOrHoliday(DicScheduleElementType scheduleElementType) {
        return scheduleElementType.getCode().equalsIgnoreCase("WEEKEND")
                || scheduleElementType.getCode().equalsIgnoreCase("HOLIDAY")
                || scheduleElementType.getCode().equalsIgnoreCase("WEEKEND_HOURS")
                || scheduleElementType.getCode().equalsIgnoreCase("HOLIDAY_HOURS")
                || scheduleElementType.getCode().equalsIgnoreCase("NIGHT_HOURS");
    }

    private List<WorkedHoursSummary> reloadSummaries(List<WorkedHoursSummary> workedHoursSummaries) {  //перегружает ВАС с вьюшкой workedHoursSummary-with-type
        return commonService.getEntities(WorkedHoursSummary.class, "select e from tsadv$WorkedHoursSummary e where e.id in :workedHoursSummaries",
                ParamsMap.of("workedHoursSummaries", workedHoursSummaries), "workedHoursSummary-with-type");
    }

    protected List<WorkedHoursDetailed> getDetailsWithHours(WorkedHoursSummary whs, boolean changeHoursFromBegin) {
        List<WorkedHoursDetailed> workedHoursDetails = getWorkedHoursDetailed(whs);
        workedHoursDetails = workedHoursDetails.stream().filter(e ->
                e.getScheduleElementType().getCode().equals("WORK_HOURS")
                        && e.getHours() > 0d)
                .collect(Collectors.toList());
        workedHoursDetails.sort((o1, o2) -> {
            if (changeHoursFromBegin) {
                return o1.getActualTimeOut().compareTo(o2.getActualTimeOut()); //asc
            } else {
                return o2.getActualTimeOut().compareTo(o1.getActualTimeOut()); //desc
            }
        });
        return workedHoursDetails;
    }

    protected List<WorkedHoursDetailed> getSpecialDetailsWithHours(WorkedHoursSummary whs, boolean changeHoursFromBegin) {
        List<WorkedHoursDetailed> workedHoursDetails = getWorkedHoursDetailed(whs);
        workedHoursDetails = workedHoursDetails.stream().filter(e -> isWeekendOrHoliday(e.getScheduleElementType()) && e.getHours() > 0d).collect(Collectors.toList());
        workedHoursDetails.sort((o1, o2) -> {
            if (changeHoursFromBegin) {
                return o1.getActualTimeOut().compareTo(o2.getActualTimeOut());
            } else {
                return o2.getActualTimeOut().compareTo(o1.getActualTimeOut());
            }
        });
        return workedHoursDetails;
    }

    protected List<WorkedHoursDetailed> getAllDetails(WorkedHoursSummary whs, boolean changeHoursFromBegin) {
        List<WorkedHoursDetailed> workedHoursDetails = getWorkedHoursDetailed(whs);
        workedHoursDetails.sort((o1, o2) -> {
            if (changeHoursFromBegin) {
                return o1.getActualTimeOut().compareTo(o2.getActualTimeOut());
            } else {
                return o2.getActualTimeOut().compareTo(o1.getActualTimeOut());
            }
        });
        return workedHoursDetails;
    }

    protected TimecardDeviation createDeviation(AssignmentGroupExt assignmentGroupExt, double hours, Date dateFrom, Date dateTo, boolean fact, boolean plan, boolean changeHoursFromBegin, Boolean changesWeekends) {
        TimecardDeviation deviation = metadata.create(TimecardDeviation.class);
        deviation.setAssignmentGroup(assignmentGroupExt);
        deviation.setHours(hours);
        deviation.setDateFrom(dateFrom);
        deviation.setDateTo(dateTo);
        deviation.setIsChangesFactHours(fact);
        deviation.setIsChangesPlanHours(plan);
        deviation.setCreateTs(new Date());
        deviation.setIsChangesDetailsFromBegin(changeHoursFromBegin);
        deviation.setChangesWeekends(changesWeekends);
        return deviation;
    }

    protected void addAbsenceStatistics(Absence absence, TimecardHeader timecardHeader) {
        String absenceCode = absence.getType().getCode();
        if (absenceCode.equals("ANNUAL")) {
            timecardHeader.setAnnualVacationDays(timecardHeader.getAnnualVacationDays() + 1);
        }
        if (absenceCode.equals("ANNUAL_EXTENTION")) {
            timecardHeader.setAnnualVacationDays(timecardHeader.getAnnualVacationDays() + 1);
        }
        if (absenceCode.equals("TRANSFER")) {
            timecardHeader.setAnnualVacationDays(timecardHeader.getAnnualVacationDays() + 1);
        }
        if (absence.getType().getAbsenceCategory() != null) {
            if (absence.getType().getAbsenceCategory().getCode().equalsIgnoreCase("UNPAID")) {
                timecardHeader.setUnpaidVacationDays(timecardHeader.getUnpaidVacationDays() + 1);
            }

            if (absence.getType().getAbsenceCategory().getCode().equalsIgnoreCase("SICKNESS")) {
                timecardHeader.setSickDays(timecardHeader.getSickDays() + 1);
            }
        }
        fillAbsenceTypeAttributes(timecardHeader, absenceCode);
        /*  all absences without absences that in weekends and holidays */
        if (!absenceCode.equals("WEEKEND") &&
                !absenceCode.equals("HOLIDAY")) {
            timecardHeader.setAbsenceDays(timecardHeader.getAbsenceDays() + 1);
        }
    }

    protected void fillAbsenceTypeAttributes(TimecardHeader timecardHeader, String absenceCode) {
        List<String> hookyCodes = new ArrayList<>(Arrays.asList("HOOKY", "Forced absence"));
        List<String> innerTrainingCodes = new ArrayList<>(Arrays.asList("EDUC", "PAIDEDUC"));
        List<String> otherTypesCodes = new ArrayList<>(Arrays.asList("unpaiddowntime", "MCCA", "MCCA_PROF", "Suspension", "EXEMPTION"));
        if (hookyCodes.contains(absenceCode)) {
            Integer hookyDays;
            String attribute1 = timecardHeader.getAttribute1();
            if (attribute1 == null) {
                hookyDays = 0;
            } else {
                hookyDays = Integer.valueOf(attribute1);
            }
            hookyDays = hookyDays + 1;
            timecardHeader.setAttribute1(String.valueOf(hookyDays));
        }
        if (innerTrainingCodes.contains(absenceCode)) {
            Integer innerTrainingDays;
            String attribute3 = timecardHeader.getAttribute3();
            if (attribute3 == null) {
                innerTrainingDays = 0;
            } else {
                innerTrainingDays = Integer.valueOf(attribute3);
            }
            innerTrainingDays = innerTrainingDays + 1;
            timecardHeader.setAttribute3(String.valueOf(innerTrainingDays));
        }

        if (otherTypesCodes.contains(absenceCode)) {
            Integer otherTypesCodesDays;
            String attribute4 = timecardHeader.getAttribute4();
            if (attribute4 == null) {
                otherTypesCodesDays = 0;
            } else {
                otherTypesCodesDays = Integer.valueOf(attribute4);
            }
            otherTypesCodesDays = otherTypesCodesDays + 1;
            timecardHeader.setAttribute4(String.valueOf(otherTypesCodesDays));
        }
    }

    protected void prepareZeroHoursHeader(TimecardHeader timecardHeader) {
        timecardHeader.setFactWorkDays(0);
        timecardHeader.setFactHoursWithoutOvertime(0d);
        timecardHeader.setWeekendHours(0d);
        timecardHeader.setWeekendHours(0d);
        timecardHeader.setWeekendDays(0);
        timecardHeader.setNightHours(0d);
        timecardHeader.setHolidayHours(0d);
        if (timecardHeader.getHolidayDays() == null) {
            timecardHeader.setHolidayDays(0);
        }
        timecardHeader.setAnnualVacationDays(0);
        timecardHeader.setUnpaidVacationDays(0);
        timecardHeader.setMaternityVacationDays(0);
        timecardHeader.setChildcareVacationDays(0);
        timecardHeader.setSickDays(0);
        timecardHeader.setAbsenceDays(0);
        timecardHeader.setTotalFreeDays(0);
        timecardHeader.setBusTripDays(0);
        timecardHeader.setTotalWorkedDays(0);
        if (timecardHeader.getOvertimeHours() == null) {
            timecardHeader.setOvertimeHours(0d);
        }
        timecardHeader.setTotalAbsence(0);
        timecardHeader.setGrandTotalDays(0);
    }

    protected WorkedHoursSummary getWorkedHoursSummary(Date date, AssignmentGroupExt assignmentGroup) {
        LoadContext<WorkedHoursSummary> loadContext = LoadContext.create(WorkedHoursSummary.class);
        loadContext.setQuery(LoadContext.createQuery(" select e from tsadv$WorkedHoursSummary e" +
                " join e.timecardHeader th " +
                " where th.assignmentGroup.id = :assignmentGroupId" +
                "     and e.workedDate = :date")
                .setParameter("assignmentGroupId", assignmentGroup.getId())
                .setParameter("date", date))
                .setView("workedHoursSummary-view");
        return dataManager.load(loadContext);
    }

    protected List<WorkedHoursSummary> getWorkedHoursSummariesForPeriod(List<UUID> assignmentGroupIds, Date startDate, Date endDate) {
        LoadContext<WorkedHoursSummary> loadContext = LoadContext.create(WorkedHoursSummary.class);
        StringBuilder sb = new StringBuilder("");
        for (Object uuid : assignmentGroupIds) {
            sb.append("'").append(uuid).append("',");
        }
        String ids = sb.toString().substring(0, sb.toString().length() - 1);
        ids = "(" + ids + ")";

        loadContext.setQuery(LoadContext.createQuery(
                " select e from tsadv$WorkedHoursSummary e" +
                        "          where e.timecardHeader.assignmentGroup.id in " + ids +
                        "              and e.workedDate >= :startDate" +
                        "              and e.workedDate <= :endDate")
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate))
                .setView("workedHoursSummary-for-timecard");
        return dataManager.loadList(loadContext);
    }

    protected Map<UUID, PersonGroupExt> getPersonGroupsForAssignmentGroup(List<UUID> assignmentGroupIds) {
        HashMap<UUID, PersonGroupExt> map = new HashMap<>();
        String str = getIdsAsString(assignmentGroupIds);

        String queryString = "SELECT distinct a.person_group_id,\n" +
                " a.group_id\n" +
                "FROM base_assignment a\n" +
                "   WHERE a.group_id in " + str;

        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createNativeQuery(queryString);
            query.setParameter(1, CommonUtils.getSystemDate());

            List<Object[]> rows = query.getResultList();

            for (Object[] row : rows) {

                PersonGroupExt personGroupExt = metadata.create(PersonGroupExt.class);
                personGroupExt.setId((UUID) row[0]);


                map.put((UUID) row[1], personGroupExt);
            }
            return map;
        }

    }

    protected String getIdsAsString(List<UUID> uuids) {
        if (uuids == null || uuids.isEmpty()) {
            return null;
        }
        StringBuilder ids = new StringBuilder();
        List<String> collect = uuids.stream().map(s -> "'" + s + "', ").collect(Collectors.toList());
        for (String s : collect) {
            ids.append(s);
        }
        String str = ids.toString();
        str = str.substring(0, str.length() - 2);
        str = "(" + str + ")";
        return str;
    }

    protected Absence getAbsenceWhereParentIs(Absence absence) {
        Absence changeAbsence = null;
        LoadContext<Absence> loadContext = LoadContext.create(Absence.class);
        loadContext.setQuery(LoadContext.createQuery(" select e from tsadv$Absence e " +
                "     where e.parentAbsence.id = :absenceId")
                .setParameter("absenceId", absence.getId()))
                .setView("absence.view");
        List<Absence> absences = dataManager.loadList(loadContext);
        if (!absences.isEmpty()) {
            changeAbsence = absences.get(0);
        }
        return changeAbsence;
    }

    protected BusinessTrip getBusinessTripWhereParentIs(BusinessTrip businessTrip) {
        BusinessTrip changeTrip = null;
        LoadContext<BusinessTrip> loadContext = LoadContext.create(BusinessTrip.class);
        loadContext.setQuery(LoadContext.createQuery(" select e from tsadv$BusinessTrip e " +
                "     where e.parentBusinessTrip.id = :businessTripId")
                .setParameter("businessTripId", businessTrip.getId()))
                .setView("businessTrip-view");
        List<BusinessTrip> businessTrips = dataManager.loadList(loadContext);
        if (!businessTrips.isEmpty()) {
            changeTrip = businessTrips.get(0);
        }
        return changeTrip;
    }

    protected List<Absence> getAbsences(Collection<PersonGroupExt> personGroups, Date startDate, Date endDate) {
        String str = getIdsAsString(personGroups.stream().map(PersonGroupExt::getId).collect(Collectors.toList()));

        LoadContext<Absence> loadContext = LoadContext.create(Absence.class);
        loadContext.setQuery(LoadContext.createQuery(" select e from tsadv$Absence e " +
                " where e.personGroup.id in " + str +
                "     and ((e.dateFrom <= :endDate and e.dateFrom >= :startDate) " +
                "     or (e.dateTo <= :endDate and e.dateTo >= :startDate) " +
                "     or (e.dateFrom >= :startDate and e.dateTo <= :endDate) " +
                "     or (e.dateFrom <= :endDate and e.dateTo >= :endDate))")
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate))
                .setView("absence.view");
        return new ArrayList<>(dataManager.loadList(loadContext));
    }

    /* overriden in aa */
    protected List<BusinessTrip> getBusinessTrips(Collection<PersonGroupExt> personGroups, Date startDate, Date endDate) {
        String str = getIdsAsString(personGroups.stream().map(PersonGroupExt::getId).collect(Collectors.toList()));

        LoadContext<BusinessTrip> loadContext = LoadContext.create(BusinessTrip.class);
        loadContext.setQuery(LoadContext.createQuery(" select e from tsadv$BusinessTrip e " +
                " where e.personGroup.id in " + str +
                "     and ((e.dateFrom <= :endDate and e.dateFrom >= :startDate) " +
                "     or (e.dateTo <= :endDate and e.dateTo >= :startDate) " +
                "     or (e.dateFrom >= :startDate and e.dateTo <= :endDate) " +
                "     or (e.dateFrom <= :endDate and e.dateTo >= :endDate))")
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate))
                .setView("businessTrip-view");
        return new ArrayList<>(dataManager.loadList(loadContext));
    }

    protected WorkedHoursSummary createWorkedHoursSummary(TimecardHeader timecardHeader,
                                                          ScheduleSummary scheduleSummary,
                                                          List<Absence> absences,
                                                          List<BusinessTrip> businessTrips,
                                                          Date date,
                                                          boolean fillElementType) {
        WorkedHoursSummary workedHoursSummary = null;
        DicScheduleElementType scheduleElementType = scheduleSummary.getElementType();
        List<AbsenceToWorkedHoursSummary> absenceToWorkedHoursSummaries = new ArrayList<>();
        if (scheduleSummary != null) {
            workedHoursSummary = metadata.create(WorkedHoursSummary.class);
            workedHoursSummary.setTimecardHeader(timecardHeader);
            workedHoursSummary.setWorkedDate(date);
            workedHoursSummary.setShift(scheduleSummary.getShift());
            workedHoursSummary.setCreateTs(new Date());
            workedHoursSummary.setScheduleElementType(scheduleElementType);

            String displayValue = fillElementType ? scheduleSummary.getDisplayValue() : " ";
            workedHoursSummary.setDisplayValue(displayValue);

            String scheduleElementTypeCode = scheduleElementType.getCode();
            boolean isNeedToSetHoursFromScheduleSummary = "WORK_HOURS".equals(scheduleElementTypeCode)
                    && scheduleSummary.getHours() != null
                    && fillElementType;
            Double hours = isNeedToSetHoursFromScheduleSummary ? scheduleSummary.getHours() : 0d;
            workedHoursSummary.setHours(hours);

            if (absences != null && !absences.isEmpty()) {
                List<Absence> recallAndCancelAbsences = absences.stream()
                        .filter(a -> a.getType().getCode().equals("RECALL") || a.getType().getCode().equals("CANCEL"))
                        .collect(Collectors.toList());
                List<Absence> resultAbsences = new ArrayList<>(absences);
                for (Absence recallOrCancelAbsence : recallAndCancelAbsences) {
                    for (Absence absence : absences) {
                        if (recallOrCancelAbsence.getParentAbsence() != null
                                && recallOrCancelAbsence.getParentAbsence().equals(absence)) {
                            resultAbsences.remove(absence);
                            break;
                        }
                    }
                    resultAbsences.remove(recallOrCancelAbsence);

                }
                List<Absence> parentAbsences = resultAbsences.stream()
                        .filter(absence -> absence.getParentAbsence() != null)
                        .map(Absence::getParentAbsence)
                        .collect(Collectors.toList());
                for (Absence absence : resultAbsences) {
                    boolean itIsDayOffAndNoMark = scheduleElementTypeCode.equals("WEEKEND")
                            && absence.getType().getIsOnlyWorkingDay() != null
                            && !absence.getType().getIsOnlyWorkingDay();
                    boolean itIsHolidayAndNoMark = scheduleElementTypeCode.equals("HOLIDAY") &&
                            absence.getType().getIsOnlyWorkingDay() != null &&
                            !absence.getType().getIsOnlyWorkingDay();
                    if (itIsHolidayAndNoMark || itIsDayOffAndNoMark) {
                        workedHoursSummary.setAbsence(absence);
                    }
                    boolean isNeedToSetAbsence = itIsHolidayAndNoMark
                            || itIsDayOffAndNoMark
                            || (!scheduleElementTypeCode.equals("WEEKEND")
                            && !scheduleElementTypeCode.equals("HOLIDAY"));
                    /* if other type of absence */
                    String absenceCode = absence.getType().getCode();
                    if (workedHoursSummary.getAbsence() == null
                            && isNeedToSetAbsence
                            && !parentAbsences.contains(absence)
                            && !"RECALL".equals(absenceCode)
                            && !"CANCEL".equals(absenceCode)) {
                        workedHoursSummary.setAbsence(absence);
                        if (!absence.getType().getIsWorkingDay()) {
                            workedHoursSummary.setHours(0d);
                            workedHoursSummary.setShift(null); //not sure..
                        }
                        if ("ANNUAL_EXTENTION".equals(absenceCode)) {
                            workedHoursSummary.setAbsence(absence.getParentAbsence());
                            absence = absence.getParentAbsence();
                        }
                    }

                    Absence transferAbsence = getAbsenceWhereParentIs(absence);
                    boolean createAbsenceToSummary = true;
                    if (transferAbsence != null) {
                        String changeAbsenceCode = transferAbsence.getType().getCode();
                        switch (changeAbsenceCode) {
                            case "CANCEL":
                                if (workedHoursSummary.getAbsence() != null && absence != null && workedHoursSummary.getAbsence().getId().equals(absence.getId())) { // really another absence
                                    workedHoursSummary.setAbsence(null);
                                    workedHoursSummary.setShift(scheduleSummary.getShift());
                                    workedHoursSummary.setHours(scheduleSummary.getHours());
                                } else {
                                    createAbsenceToSummary = false;
                                }
                                absence = null;
                                break;
                            case "TRANSFER":
                                if (absenceIsTransferedOnDay(absence, transferAbsence, date)) {
                                    createAbsenceToSummary = false;
                                    absence = null;
                                    workedHoursSummary.setAbsence(null);
                                    workedHoursSummary.setShift(scheduleSummary.getShift());
                                    workedHoursSummary.setHours(scheduleSummary.getHours());
                                }
                                break;
                            case "RECALL":
                                //day is in recall dates
                                if (!date.after(transferAbsence.getDateTo()) && !date.before(transferAbsence.getDateFrom())) {
                                    absence = null;
                                    workedHoursSummary.setAbsence(null);
                                    workedHoursSummary.setShift(scheduleSummary.getShift());
                                    workedHoursSummary.setHours(scheduleSummary.getHours());
                                }
                                break;
                        }
                    }

                    if (absence != null && absence.getType().getCode().equals("RECALL")
                            && absence.getType().getCode().equals("CANCEL")
                            && workedHoursSummary.getAbsence() != null) {
                        absence = null;
//                        workedHoursSummary.setShift(scheduleSummary.getShift());
                        workedHoursSummary.setHours(scheduleSummary.getHours());
                    }
                    if (workedHoursSummary.getAbsence() == null && absence != null
                            && isNeedToSetAbsence
                            && !absence.getType().getCode().equals("RECALL")
                            && !absence.getType().getCode().equals("CANCEL")) {
                        workedHoursSummary.setAbsence(absence);
                        workedHoursSummary.setShift(scheduleSummary.getShift());
                        workedHoursSummary.setHours(scheduleSummary.getHours());
                    }
                    if (workedHoursSummary.getAbsence() != null && absence != null
                            && isNeedToSetAbsence
                            && absence.getType().getCode().equals("TRANSFER")) {
                        workedHoursSummary.setAbsence(absence.getParentAbsence());
                        absence = absence.getParentAbsence();
                    }
                    if (absence != null && timecardConfig.getMultipleAbsencesToOneDay()
                            && isNeedToSetAbsence
                            && !absence.getType().getCode().equals("RECALL")
                            && !absence.getType().getCode().equals("CANCEL")
                            && createAbsenceToSummary) {
                        AbsenceToWorkedHoursSummary absenceToWorkedHoursSummary = metadata.create(AbsenceToWorkedHoursSummary.class);
                        absenceToWorkedHoursSummary.setWorkedHoursSummary(workedHoursSummary);
                        absenceToWorkedHoursSummary.setAbsence(absence);
                        absenceToWorkedHoursSummaries.add(absenceToWorkedHoursSummary);
                    }
                }
            }
            workedHoursSummary.setAbsenceToWorkedHoursSummaryList(absenceToWorkedHoursSummaries);
        }
        if (workedHoursSummary == null) {
            System.out.println("scheduleSummary is null for timecardHeader with id " + timecardHeader);
        }
        if (businessTrips != null && workedHoursSummary != null) {
            for (BusinessTrip businessTrip : businessTrips) {
                if (businessTrip != null) {
                    businessTrip = processBusinessTrip(date, businessTrip);
                }
                if (businessTrip != null && workedHoursSummary.getBussinessTrip() == null) {
                    workedHoursSummary.setBussinessTrip(businessTrip);
                }

                if (scheduleSummary.getHours() != null) {

                    AssignmentGroupExt assignmentGroup = timecardHeader.getAssignmentGroup();
                    //getting actual schedule/scheduleSums for person because in month those can be different
                    AssignmentSchedule actualAssignmentScheduleForDay = getActualAssignmentScheduleForDay(assignmentGroup, date);

                    if (actualAssignmentScheduleForDay != null) {
                        StandardOffset offset = actualAssignmentScheduleForDay.getOffset();
                        Date monthBegin = datesService.getMonthBegin(date);
                        ScheduleHeader scheduleHeader = getScheduleHeader(offset, monthBegin);
                        if (scheduleHeader != null) {
                            Map<Integer, ScheduleSummary> summariesMap = scheduleHeader.getSummariesMap();
                            int day = datesService.getDayOfMonth(date);
                            if (summariesMap.containsKey(day)) {
                                scheduleSummary = summariesMap.get(day);
                            }
                        }
                    }
                    workedHoursSummary.setHours(scheduleSummary.getHours());
                }
            }

        }
        return workedHoursSummary;
    }

    private boolean absenceIsTransferedOnDay(Absence absence, Absence transferAbsence, Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(absence.getDateTo());
        int l = (int) (transferAbsence.getDateTo().getTime() - transferAbsence.getDateFrom().getTime());
        c.add(Calendar.MILLISECOND, -l);
        if (!date.before(c.getTime())) {
            return true;
        }
        return false;
    }

    protected BusinessTrip processBusinessTrip(Date date, BusinessTrip businessTrip) {
        BusinessTripOrderStatus status = businessTrip.getStatus();
        if (status != null && status.equals(BusinessTripOrderStatus.CANCELED)) {
            return null;
        }
        BusinessTrip nextTrip = getBusinessTripWhereParentIs(businessTrip);
        if (status != null && status.equals(BusinessTripOrderStatus.APPROVED)) {
            if (businessTrip.getTypeTrip() != null && businessTrip.getTypeTrip().equals(BusinessTripOrderType.RECALL)) {
                return null;
            }
        }
        if (nextTrip != null && nextTrip.getTypeTrip() != null) {
            BusinessTripOrderType typeTrip = nextTrip.getTypeTrip();
            if (typeTrip.equals(BusinessTripOrderType.TRANSFER)) {
                if (!date.before(nextTrip.getDateFrom()) && !date.after(nextTrip.getDateTo())) {
                    businessTrip = nextTrip;
                } else {
                    businessTrip = null;
                }
            }
            if (typeTrip.equals(BusinessTripOrderType.RECALL)) {
                //day is in recall dates
                if (!date.after(nextTrip.getDateTo()) && !date.before(nextTrip.getDateFrom())) {
                    businessTrip = null;
                }

                if (nextTrip.getStatus().equals(BusinessTripOrderStatus.CHANGED)) {
                    businessTrip = null;
                }
            }
            if (typeTrip.equals(BusinessTripOrderType.EXTENDED)) {
                if (businessTrip != null && businessTrip.getTypeTrip() != null && !businessTrip.getTypeTrip().equals(BusinessTripOrderType.EXTENDED))
                    businessTrip = null;
            }
        }
        return businessTrip;
    }

    protected AssignmentSchedule getActualAssignmentScheduleForDay(AssignmentGroupExt assignmentGroup, Date date) {
        return commonService.getEntity(AssignmentSchedule.class,
                "select e from tsadv$AssignmentSchedule e where e.assignmentGroup.id = :assignmentGroupId " +
                        "  and e.startDate <= :date and e.endDate >= :date",
                ParamsMap.of("assignmentGroupId", assignmentGroup.getId(),
                        "date", date),
                "assignmentSchedule.view");
    }

    protected List<WorkedHoursDetailed> createWorkedHoursDetailedList(List<ScheduleDetail> scheduleDetails,
                                                                      WorkedHoursSummary workedHoursSummary,
                                                                      boolean createFactHours) {
        List<WorkedHoursDetailed> list = new ArrayList<>();
        for (ScheduleDetail scheduleDetail : scheduleDetails) {
            WorkedHoursDetailed workedHoursDetailed = metadata.create(WorkedHoursDetailed.class);
            workedHoursDetailed.setTimeIn(scheduleDetail.getTimeIn());
            workedHoursDetailed.setActualTimeIn(scheduleDetail.getTimeIn());
            workedHoursDetailed.setActualTimeOut(scheduleDetail.getTimeOut());
            workedHoursDetailed.setActualTimeOut(scheduleDetail.getTimeOut());
            if (createFactHours) {
                workedHoursDetailed.setHours(scheduleDetail.getHours());
            } else {
                workedHoursDetailed.setHours(0d);
            }
            if (!scheduleDetail.getElementType().getCode().equals("BREAK")) {
                workedHoursDetailed.setPlanHours(scheduleDetail.getHours());
            }
            workedHoursDetailed.setWorkedHoursSummary(workedHoursSummary);
            workedHoursDetailed.setTimecardHeader(workedHoursSummary.getTimecardHeader());
            workedHoursDetailed.setScheduleElementType(scheduleDetail.getElementType());
            workedHoursDetailed.setWorkedDate(scheduleDetail.getDayDate());
            workedHoursDetailed.setIsNeedToCheckAndCreateAdditionalHours(false);
            list.add(workedHoursDetailed);
        }
        return list;
    }

    protected DicScheduleElementType getElementType(String code) {
        return commonService.getEntity(DicScheduleElementType.class, code);
    }

    protected List<NightPartDTO> getNightParts(WorkedHoursDetailed workDetail, DicScheduleElementType nightHoursElement) {
        List<NightPartDTO> nightPartsMap = new ArrayList<>();

        Date nightHoursFrom = getDateTime(workDetail.getActualTimeIn(), nightHoursElement.getTimeFrom());
        Date nightHoursTo = getDateTime(workDetail.getActualTimeIn(), nightHoursElement.getTimeTo());

        /* 1st - we check when start is day before */
        nightHoursFrom = DateUtils.addDays(nightHoursFrom, -1);
        if (intersectPeriods(nightHoursFrom, nightHoursTo, workDetail.getActualTimeIn(), workDetail.getActualTimeOut())) {
            nightPartsMap.add(new NightPartDTO(workDetail, null, nightHoursFrom, nightHoursTo));
        }

        /* 2nd - we check - like as it, but incrementing 2nd day if the same */

        nightHoursFrom = getDateTime(workDetail.getActualTimeIn(), nightHoursElement.getTimeFrom());
        nightHoursTo = getDateTime(workDetail.getActualTimeOut(), nightHoursElement.getTimeTo());

        if (!nightHoursTo.after(nightHoursFrom)) {
            nightHoursTo = DateUtils.addDays(nightHoursTo, 1);
        }
        if (intersectPeriods(nightHoursFrom, nightHoursTo, workDetail.getActualTimeIn(), workDetail.getActualTimeOut())) {
            nightPartsMap.add(new NightPartDTO(workDetail, null, nightHoursFrom, nightHoursTo));
        }
        /* 3rd - we incrementing start date for 1 */
        /* but we check that not same day */
        if (!DateUtils.isSameDay(workDetail.getActualTimeIn(), workDetail.getActualTimeOut())) {
            nightHoursFrom = getDateTime(workDetail.getActualTimeOut(), nightHoursElement.getTimeFrom());
            nightHoursTo = getDateTime(workDetail.getActualTimeOut(), nightHoursElement.getTimeTo());
            nightHoursTo = DateUtils.addDays(nightHoursTo, 1);
            if (!nightHoursTo.after(nightHoursFrom)) {
                nightHoursTo = DateUtils.addDays(nightHoursTo, 1);
            }
            if (intersectPeriods(nightHoursFrom, nightHoursTo, workDetail.getActualTimeIn(), workDetail.getActualTimeOut())) {
                nightPartsMap.add(new NightPartDTO(workDetail, null, nightHoursFrom, nightHoursTo));
            }
        }
        return nightPartsMap;
    }

    protected boolean containsHolidayOrWeekendHours(WorkedHoursDetailed workDetail, String code) {
        WorkedHoursSummary workedHoursSummary = workDetail.getWorkedHoursSummary();
        DicScheduleElementType summaryElementType = workedHoursSummary.getScheduleElementType();
        if (summaryElementType != null && summaryElementType.getCode().equals(code)) {
            Date startDate = getDateTime(workDetail.getWorkedDate(), workedHoursSummary.getWorkedDate());
            Date endDate = DateUtils.addDays(startDate, 1);

            return intersectPeriods(startDate, endDate, workDetail.getActualTimeIn(), workDetail.getActualTimeOut());
        }
        return false;
    }

    /* get TRUE if periods "dateFrom1-dateTo1" and "dateFrom2-dateTo2' intersects */
    protected static boolean intersectPeriods(Date dateFrom1, Date dateTo1, Date dateFrom2, Date dateTo2) {
        return !dateFrom1.before(dateFrom2) && !dateFrom1.after(dateTo2)
                || !dateTo1.before(dateFrom2) && !dateTo1.after(dateTo2)
                || !dateFrom2.before(dateFrom1) && !dateFrom2.after(dateTo1)
                || !dateTo2.before(dateFrom1) && !dateTo2.after(dateTo1);
    }

    /* set time to date */
    protected static Date getDateTime(Date date, Date time) {
        SimpleDateFormat hoursFormat = new SimpleDateFormat("HH");
        SimpleDateFormat minutesFormat = new SimpleDateFormat("mm");

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hoursFormat.format(time)));
        c.set(Calendar.MINUTE, Integer.parseInt(minutesFormat.format(time)));

        return c.getTime();
    }

    protected WorkedHoursDetailed createNightDetail(NightPartDTO nightPartDTO) {
        WorkedHoursDetailed nightDetail = metadata.create(WorkedHoursDetailed.class);

        Date nightHoursFrom = nightPartDTO.getNightHoursFrom();
        Date nightHoursTo = nightPartDTO.getNightHoursTo();

        Date firstDate = getFirstDate(nightHoursFrom, nightPartDTO.getWorkedHoursDetailed().getActualTimeIn());
        Date timeInLimit = nightPartDTO.getWorkedHoursDetailed().getActualTimeIn();
        if (!firstDate.after(nightHoursFrom) && !nightHoursFrom.before(timeInLimit)) { //second check - for not out from night hours start time
            nightDetail.setActualTimeIn(nightHoursFrom);
        } else {
            nightDetail.setActualTimeIn(timeInLimit);
        }

        Date secondDate = getSecondDate(nightHoursTo, nightPartDTO.getWorkedHoursDetailed().getActualTimeOut());
        Date timeOutLimit = nightPartDTO.getWorkedHoursDetailed().getActualTimeOut();

        if (timeOutLimit.after(nightHoursTo)) {
            timeOutLimit = nightHoursTo;
        }
        if (!secondDate.after(nightHoursTo) && !secondDate.after(timeOutLimit)) { /* second check - for not out from night hours end time */
            nightDetail.setActualTimeOut(secondDate);
        } else {
            nightDetail.setActualTimeOut(timeOutLimit);
        }

        nightDetail.setScheduleElementType(getElementType("NIGHT_HOURS"));
        nightDetail.setWorkedDate(nightPartDTO.getWorkedHoursDetailed().getWorkedDate());
        nightDetail.setWorkedHoursSummary(nightPartDTO.getWorkedHoursDetailed().getWorkedHoursSummary());
        nightDetail.setTimecardHeader(nightPartDTO.getWorkedHoursDetailed().getWorkedHoursSummary().getTimecardHeader());
        nightDetail.setHours(datesService.calculateDifferenceInHours(nightDetail.getActualTimeOut(), nightDetail.getActualTimeIn()));
        nightDetail.setPlanHours(0d);
        nightDetail.setIsNeedToCheckAndCreateAdditionalHours(false);
        return nightDetail;
    }

    protected List<WorkedHoursDetailed> createElementDetails(WorkedHoursDetailed workDetail, DicScheduleElementType elementType) {
        List<WorkedHoursDetailed> list = new ArrayList();
        WorkedHoursSummary workedHoursSummary = workDetail.getWorkedHoursSummary();

        Date elementDayStart = workedHoursSummary.getWorkedDate();
        Date elementDayEnd = DateUtils.addDays(elementDayStart, 1);

        Date startDate = workDetail.getActualTimeIn();
        Date endDate = workDetail.getActualTimeOut();

        /* if 2nd date after 0.00 */
        if (!DateUtils.isSameDay(startDate, endDate)) {
            WorkedHoursSummary nextDaySummary = getNextDaySummary(workedHoursSummary);
            if (nextDaySummary != null) {
                /* is next day not holiday(example) too */
                DicScheduleElementType nextDayElementType = nextDaySummary.getScheduleElementType();
                if (!nextDayElementType.equals(workedHoursSummary.getScheduleElementType())) {
                    /* set end date for 1st new detail */
                    endDate = elementDayEnd;
                    /* if another type then create new detail - with new type
                    start date will began from 0.0 next day */
                    Date startDateForSecondDetail = elementDayEnd;
                    /* end date will be full of workDetail */
                    Date endDateForSecondDetail = workDetail.getActualTimeOut();
                    /* types for summaries and details like HOLIDAY and HOLIDAY_HOURS accordingly */
                    DicScheduleElementType elementTypeForSecondDetail = null;
                    switch (nextDayElementType.getCode()) {
                        case "HOLIDAY":
                            elementTypeForSecondDetail = getElementType("HOLIDAY_HOURS");
                            break;
                        case "WEEKEND":
                            elementTypeForSecondDetail = getElementType("WEEKEND_HOURS");
                            break;
                    }

                    list.add(createNewDetail(startDateForSecondDetail,
                            endDateForSecondDetail,
                            elementTypeForSecondDetail,
                            workDetail.getWorkedDate(),
                            workDetail.getWorkedHoursSummary(),
                            false));
                }

            }
        }

        list.add(createNewDetail(startDate,
                endDate,
                elementType,
                workDetail.getWorkedDate(),
                workDetail.getWorkedHoursSummary(),
                false));
        return list;
    }

    protected WorkedHoursDetailed createNewDetail(Date startDate,
                                                  Date endDate,
                                                  DicScheduleElementType elementType,
                                                  Date workedDate,
                                                  WorkedHoursSummary workedHoursSummary,
                                                  boolean checkAndCreateAdditionalHours) {
        WorkedHoursDetailed newDetail = metadata.create(WorkedHoursDetailed.class);
        newDetail.setActualTimeIn(startDate);
        newDetail.setActualTimeOut(endDate);
        newDetail.setScheduleElementType(elementType);
        newDetail.setWorkedDate(workedDate);
        newDetail.setWorkedHoursSummary(workedHoursSummary);
        newDetail.setTimecardHeader(workedHoursSummary.getTimecardHeader());
        newDetail.setHours(datesService.calculateDifferenceInHours(newDetail.getActualTimeOut(), newDetail.getActualTimeIn()));
        newDetail.setPlanHours(0d);
        newDetail.setIsNeedToCheckAndCreateAdditionalHours(checkAndCreateAdditionalHours);
        return newDetail;
    }

    protected WorkedHoursSummary getNextDaySummary(WorkedHoursSummary workedHoursSummary) {
        Date nextDay = DateUtils.addDays(workedHoursSummary.getWorkedDate(), 1);
        WorkedHoursSummary nextDaySummary = getWorkedHoursSummary(nextDay, workedHoursSummary.getTimecardHeader().getAssignmentGroup());
        return nextDaySummary;
    }

    protected static Date getSecondDate(Date date1, Date date2) {
        return (date1.after(date2)) ? date1 : date2;
    }

    protected static Date getFirstDate(Date date1, Date date2) {
        return (date1.after(date2)) ? date2 : date1;
    }

    protected TimecardHeader getFullTimecardHeader(TimecardHeader timecardHeader) {
        LoadContext<TimecardHeader> loadContext = LoadContext.create(TimecardHeader.class);
        loadContext.setQuery(LoadContext.createQuery("select e from tsadv$TimecardHeader e " +
                "   where e.id = :timecardHeaderId ")
                .setParameter("timecardHeaderId", timecardHeader.getId()))
                .setView(View.LOCAL);
        return dataManager.load(loadContext);
    }

    protected ScheduleHeader getScheduleHeader(StandardOffset offset, Date monthStart) {
        LoadContext<ScheduleHeader> loadContext = LoadContext.create(ScheduleHeader.class);
        loadContext.setQuery(LoadContext.createQuery("select e from tsadv$ScheduleHeader e " +
                "   where e.offset.id = :offsetId " +
                " and e.month = :date ")
                .setParameter("offsetId", offset.getId())
                .setParameter("date", monthStart))
                .setView("scheduleHeaderForTimecard");
        return dataManager.loadList(loadContext).stream().findFirst().orElse(null);
    }

    protected List<ScheduleHeader> getScheduleHeaders(List<StandardOffset> offsets, Date monthStart) {
        String str = getIdsAsString(offsets.stream().map(StandardOffset::getId).collect(Collectors.toList()));

        LoadContext<ScheduleHeader> loadContext = LoadContext.create(ScheduleHeader.class);
        loadContext.setQuery(LoadContext.createQuery("select e from tsadv$ScheduleHeader e " +
                "   where e.offset.id in " + str +
                " and e.month = :date ")
                .setParameter("date", monthStart))
                .setView("scheduleHeaderForTimecardWithOffset");
        return dataManager.loadList(loadContext);
    }

    protected List<TimecardHeader> getTimecardHeaders(List<UUID> assignmentGroupIds, Date monthStartDate, Date monthEndDate) {
        LoadContext<TimecardHeader> loadContext = LoadContext.create(TimecardHeader.class);
        String str = getIdsAsString(assignmentGroupIds);

        loadContext.setQuery(LoadContext.createQuery("select e from tsadv$TimecardHeader e " +
                "   where e.assignmentGroup.id in " + str +
                " and e.effectiveStartDate >= :monthStartDate " +
                " and e.effectiveEndDate <= :monthEndDate")
                .setParameter("monthStartDate", monthStartDate)
                .setParameter("monthEndDate", monthEndDate))
                .setView("timecardHeader-view");
        List<TimecardHeader> timecardHeaders = dataManager.loadList(loadContext);
        return timecardHeaders;
    }

    protected List<WorkedHoursSummary> getWorkedHoursSummaries(TimecardHeader timecardHeader) {
        LoadContext<WorkedHoursSummary> loadContext = LoadContext.create(WorkedHoursSummary.class);
        loadContext.setQuery(LoadContext.createQuery("select e from tsadv$WorkedHoursSummary e " +
                "   where e.timecardHeader.id = :timecardHeaderId ")
                .setParameter("timecardHeaderId", timecardHeader.getId()))
                .setView("workedHoursSummary-view");
        return dataManager.loadList(loadContext);
    }

    protected List<WorkedHoursDetailed> getWorkedHoursDetailed(WorkedHoursSummary summary) {
        LoadContext<WorkedHoursDetailed> loadContext = LoadContext.create(WorkedHoursDetailed.class);
        loadContext.setQuery(LoadContext.createQuery("select e from tsadv$WorkedHoursDetailed e " +
                "   where e.workedHoursSummary.id = :workedHoursSummaryId")
                .setParameter("workedHoursSummaryId", summary.getId()))
                .setView("workedHoursDetailed-view");
        return dataManager.loadList(loadContext);
    }

    //The kostyl'
    protected WorkedHoursDetailed getSpecialDetailForStartDate(WorkedHoursDetailed originalDetail) {
        List<WorkedHoursDetailed> entities = commonService.getEntities(WorkedHoursDetailed.class, "select e from tsadv$WorkedHoursDetailed e " +
                        "   where e.workedHoursSummary.id = :workedHoursSummaryId and e.id <> :originalDetailId and e.actualTimeIn = :startDate and e.actualTimeIn <> e.actualTimeOut and e.deleteTs is null and e.scheduleElementType.code <> 'WORK_HOURS'",
                ParamsMap.of("workedHoursSummaryId", originalDetail.getWorkedHoursSummary().getId(), "startDate", originalDetail.getActualTimeIn(), "originalDetailId", originalDetail.getId()),
                "workedHoursDetailed-view");
        if (entities.size() > 0) {
            return entities.get(0);
        }
        return null;
    }

    protected AssignmentSchedule getAssignmentScheduleForDay(Set<AssignmentSchedule> assignmentSchedules, TimecardHeader timecardHeader, Date date) {
        return assignmentSchedules.stream()
                .filter(as -> as.getAssignmentGroup().getId().equals(timecardHeader.getAssignmentGroup().getId())
                        && !as.getStartDate().after(date)
                        && !as.getEndDate().before(date))
                .findAny().orElse(null);
    }

    protected List<Absence> getAbsencesForDay(List<Absence> absences, PersonGroupExt personGroupExt, Date date) {
        return absences.stream().filter(
                as -> as.getPersonGroup().getId().equals(personGroupExt.getId()) &&
                        !as.getDateFrom().after(date) && !as.getDateTo().before(date))
                .collect(Collectors.toList());
    }

    protected List<BusinessTrip> getBusinessTripsForDay(List<BusinessTrip> businessTrips, PersonGroupExt personGroupExt, Date date) {
        return businessTrips.stream().filter(
                as -> as.getPersonGroup().getId().equals(personGroupExt.getId()) &&
                        !as.getDateFrom().after(date) && !as.getDateTo().before(date))
                .collect(Collectors.toList());
    }


    private void setDefaultSchedule(Collection<AssignmentGroupExt> assignmentGroups, Date startDate, Date endDate) {
        String offsetIdString = timecardConfig.getOffsetId();
        if (offsetIdString == null || assignmentGroups.isEmpty()) {
            return;
        }
        UUID offsetId = UUID.fromString(offsetIdString);
        StandardOffset offset = commonService.getEntity(StandardOffset.class,
                "select e from tsadv$StandardOffset e where e.id = :offsetId",
                ParamsMap.of("offsetId", offsetId), "standardOffset-with-schedule-view");

        List<AssignmentSchedule> assignmentSchedules = new ArrayList<>();
        for (AssignmentGroupExt assignmentGroup : assignmentGroups) {
            AssignmentSchedule assignmentSchedule = metadata.create(AssignmentSchedule.class);
            assignmentSchedule.setAssignmentGroup(assignmentGroup);
            assignmentSchedule.setOffset(offset);
            assignmentSchedule.setSchedule(offset.getStandardSchedule());
            assignmentSchedule.setStartDate(startDate);
            assignmentSchedule.setEndDate(endDate);
            assignmentSchedule.setColorsSet(MaterialDesignColorsEnum.GREEN);
            assignmentSchedules.add(assignmentSchedule);
            dataManager.commit(assignmentSchedule); //that's for integration with OEBS
        }

//        dataManager.commit(new CommitContext(assignmentSchedules)); //that's for integration with OEBS
//        persistence.runInTransaction(em -> {
//            Connection connection = em.getConnection();
//            try {
//                PreparedStatement preparedStatement = fillStatement(connection, assignmentSchedules, AssignmentSchedule.class);
//                preparedStatement.executeBatch();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//
//        });

    }

    private List<AssignmentGroupExt> getAssignmentGroupsWithNoSchedules(Collection<Timecard> timecards, Date
            startDate, Date endDate) {

        List<UUID> assignmentGroupIds = timecards.stream().filter(t -> t.getAssignmentGroupId() != null).map(Timecard::getAssignmentGroupId).collect(Collectors.toList());
        String assignmentGroupIdsStr = getIdsAsString(assignmentGroupIds);
        List<AssignmentGroupExt> assignmentGroups = new ArrayList<>();
        if (assignmentGroupIdsStr == null) {
            return assignmentGroups;
        }
        String queryString = "select " +
                " e.id, e.legacy_id " +
                " from BASE_ASSIGNMENT_GROUP e " +
                " left join TSADV_ASSIGNMENT_SCHEDULE o" +
                " on e.id = o.ASSIGNMENT_GROUP_ID" +
                "     and  o.delete_ts is null " +
                "     and ((o.start_DATE <= ?3 and o.start_DATE >= ?2) " +
                "     or (o.end_DATE <= ?3 and o.end_DATE >= ?2) " +
                "     or (o.start_DATE >= ?2 and o.end_DATE <= ?3) " +
                "     or (o.start_DATE <= ?3 and o.end_DATE >= ?3 ))" +
                " WHERE e.id in " + assignmentGroupIdsStr +
                " and o.ASSIGNMENT_GROUP_ID  is null";

        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createNativeQuery(queryString);

            query.setParameter(2, startDate);
            query.setParameter(3, endDate);

            List<Object[]> rows = query.getResultList();

            for (Object[] row : rows) {
                UUID assignmentGroupId = (UUID) row[0];
                String legacyId = (String) row[1];

                AssignmentGroupExt assignmentGroup = metadata.create(AssignmentGroupExt.class);
                assignmentGroup.setId(assignmentGroupId);
                assignmentGroup.setLegacyId(legacyId);
                assignmentGroups.add(assignmentGroup);
            }
            return assignmentGroups;
        }
    }

    protected List<AssignmentScheduleWithObjectsDto> getAssignmentScheduleWithObjectsDto(Collection<Timecard> timecards, Date
            startDate, Date endDate) {
        List<AssignmentScheduleWithObjectsDto> list = new ArrayList<>();

        Set<UUID> assignmentGroupIds = timecards.stream().filter(t -> t.getAssignmentGroupId() != null)
                .map(Timecard::getAssignmentGroupId).collect(Collectors.toSet());
        String assignmentGroupIdsStr = getIdsAsString(new ArrayList<>(assignmentGroupIds));

        String queryString = "SELECT\n" +
                "  o.id,\n" +
                "  o.START_DATE,\n" +
                "  o.END_DATE,\n" +
                "  o.ASSIGNMENT_GROUP_ID,\n" +
                "  o.OFFSET_ID,\n" +
                "  a.id AS assignment_id,\n" +
                "  a.start_date as assignment_start_date,\n" +
                "  a.end_date as assignment_end_date, \n" +
                "  st.id as st_id, " +
                "  st.code as st_code " +
                " FROM TSADV_ASSIGNMENT_SCHEDULE o\n" +
                "  LEFT JOIN base_assignment a\n" +
                "    ON a.group_id = o.assignment_group_id\n" +
                "       AND a.start_date <= ?3\n" +
                "       AND a.end_date >= ?2 " +
                "       AND a.delete_ts is null " +
                "  JOIN tsadv_dic_assignment_status st ON a.assignment_status_id = st.id\n" +
                "                                         AND st.code IN ('ACTIVE', 'SUSPENDED') " +
                "                                         AND st.delete_ts is NULL " +
                " WHERE o.delete_ts IS NULL AND\n" +
                "      o.ASSIGNMENT_GROUP_ID IN " + assignmentGroupIdsStr +
                " and o.start_DATE <= ?3 and o.end_date >= ?2\n" +
                "       and o.start_date <= a.end_date\n" +
                "       and o.end_date >= a.start_date " +
                " ORDER BY o.start_DATE ASC";


        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createNativeQuery(queryString);

            query.setParameter(2, startDate);
            query.setParameter(3, endDate);

            List<Object[]> rows = query.getResultList();

            for (Object[] row : rows) {
                UUID assignmentScheduleId = (UUID) row[0];
                Date assignmentStartDate = (Date) row[1];
                Date assignmentEndDate = (Date) row[2];

                AssignmentSchedule assignmentSchedule = metadata.create(AssignmentSchedule.class);
                assignmentSchedule.setId(assignmentScheduleId);
                assignmentSchedule.setStartDate(assignmentStartDate);
                assignmentSchedule.setEndDate(assignmentEndDate);

                AssignmentGroupExt assignmentGroupExt = metadata.create(AssignmentGroupExt.class);
                assignmentGroupExt.setId((UUID) row[3]);
                assignmentSchedule.setAssignmentGroup(assignmentGroupExt);

                StandardOffset offset = metadata.create(StandardOffset.class);
                offset.setId((UUID) row[4]);
                assignmentSchedule.setOffset(offset);

                AssignmentExt assignmentExt = metadata.create(AssignmentExt.class);
                assignmentExt.setId((UUID) row[5]);
                assignmentExt.setStartDate((Date) row[6]);
                assignmentExt.setEndDate((Date) row[7]);

                DicAssignmentStatus assignmentStatus = metadata.create(DicAssignmentStatus.class);
                assignmentStatus.setId((UUID) row[8]);
                assignmentStatus.setCode((String) row[9]);
                assignmentExt.setAssignmentStatus(assignmentStatus);

                assignmentGroupExt.setList(new ArrayList<>(Arrays.asList(assignmentExt)));
                assignmentGroupExt.setAssignment(assignmentExt);

                AssignmentScheduleWithObjectsDto dto = metadata.create(AssignmentScheduleWithObjectsDto.class);
                dto.setUuid(UUID.randomUUID());
                dto.setAssignmentSchedule(assignmentSchedule);
                list.add(dto);
            }
            return list;
        }
    }

    private List<TimecardCorrection> getTimecardCorrections(Set<AssignmentGroupExt> assignmentGroups, Date startDate, Date endDate) {
        List<UUID> assignmentGroupIds = assignmentGroups.stream().map(BaseUuidEntity::getId).collect(Collectors.toList());
        return commonService.getEntities(TimecardCorrection.class, "select e from tsadv$TimecardCorrection e " +
                        "where e.assignmentGroup.id in :uuidList and e.dateFrom >= :startDate and e.dateTo <= :endDate",
                ParamsMap.of("uuidList", assignmentGroupIds, "startDate", startDate, "endDate", endDate), "timecardCorrection-with-assignmentGroup");
    }

    protected List<ScheduleDetail> getAllScheduleDetails(List<ScheduleSummary> scheduleSummaries, Date
            startDate, Date endDate) {
        if (scheduleSummaries.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder("");
        for (Object uuid : scheduleSummaries.stream().map(BaseUuidEntity::getId).collect(Collectors.toList())) {
            sb.append("'").append(uuid).append("',");
        }
        String ids = sb.toString().substring(0, sb.toString().length() - 1);
        ids = "(" + ids + ")";

        LoadContext<ScheduleDetail> loadContext = LoadContext.create(ScheduleDetail.class);
        loadContext.setQuery(LoadContext.createQuery(" select e " +
                " from tsadv$ScheduleDetail e " +
                "    where e.summary.id in " + ids +
                " and e.dayDate >= :startDate " +
                " and e.dayDate <= :endDate " +
                "     order by e.timeIn, e.elementType")
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate))
                .setView("scheduleDetail.view");
        return new ArrayList<>(dataManager.loadList(loadContext));
    }

    protected List<ScheduleDetail> getScheduleDetailsForSummaryAndDate
            (List<ScheduleDetail> allScheduleDetails, ScheduleSummary scheduleSummary, Date date) {
        return allScheduleDetails.stream().filter(
                as -> as.getSummary().equals(scheduleSummary) &&
                        as.getDayDate().equals(date))
                .collect(Collectors.toList());
    }

    @Override
    public String getTimecardRepresentation(Double hours) {
        String value;
        if (timecardConfig.getDisplayHoursWithMinutes()) {
            value = datesService.getHoursWithMinutes(hours);
        } else {
            value = datesService.getHoursWithTwoDigitsAfterComma(hours);
        }
        return value;
    }
}