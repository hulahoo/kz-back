package kz.uco.tsadv.web.modules.personal.timecard.frames;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.KeyValueEntity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.backgroundwork.BackgroundWorkWindow;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.impl.ValueCollectionDatasourceImpl;
import com.haulmont.cuba.gui.executors.BackgroundTask;
import com.haulmont.cuba.gui.executors.TaskLifeCycle;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.entity.ReportOutputType;
import com.haulmont.reports.gui.ReportGuiManager;
import kz.uco.base.entity.abstraction.AbstractDictionary;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.datasource.TimecardDatasource;
import kz.uco.tsadv.datasource.TimecardHierarchyDatasource;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.global.dictionary.DicMonth;
import kz.uco.tsadv.modules.personal.dictionary.DicAbsenceType;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.Absence;
import kz.uco.tsadv.modules.personal.model.BusinessTrip;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.modules.timesheet.dictionary.DicScheduleElementType;
import kz.uco.tsadv.modules.timesheet.model.*;
import kz.uco.tsadv.service.BusinessRuleService;
import kz.uco.tsadv.service.DatesService;
import kz.uco.tsadv.service.TimecardService;
import kz.uco.tsadv.service.TimesheetService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.time.DateUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TimecardFrame extends AbstractFrame {

    protected final static int BATCH_SIZE = 200;

    @Inject
    private ComponentsFactory componentsFactory;
    @Inject
    protected UserSessionSource userSessionSource;
    @Inject
    protected CommonService commonService;
    @Inject
    protected Metadata metadata;
    @Inject
    protected TimecardService timecardService;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected DatesService datesService;
    @Inject
    protected BusinessRuleService businessRuleService;

    @Inject
    protected ReportGuiManager reportGuiManager;

    @Inject
    protected TimecardHierarchyDatasource hierarchyElementsDs;
    @Inject
    protected TimecardDatasource timecardsDs;
    @Inject
    protected ValueCollectionDatasourceImpl absencesAndTripsDs;
    @Inject
    protected CollectionDatasource<Absence, UUID> absencesDs;
    @Inject
    protected CollectionDatasource<BusinessTrip, UUID> businessTripsDs;
    @Inject
    protected CollectionDatasource<Absence, UUID> timecardAbsencesDs;
    @Inject
    protected CollectionDatasource<AssignmentSchedule, UUID> assignmentSchedulesDs;
    @Inject
    protected CollectionDatasource<WorkedHoursSummary, UUID> summariesDs;

    @Inject
    private DateField<Date> dateField;
    @Inject
    protected DataGrid<Timecard> dataGrid;
    @Named("dataGrid.deviations")
    protected Action tableDeviations;
    @Named("dataGrid.copyTimecard")
    protected Action dataGridCopyTimecard;
    @Inject
    protected TabSheet tabsheet;
    @Inject
    protected Table<WorkedHoursSummary> summariesTable;
    @Named("timecardAbsencesTable.create")
    protected CreateAction timecardAbsencesTableCreate;
    @Named("timecardAbsencesTable.edit")
    protected EditAction timecardAbsencesTableEdit;
    @Inject
    protected CheckBox enableInclusions;
    @Inject
    protected TextField searchByNumber;

    protected DateField<java.sql.Date> month;
    protected Consumer<Object> consumer;
    protected Date monthEnd;
    protected Date monthStart;
    protected UUID selectedPersonGroupId;
    protected UUID selectedAssignmentGroupId;
    protected Boolean enableInclusionsValue;
    protected OrganizationGroupExt organizationGroup;
    protected boolean isIntern;
    protected Map<UUID, List<TimecardDeviation>> deviations; //assignmentGroupId

    @Inject
    protected TimesheetService timesheetService;


    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        isIntern = params.get("isIntern") != null && params.get("isIntern").equals(true);
        organizationGroup = (OrganizationGroupExt) params.get("organizationGroup");
        consumer = (Consumer<Object>) params.get("consumer");
        enableInclusionsValue = (Boolean) params.get("enableInclusionsValue");
        if (enableInclusionsValue != null) {
            enableInclusions.setValue(enableInclusionsValue);
        }
        enableInclusions.addValueChangeListener(e -> consumer.accept(enableInclusions.getValue()));

        DicScheduleElementType workHoursType = commonService.getEntity(DicScheduleElementType.class, "WORK_HOURS");

        month = (DateField) getComponentNN("month");
        monthStart = datesService.getMonthBegin(month.getValue());
        monthEnd = datesService.getMonthEnd(month.getValue());
        dataGrid.setFrozenColumnCount(4);

        dataGridCopyTimecard.setEnabled(false);

        showMonthColumns(monthStart);
        month.addValueChangeListener(e -> showMonthColumns(monthStart));
        setDateToSearch();
        dataGrid.addSelectionListener(event -> {
            Set<Timecard> selected = dataGrid.getSelected();
            if (!selected.isEmpty()) {
                tableDeviations.setEnabled(true);
                if (selected.size() == 1) {
                    tabsheet.setVisible(true);
                    Timecard timecard = timecardsDs.getItem();
                    selectedAssignmentGroupId = timecard.getAssignmentGroupId();
                    selectedPersonGroupId = getPersonGroupId(selectedAssignmentGroupId);
                    dataGridCopyTimecard.setEnabled(true);
                    updateTabData();
                } else {
                    tabsheet.setVisible(false);
                    dataGridCopyTimecard.setEnabled(false);
                    summariesDs.setItem(null);
                }
            } else {
                dataGridCopyTimecard.setEnabled(false);
                tabsheet.setVisible(false);
            }
        });

//        DataGrid.Column corrective = dataGrid.addGeneratedColumn("corrective", new DataGrid.ColumnGenerator<Timecard, Component>() {
//            @Override
//            public Component getValue(DataGrid.ColumnGeneratorEvent event) {
//                CheckBox checkBox = componentsFactory.createComponent(CheckBox.class);
//                List<TimecardHeader> timecardHeaders = ((Timecard) event.getItem()).getTimecardHeaders();
//                if (!timecardHeaders.isEmpty() && timecardHeaders.get(0).getAssignmentGroup() != null) {
//
//                    Boolean corrective = ((Timecard) event.getItem()).getCorrective();
//                    checkBox.setValue(corrective);
//                    checkBox.addValueChangeListener(e -> {
//                        Boolean isTrue = (Boolean) e.getValue();
//                        if (isTrue) {
//                            TimecardCorrection timecardCorrection = metadata.create(TimecardCorrection.class);
//                            timecardCorrection.setAssignmentGroup(timecardHeaders.get(0).getAssignmentGroup());
//                            timecardCorrection.setDateFrom(month.getValue());
//                            timecardCorrection.setDateTo(monthEnd);
//                            dataManager.commit(timecardCorrection);
//                            timecardHeaders.forEach(h -> {
//                                h = dataManager.reload(h, "timecardHeader-for-correction");
//                                h.setTimecardCorrection(timecardCorrection);
//                                dataManager.commit(h);
//                            });
//                        } else {
//                            timecardHeaders.forEach(h -> {
//                                h = dataManager.reload(h, "timecardHeader-for-correction");
//                                h.setTimecardCorrection(null);
//                                dataManager.commit(h);
//                            });
//                            List<TimecardCorrection> timecardCorrections = commonService.getEntities(TimecardCorrection.class, "select e from tsadv$TimecardCorrection e " +
//                                            " where e.assignmentGroup.id = :assignmentGroupId and e.dateFrom = :dateFrom and e.dateTo = :dateTo",
//                                    ParamsMap.of("assignmentGroupId", timecardHeaders.get(0).getAssignmentGroup().getId(),
//                                            "dateFrom", month.getValue(), "dateTo", monthEnd), View.MINIMAL);
//                            timecardCorrections.forEach(t -> dataManager.remove(t));
//                        }
//                    });
//                } else {
//                    checkBox.setEditable(false);
//                }
//                return checkBox;
//            }
//
//            @Override
//            public Class getType() {
//                return CheckBox.class;
//            }
//        });
//
//        corrective.setRenderer(new WebComponentRenderer());

        tabsheet.addSelectedTabChangeListener(event -> {
            updateTabData();
            summariesDs.setItem(null);
        });
        tabsheet.setVisible(false);

        summariesTable.addGeneratedColumn("scheduleElementType", entity -> {
            String type = entity.getScheduleElementType().getLangValue();
            if (timecardService.isWeekendOrHoliday(entity.getScheduleElementType()) && entity.getHours() > 0) {
                type = workHoursType.getLangValue();
            }

            Label label = componentsFactory.createComponent(Label.class);

            if (entity.getAbsence() == null) {
                label.setValue(type);
            }

            return label;
        });

        summariesTable.addGeneratedColumn("planHours", entity -> {
            Label label = componentsFactory.createComponent(Label.class);
            Double planHours = 0d;
            List<WorkedHoursDetailed> detailsForSummary = getDetailsForSummary(entity);
            for (WorkedHoursDetailed workedHoursDetailed : detailsForSummary) {
                if ("WORK_HOURS".equals(workedHoursDetailed.getScheduleElementType().getCode())) {
                    planHours += workedHoursDetailed.getPlanHours();
                }
            }
            label.setValue(planHours);
            return label;
        });

        summariesTable.addGeneratedColumn("absenceType", entity -> {
            String type = "";
            if (entity.getAbsence() != null) {
                type = entity.getAbsence().getType().getLangValue();
            }
            if (CollectionUtils.isNotEmpty(entity.getAbsenceToWorkedHoursSummaryList())) {
                type = entity.getAbsenceToWorkedHoursSummaryList().stream()
                        .map(AbsenceToWorkedHoursSummary::getAbsence)
                        .map(Absence::getType)
                        .map(AbstractDictionary::getLangValue)
                        .collect(Collectors.joining(", "));
            }
            if (entity.getBussinessTrip() != null && entity.getBussinessTrip().getType() != null) {
                type = getMessage("businessTrip");
                type += " " + entity.getBussinessTrip().getType().getLangValue();
            }

            Label label = componentsFactory.createComponent(Label.class);
            label.setValue(type);
            return label;
        });

        timecardAbsencesTableCreate.setWindowParamsSupplier(() -> ParamsMap.of("offerOnlyTimecardAbsencesType", true,
                "assignmentGroupId", timecardsDs.getItem().getAssignmentGroupId()));
        timecardAbsencesTableEdit.setWindowParamsSupplier(() -> ParamsMap.of("offerOnlyTimecardAbsencesType", true,
                "assignmentGroupId", timecardsDs.getItem().getAssignmentGroupId()));

        dataGrid.addCellStyleProvider((entity, property) -> {
            if (isFirstCellStyleProvider(entity, property)) {
                reloadDeviation();
            }

            if (property.startsWith("summary")) {
                WorkedHoursSummary summary = entity.getValue(property);
                return getCssStyle(summary, entity);
            }
            if (property.startsWith("name")) {
                if (entity.getFactHoursWithoutOvertime() != null && entity.getPlanWorkHours() != null) {
                    if (entity.getFactHoursWithoutOvertime() - entity.getPlanWorkHours() > 12) {
                        return "red-day";
                    }
                }
            }
            return null;
        });

        summariesDs.addItemChangeListener(e -> {
            if (e.getItem() != null) {
                AbstractWindow window = openWindow("tsadv$worked-hours-detailed", WindowManager.OpenType.DIALOG,
                        ParamsMap.of("selectedWorkedHoursSummary", e.getItem()));
                window.addCloseWithCommitListener(() -> {
                    timecardService.calculateStatistic(e.getItem().getTimecardHeader());
                    consumer.accept(enableInclusions.getValue());
                });
            }

        });

        searchByNumber.addEnterPressListener(e -> searchPersonByCode());

        modifyDataGridForInterns();
    }

    private void reloadDeviation() {
        if (timecardsDs.getItems().isEmpty()) deviations = null;

        List<UUID> collectUUID = timecardsDs.getItems().stream().map(Timecard::getAssignmentGroupId).collect(Collectors.toList());
        deviations = commonService.getEntities(TimecardDeviation.class,
                "select e from tsadv$TimecardDeviation e " +
                        " where e.hours <> 0 " +
                        "   and e.assignmentGroup.id in :collectUUID " +
                        "   and e.dateFrom >= :dateFrom and e.dateTo <= :dateTo",
                ParamsMap.of("collectUUID", collectUUID,
                        "dateFrom", monthStart,
                        "dateTo", monthEnd),
                "timecardDeviation-for-form-timecard")
                .stream()
                .collect(Collectors.groupingBy(o -> o.getAssignmentGroup().getId()));

    }

    private boolean isFirstCellStyleProvider(Timecard entity, String property) {
        return "name".equals(property) && timecardsDs.getItems().iterator().next().equals(entity);
    }

    protected void modifyDataGridForInterns() {
        if (isIntern) {
            dataGrid.getColumnNN("name").setCaption(getMessage("Intern"));
            dataGrid.addGeneratedColumn(
                    "positionName",
                    new DataGrid.ColumnGenerator<Timecard, String>() {
                        @Override
                        public String getValue(DataGrid.ColumnGeneratorEvent<Timecard> event) {
                            return getMessage("Intern");
                        }

                        @Override
                        public Class<String> getType() {
                            return String.class;
                        }
                    });
        }
    }

    protected void setDateToSearch() {
        int currentMonthNumber = datesService.getMonthNumber(CommonUtils.getSystemDate());
        Date systemDate = CommonUtils.getSystemDate();
        systemDate = datesService.getDayBegin(systemDate);

        int monthNumber = datesService.getMonthNumber(month.getValue());
        if (monthNumber != currentMonthNumber) {
            dateField.setValue(month.getValue());
        } else {
            dateField.setValue(systemDate);
        }
    }

    protected void configureAssignmentSchedulesTableActions() {
        HashMap<String, Object> paramsMap = new HashMap<>();
        PersonGroupExt personGroupExt = new PersonGroupExt();
        personGroupExt.setId(selectedPersonGroupId);

        paramsMap.put("personGroup", personGroupExt);
        paramsMap.put("startDate", monthStart);
        paramsMap.put("endDate", monthEnd);
        paramsMap.put("loadFullData", true);
        paramsMap.put("enableInclusions", false);
        List<Timesheet> timesheets = timesheetService.getTimesheets(paramsMap);

        Table<AssignmentSchedule> assignmentSchedulesTable = (Table) getComponentNN("assignmentSchedulesTable");
        CreateAction createAction = (CreateAction) assignmentSchedulesTable.getActionNN("create");
        EditAction editAction = (EditAction) assignmentSchedulesTable.getActionNN("edit");

        createAction.setWindowParamsSupplier(() -> ParamsMap.of("personGroup", hierarchyElementsDs.getItem().getPersonGroup(), "timesheets", timesheets));

        createAction.setEditorCloseListener(actionId -> assignmentSchedulesDs.refresh(ParamsMap.of("assignmentGroupId", selectedAssignmentGroupId,
                "startDate", monthStart, "endDate", monthEnd)));
        editAction.setEditorCloseListener(actionId -> assignmentSchedulesDs.refresh(ParamsMap.of("assignmentGroupId", selectedAssignmentGroupId,
                "startDate", monthStart, "endDate", monthEnd)));

        RemoveAction removeAction = (RemoveAction) assignmentSchedulesTable.getActionNN("remove");
        removeAction.setAfterRemoveHandler(set -> assignmentSchedulesDs.refresh());
    }

    protected List<WorkedHoursDetailed> getDetailsForSummary(WorkedHoursSummary workedHoursSummary) {
        return commonService.getEntities(WorkedHoursDetailed.class, "select e" +
                        "                           from tsadv$WorkedHoursDetailed e" +
                        "                          where e.workedHoursSummary.id = :workedHoursSummaryId", ParamsMap.of("workedHoursSummaryId", workedHoursSummary.getId()),
                "workedHoursDetailed-with-type");
    }

    public void searchPersonByCode() {
        if (searchByNumber.getValue() != null) {
            PersonExt personExt = getPersonByEmployeeCode(searchByNumber.getValue().toString(), dateField.getValue(), organizationGroup != null ? organizationGroup.getId() : null);
            if (personExt != null) {
                consumer.accept(personExt);
            } else {
                showNotification(getMessage("personNotFound"), NotificationType.HUMANIZED);
            }
        } else {
            consumer.accept(null);
        }
    }

    protected PersonExt getPersonByEmployeeCode(String employeeNumber, Date date, UUID organizationGroupId) {
        return commonService.getEntity(PersonExt.class, "select distinct e" +
                        "  from base$PersonExt e" +
                        "    left join e.group.assignments a" +
                        "    left join aa$TemporaryTransfer tt " +
                        "       where e.employeeNumber = :employeeNumber" +
                        "           and :date between e.startDate and e.endDate" +
                        "           and :date between a.startDate and a.endDate" +
                        "           and :date between tt.startDate and tt.endDate" +
                        "           and (a.assignmentStatus.code = 'ACTIVE' or  a.assignmentStatus.code = 'SUSPENDED')" +
                        "           and a.primaryFlag = true" +
                        "           and (a.organizationGroup.id = :organizationGroupId" +
                        "            or tt.organization.id = :organizationGroupId)",
                ParamsMap.of("employeeNumber", employeeNumber,
                        "date", date,
                        "organizationGroupId", organizationGroupId),
                "person-for-search");
    }

    protected String getCssStyle(WorkedHoursSummary workedHoursSummary, Timecard timecard) {
        if (workedHoursSummary == null) {
            return null;
        }

        List<AbsenceToWorkedHoursSummary> absenceToWorkedHoursSummaryList = workedHoursSummary.getAbsenceToWorkedHoursSummaryList();

        if (deviations != null && CollectionUtils.isNotEmpty(absenceToWorkedHoursSummaryList)) {

            boolean isWorkingDate = Optional.ofNullable(workedHoursSummary.getAbsence())
                    .map(Absence::getType)
                    .map(DicAbsenceType::getIsWorkingDay)
                    .orElse(false)
                    || absenceToWorkedHoursSummaryList.stream()
                    .map(AbsenceToWorkedHoursSummary::getAbsence)
                    .map(Absence::getType)
                    .map(DicAbsenceType::getIsWorkingDay)
                    .anyMatch(BooleanUtils::isTrue);

            List<TimecardDeviation> timecardDeviations = deviations.get(timecard.getAssignmentGroupId());
            boolean hasDeviation = false;
            if (CollectionUtils.isNotEmpty(timecardDeviations)) {
                double sumHours = timecardDeviations.stream()
                        .filter(deviation -> !deviation.getDateFrom().after(workedHoursSummary.getWorkedDate())
                                && !deviation.getDateTo().before(workedHoursSummary.getWorkedDate()))
                        .mapToDouble(TimecardDeviation::getHours)
                        .sum();
                hasDeviation = Math.abs(sumHours) > 1e-4;
            }
            if (isWorkingDate && hasDeviation) {
                return "lightCoral-day";
            }
        }

        Absence absence = workedHoursSummary.getAbsence();
        if (absence != null) {
            DicAbsenceType absenceType = absence.getType();
            if (absenceType != null) {
                Boolean displayAbsence = absenceType.getDisplayAbsence();
                if (absenceType.getTimesheetCode() != null) {
                    if (displayAbsence != null) {
                        if (displayAbsence) {
                            return "orange-day";
                        }

                    }
                }
            }
        }

        Optional<BusinessTrip> businessTripOptional = Optional.ofNullable(workedHoursSummary.getBussinessTrip());
        if (businessTripOptional.isPresent()) {
            return "blue-day";
        }

        DicScheduleElementType elementType = workedHoursSummary.getScheduleElementType();
        String code = elementType.getCode();
        if (!code.equals("WORK_HOURS")) {
            if (code.equals("WEEKEND")) {
                return "green-day";
            }
            if (code.equals("HOLIDAY")) {
                return "red-day";
            }
        }

        if (code.equals("WORK_HOURS")
                && workedHoursSummary.getHours() == 0
                && workedHoursSummary.getBussinessTrip() == null
                && workedHoursSummary.getAbsence() == null
                && CollectionUtils.isEmpty(absenceToWorkedHoursSummaryList)) {
            return "green-day";
        }
        return null;
    }

    protected void updateTabData() {
        String selectedTab = tabsheet.getSelectedTab().getName();
        switch (selectedTab) {
            case "absencesTab":
                showAbsencesAndTrips(selectedPersonGroupId, monthStart, monthEnd);
                break;
            case "schedulesTab":
                showAsignmentSchedules(selectedAssignmentGroupId, monthStart, monthEnd);
                configureAssignmentSchedulesTableActions();
                break;
            case "timecardInDetailTab":
                List<TimecardHeader> timecardHeaders = timecardsDs.getItem().getTimecardHeaders();
                showSummaries(timecardHeaders, monthStart, monthEnd);
                break;
            case "timecardAbsencesTab":
                showTimecardAbsences(selectedPersonGroupId, monthStart, monthEnd);
                break;
        }
    }

    public void formTimeCards() {
        final OrganizationGroupExt organizationGroup = hierarchyElementsDs.getItem().getOrganizationGroup();
        final PositionGroupExt positionGroup = hierarchyElementsDs.getItem().getPositionGroup();
        final PersonGroupExt personGroup = hierarchyElementsDs.getItem().getPersonGroup();
        final Date monthStart = this.monthStart;
        final Date monthEndFinal = monthEnd;
        final Boolean enableInclusionsFinal = enableInclusions.getValue();

        BackgroundTask<Integer, Void> backgroundTask = new BackgroundTask<Integer, Void>(0, TimeUnit.SECONDS, this.getHostScreen()) { //TODO нужно проверить
            @Override
            public Void run(TaskLifeCycle<Integer> taskLifeCycle) throws Exception {
                int firstResult = 0;
                Collection<Timecard> timecards = new ArrayList<>();
                while (timecards.isEmpty() || timecards.size() == BATCH_SIZE) {
                    if (isIntern)
                        timecards = timecardService.getTimecards(organizationGroup,
                                personGroup,
                                monthStart, monthEndFinal, firstResult, BATCH_SIZE,
                                false, null, enableInclusionsFinal);
                    else
                        timecards = timecardService.getTimecards(organizationGroup,
                                positionGroup,
                                personGroup,
                                monthStart, monthEndFinal, firstResult, BATCH_SIZE,
                                false, null, enableInclusionsFinal);
                    firstResult += timecards.size();
                    if (!timecards.isEmpty()) {
                        timecardService.formTimecard(monthStart, timecards);
                    } else {
                        break;
                    }
                }
                return null;
            }

            @Override
            public void done(Void result) {
                consumer.accept(enableInclusions.getValue());
            }

            @Override
            public boolean handleException(Exception ex) {
                if (ex != null && ex.getMessage() != null && ex.getMessage().contains("scheduleNotFormed")) {
                    String offsetName = ex.getMessage().replace("scheduleNotFormed", "");
                    showNotification(businessRuleService.getBusinessRuleMessage("scheduleNotFormed") + " " + offsetName, NotificationType.HUMANIZED);
                } else if (ex != null && ex.getMessage() != null) {
                    ex.printStackTrace();
                }
                return true;
            }
        };
        BackgroundWorkWindow.show(backgroundTask, true);
    }

    protected void setCorrectiveToAllTimeCards(Boolean corrective) {
        final OrganizationGroupExt organizationGroup = hierarchyElementsDs.getItem().getOrganizationGroup();
        final PositionGroupExt positionGroup = hierarchyElementsDs.getItem().getPositionGroup();
        final PersonGroupExt personGroup = hierarchyElementsDs.getItem().getPersonGroup();
        final Date monthStart = this.monthStart;
        final Date monthEndFinal = monthEnd;
        final Boolean enableInclusionsFinal = enableInclusions.getValue();

        BackgroundTask<Integer, Void> backgroundTask = new BackgroundTask<Integer, Void>(0, TimeUnit.SECONDS, this.getHostScreen()) { //TODO нужно проверить
            @Override
            public Void run(TaskLifeCycle<Integer> taskLifeCycle) throws Exception {
                int firstResult = 0;
                Collection<Timecard> timecards = new ArrayList<>();
                while (timecards.isEmpty() || timecards.size() == BATCH_SIZE) {
                    timecards = timecardService.getTimecards(organizationGroup,
                            positionGroup,
                            personGroup,
                            monthStart, monthEndFinal, firstResult, BATCH_SIZE,
                            false, null, enableInclusionsFinal);
                    firstResult += timecards.size();
                    if (!timecards.isEmpty()) {
                        timecardService.setCorrective(corrective, timecards, monthStart, monthEnd);
                    } else {
                        break;
                    }
                }
                return null;
            }

            @Override
            public void done(Void result) {
                consumer.accept(enableInclusions.getValue());
            }

            @Override
            public boolean handleException(Exception ex) {
                if (ex != null && ex.getMessage() != null) {
                    ex.printStackTrace();
                }
                return true;
            }
        };
        BackgroundWorkWindow.show(backgroundTask, true);
    }

    public void insertDeviation() {
        Map<String, Object> params = new HashMap<>();
        Set<Timecard> selectedTimecards = dataGrid.getSelected();
        selectedTimecards = selectedTimecards.stream().filter(e -> e.getWorkedHoursSummaries() != null).collect(Collectors.toSet());
        params.put("startDate", monthStart);
        params.put("endDate", monthEnd);
        params.put("selectedTimecards", selectedTimecards);
        if (!selectedTimecards.isEmpty()) {
            Window window = openWindow("tsadv$timecard-deviations", WindowManager.OpenType.DIALOG, params);
            window.addCloseWithCommitListener(() -> consumer.accept(enableInclusions.getValue()));
        } else {
            showNotification(getMessage("ThereIsNoTimecards"), NotificationType.TRAY);
        }
    }

    protected void showMonthColumns(Date month) {
        if (month != null) {
            Date maxMonthDate = DateUtils.addDays(DateUtils.addMonths(month, 1), -1);
            Calendar c = Calendar.getInstance();
            c.setTime(maxMonthDate);
            int maxMonthDay = c.get(Calendar.DAY_OF_MONTH);

            for (int j = 1; j <= 10; j++) {
                dataGrid.getColumnNN("summary" + j).setCaption(String.format("%02d", j));
            }

            for (int j = maxMonthDay + 1; j <= 31; j++) {
                dataGrid.getColumnNN("summary" + j).setCollapsed(true);
            }
        }
    }

    protected void showAbsencesAndTrips(UUID selectedPersonGroupId, Date startDate, Date endDate) {
        absencesAndTripsDs.clear();
        absencesDs.refresh(ParamsMap.of("personGroupId", selectedPersonGroupId,
                "startDate", startDate, "endDate", endDate));
        businessTripsDs.refresh(ParamsMap.of("personGroupId", selectedPersonGroupId,
                "startDate", startDate, "endDate", endDate));
        Collection<KeyValueEntity> keyValueItems = getKeyValueItems(absencesDs.getItems(), businessTripsDs.getItems());
        keyValueItems.forEach(e -> absencesAndTripsDs.addItem(e));
    }

    protected Collection<KeyValueEntity> getKeyValueItems(Collection<Absence> absences, Collection<BusinessTrip> trips) {
        ArrayList<KeyValueEntity> list = new ArrayList<>();
        absences.forEach(absence -> list.add(createKeyKeyValueEntityByAbsence(absence)));

        String businessTripEntityLocalName =
                messages.getTools().getEntityCaption(metadata.getClassNN(BusinessTrip.class), userSessionSource.getLocale());

        trips.forEach(trip -> list.add(createKeyKeyValueEntityByTrip(trip, businessTripEntityLocalName)));

        list.sort((o1, o2) -> (((Date) o1.getValue("dateFrom")).compareTo(o2.getValue("dateTo"))));
        return list;
    }

    protected KeyValueEntity createKeyKeyValueEntityByTrip(BusinessTrip trip, String businessTripEntityLocalName) {
        KeyValueEntity keyValueEntity = createKeyKeyValueEntity(trip.getId(), trip.getDateFrom(), trip.getDateTo(), trip.getAbsenceDays());
        keyValueEntity.setValue("type", businessTripEntityLocalName + " - " + trip.getType().getLangValue());
        keyValueEntity.setValue("status", messages.getMessage(trip.getStatus()));
        if (trip.getTypeTrip() != null) {
            keyValueEntity.setValue("additional", messages.getMessage(trip.getTypeTrip()));
        }
        return keyValueEntity;
    }

    protected KeyValueEntity createKeyKeyValueEntityByAbsence(Absence absence) {
        KeyValueEntity keyValueEntity = createKeyKeyValueEntity(absence.getId(), absence.getDateFrom(), absence.getDateTo(), absence.getAbsenceDays());
        keyValueEntity.setValue("type", absence.getType().getLangValue());
        keyValueEntity.setValue("orderNumber", absence.getOrderNum());
        keyValueEntity.setValue("orderDate", absence.getOrderDate());
        return keyValueEntity;
    }

    protected KeyValueEntity createKeyKeyValueEntity(UUID id, Date dateFrom, Date dateTo, Integer absenceDays) {
        KeyValueEntity keyValueEntity = new KeyValueEntity();
        keyValueEntity.setIdName("id");
        keyValueEntity.setValue("id", id);
        keyValueEntity.setValue("dateFrom", dateFrom);
        keyValueEntity.setValue("dateTo", dateTo);
        keyValueEntity.setValue("absenceDays", absenceDays);
        return keyValueEntity;
    }

    protected void showTimecardAbsences(UUID selectedPersonGroupId, Date startDate, Date endDate) {
        timecardAbsencesDs.refresh(ParamsMap.of("personGroupId", selectedPersonGroupId,
                "startDate", startDate, "endDate", endDate));
    }

    protected void showAsignmentSchedules(UUID selectedAssignmentGroupId, Date startDate, Date endDate) {
        assignmentSchedulesDs.refresh(ParamsMap.of("assignmentGroupId", selectedAssignmentGroupId,
                "startDate", startDate, "endDate", endDate));
    }

    protected void showSummaries(List<TimecardHeader> timecardHeaders, Date startDate, Date endDate) {
        StringBuilder sb = new StringBuilder();
        if (timecardHeaders.isEmpty()) {
            return;
        }
        for (Object uuid : timecardHeaders.stream().map(BaseUuidEntity::getId).collect(Collectors.toList())) {
            sb.append("'").append(uuid).append("',");
        }
        String ids = sb.toString().substring(0, sb.toString().length() - 1);
        ids = "(" + ids + ")";
        summariesDs.setQuery("select e from tsadv$WorkedHoursSummary e where e.timecardHeader.id in " + ids +
                " and e.workedDate between :custom$startDate and :custom$endDate and e.deleteTs is null order by e.workedDate");
        summariesDs.refresh(ParamsMap.of("startDate", startDate, "endDate", endDate));
    }

    protected UUID getPersonGroupId(UUID assignmentId) {
        Map<Integer, Object> paramToQuery = new HashMap<>();
        paramToQuery.put(1, assignmentId);
        String query = "SELECT distinct a.person_group_id\n" +
                "FROM base_assignment a\n" +
                "   WHERE a.group_id = ?1 " +
                "      AND a.delete_ts is null ";
        return commonService.emNativeQuerySingleResult(UUID.class, query, paramToQuery);
    }

    public void reportExcel() {
        report(ReportOutputType.XLSX);
    }

    public void reportPdf() {
        report(ReportOutputType.PDF);
    }

    public void report(ReportOutputType reportOutputType) {
        Map<String, Object> params = new HashMap<>();
        OrganizationGroupExt organizationGroup = hierarchyElementsDs.getItem().getOrganizationGroup();
        PositionGroupExt positionGroup = hierarchyElementsDs.getItem().getPositionGroup();
        PersonGroupExt personGroup = hierarchyElementsDs.getItem().getPersonGroup();
        params.put("pDepartment", organizationGroup);
        params.put("pPosition", positionGroup);
        params.put("pEmployee", personGroup);
        params.put("pIsIntern", isIntern);
        params.put("pEnableAttach", enableInclusions.getValue());

        String fileName = "";

        if (organizationGroup != null) {
            organizationGroup = dataManager.reload(organizationGroup, "organizationGroupExt.for.attestation.lookup");
            fileName = organizationGroup.getOrganizationName();
            fileName = fileName.replace('.', '_');
        }

        if (positionGroup != null) {
            positionGroup = dataManager.reload(positionGroup, "positionGroup.filter");
            fileName = positionGroup.getPositionName();
            fileName = fileName.replace('.', '_');
        }

        if (personGroup != null) {
            personGroup = dataManager.reload(personGroup, "personGroupExt.responsibleEmployee");
            fileName = personGroup.getFullName();
            fileName = fileName.replace('.', '_');
        }


        String dateFormated = new SimpleDateFormat("MMyyyy").format(monthStart);
        String dateFormatedForFileName = new SimpleDateFormat("MM-yyyy").format(monthStart);
        fileName += " " + dateFormatedForFileName;

        DicMonth dicMonth = commonService.getEntity(DicMonth.class, dateFormated);

        if (dicMonth == null) {
            showNotification(getMessage("no.Dictionary.for.month"), NotificationType.HUMANIZED);
            return;
        }

        params.put("pMonth", dicMonth);

        LoadContext<Report> reportLoadContext = LoadContext.create(Report.class)
                .setQuery(LoadContext.createQuery("select r from report$Report r where r.code = :code")
                        .setParameter("code", "KCHR-9"))
                .setView("report.edit");
        Report report = dataManager.load(reportLoadContext);

        reportGuiManager.printReport(report, params, null, fileName, reportOutputType, this);
    }

    public void copyTimecard() {
        if (timecardsDs.getItem() == null) {
            showNotification(getMessage("selectFromToCopy"));
            return;
        }
        showNotification(getMessage("selectWhomToCopy"));

        openLookup("base$Person.browse", items -> {
            List<PersonGroupExt> personGroups = ((Collection<PersonExt>) items).stream()
                    .map(PersonExt::getGroup)
                    .collect(Collectors.toList());
            copyTimecards(personGroups, timecardsDs.getItem(), monthStart);
        }, WindowManager.OpenType.DIALOG, ParamsMap.of("multiselect", true));
    }

    protected void copyTimecards(List<PersonGroupExt> personGroups, Timecard selectedTimecard, Date monthToCopy) {

        Map<String, Object> params = ParamsMap.of(
                "personGroups", personGroups,
                "selectedTimecard", selectedTimecard,
                "monthToCopy", monthToCopy);

        AbstractWindow window = openWindow("tsadv$timecard-copy", WindowManager.OpenType.DIALOG, params);
        window.addCloseWithCommitListener(() -> consumer.accept(enableInclusions.getValue()));
    }

    public void setCorrective() {
        setCorrectiveToAllTimeCards(true);
    }

    public void unsetCorrective() {
        setCorrectiveToAllTimeCards(false);
    }
}
