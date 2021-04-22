package kz.uco.tsadv.web.modules.performance.performanceplan;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.Dialogs;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.Screens;
import com.haulmont.cuba.gui.app.core.file.FileUploadDialog;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.export.ByteArrayDataProvider;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.export.ExportFormat;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.model.InstanceLoader;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.gui.upload.FileUploadingAPI;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.gui.ReportGuiManager;
import com.ibm.icu.text.RuleBasedNumberFormat;
import com.ibm.icu.text.SimpleDateFormat;
import kz.uco.base.common.BaseCommonUtils;
import kz.uco.base.entity.dictionary.DicCompany;
import kz.uco.base.entity.dictionary.DicCurrency;
import kz.uco.base.service.NotificationService;
import kz.uco.tsadv.config.ExtAppPropertiesConfig;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.modules.performance.enums.AssignedGoalTypeEnum;
import kz.uco.tsadv.modules.performance.enums.CardStatusEnum;
import kz.uco.tsadv.modules.performance.model.*;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.*;
import kz.uco.tsadv.service.DatesService;
import kz.uco.tsadv.service.KpiService;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.inject.Inject;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;

@UiController("tsadv$PerformancePlan.edit")
@UiDescriptor("performance-plan-edit.xml")
@EditedEntityContainer("performancePlanDc")
public class PerformancePlanEdit extends StandardEditor<PerformancePlan> {
    @Inject
    protected Notifications notifications;
    @Inject
    protected MessageBundle messageBundle;
    @Inject
    protected Screens screens;
    @Inject
    protected InstanceLoader<PerformancePlan> performancePlanDl;
    @Inject
    protected CollectionLoader<AssignedPerformancePlan> assignedPerformancePlansDl;
    @Inject
    protected InstanceContainer<PerformancePlan> performancePlanDc;
    @Inject
    protected CollectionLoader<InstructionsKpi> instructionKpiDl;
    @Inject
    protected ScreenBuilders screenBuilders;
    @Inject
    protected Metadata metadata;
    @Inject
    protected CollectionContainer<AssignedPerformancePlan> assignedPerformancePlansDc;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected TabSheet tabSheet;
    @Inject
    protected Table<InstructionsKpi> instructionTable;
    @Inject
    protected DataGrid<AssignedPerformancePlan> assignedPerformancePlanTable;
    @Inject
    protected CollectionLoader<ScoreSetting> scoreSettingDl;
    @Inject
    protected Table<ScoreSetting> scoreSettingTable;
    @Inject
    protected KpiService kpiService;
    @Inject
    protected CollectionLoader<CorrectionCoefficient> correctionCoefDl;
    @Inject
    protected Table<CorrectionCoefficient> correctionCoefTable;
    @Inject
    protected CollectionContainer<CorrectionCoefficient> correctionCoefDc;
    @Inject
    protected ExtAppPropertiesConfig extAppPropertiesConfig;
    @Inject
    protected FileUploadingAPI fileUploadingAPI;
    @Inject
    protected ReportGuiManager reportGuiManager;
    @Inject
    protected ExportDisplay exportDisplay;
    @Inject
    protected DatesService datesService;
    @Inject
    protected GlobalConfig globalConfig;
    @Inject
    protected Dialogs dialogs;
    private FileInputStream inputStream;
    @Inject
    protected NotificationService notificationService;
    protected SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
    protected SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
    protected SimpleDateFormat monthTextFormatRu = new SimpleDateFormat("dd MMMM yyyy", Locale.forLanguageTag("ru"));
    protected SimpleDateFormat monthTextFormatEn = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);
    protected SimpleDateFormat monthYearTextFormatRu = new SimpleDateFormat("MMMM yyyy", Locale.forLanguageTag("ru"));
    protected SimpleDateFormat monthYearTextFormatEn = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);


    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        performancePlanDl.load();
        assignedPerformancePlansDl.setParameter("performancePlan", performancePlanDc.getItem());
        assignedPerformancePlansDl.load();
        instructionKpiDl.setParameter("performancePlan", performancePlanDc.getItem());
        instructionKpiDl.load();
        scoreSettingDl.setParameter("performancePlan", performancePlanDc.getItem());
        scoreSettingDl.load();
        correctionCoefDl.setParameter("performancePlan", performancePlanDc.getItem());
        correctionCoefDl.load();
    }

    @Subscribe
    protected void onInit(InitEvent event) {
        assignedPerformancePlanTable.addRowStyleProvider(assignedPerformancePlan -> {
            if (assignedPerformancePlan.getMaxBonusPercent() != null) {
                return "orange-day";
            }
            return null;
        });
    }


    @Subscribe
    protected void onAfterShow(AfterShowEvent event) {
        if (PersistenceHelper.isNew(performancePlanDc.getItem())) {
            visibleTab(false);
            performancePlanDc.getItem().setStartDate(BaseCommonUtils.getSystemDate());
            performancePlanDc.getItem().setAccessibilityEndDate(BaseCommonUtils.getEndOfTime());
            performancePlanDc.getItem().setEndDate(BaseCommonUtils.getEndOfTime());
            performancePlanDc.getItem().setAccessibilityStartDate(BaseCommonUtils.getSystemDate());
        } else {
            visibleTab(true);
        }
    }

    @Subscribe
    protected void onAfterCommitChanges(AfterCommitChangesEvent event) {
        visibleTab(true);
    }

    protected void visibleTab(Boolean isVisible) {
        tabSheet.getTab("assignedPerformancePlan").setVisible(isVisible);
        tabSheet.getTab("instruction").setVisible(isVisible);
        tabSheet.getTab("scoreSetting").setVisible(isVisible);
        tabSheet.getTab("correctionCoef").setVisible(isVisible);
    }


    @Subscribe
    protected void onBeforeCommitChanges(BeforeCommitChangesEvent event) {
        Date accessibilityStartDate = performancePlanDc.getItem().getAccessibilityStartDate();
        Date startDate = performancePlanDc.getItem().getStartDate();
        if (accessibilityStartDate != null && startDate != null && accessibilityStartDate.before(startDate)) {
            notifications.create().withPosition(Notifications.Position.BOTTOM_RIGHT)
                    .withCaption(messageBundle.getMessage("accessStartDateNotBeEarlier")).show();
            event.preventCommit();
        }
    }

    @Subscribe("instructionTable.create")
    protected void onInstructionTableCreate(Action.ActionPerformedEvent event) {
        screenBuilders.editor(instructionTable)
                .newEntity()
                .withInitializer(instructionsKpi -> instructionsKpi.setPerformancePlan(performancePlanDc.getItem()))
                .build().show()
                .addAfterCloseListener(afterCloseEvent -> instructionKpiDl.load());
    }

    @Subscribe("assignedPerformancePlanTable.create")
    protected void onAssignedPerformancePlanTableCreate(Action.ActionPerformedEvent event) {
        screenBuilders.lookup(PersonExt.class, this)
                .withScreenId("base$PersonForKpiCard.browse")
                .withSelectHandler(personList -> {
                    CommitContext commitContext = new CommitContext();
                    personList.forEach(personExt -> {
                        boolean isNew = true;
                        List<AssignedPerformancePlan> assignedPerformancePlanList =
                                dataManager.load(AssignedPerformancePlan.class)
                                        .query("select e from tsadv$AssignedPerformancePlan e " +
                                                " where e.performancePlan = :performancePlan")
                                        .parameter("performancePlan", performancePlanDc.getItem())
                                        .view("assignedPerformancePlan.browse")
                                        .list();
                        if (!assignedPerformancePlanList.isEmpty()) {
                            for (AssignedPerformancePlan item : assignedPerformancePlanList) {
                                if (item.getAssignedPerson().equals(personExt.getGroup())) {
                                    isNew = false;
                                    break;
                                }
                            }
                        }
                        if (isNew) {
                            AssignedPerformancePlan assignedPerformancePlan = metadata
                                    .create(AssignedPerformancePlan.class);
                            assignedPerformancePlan.setPerformancePlan(performancePlanDc.getItem());
                            assignedPerformancePlan.setAssignedPerson(personExt.getGroup());
                            assignedPerformancePlan.setStepStageStatus(CardStatusEnum.DRAFT);
                            assignedPerformancePlan.setStartDate(performancePlanDc.getItem().getStartDate());
                            assignedPerformancePlan.setEndDate(performancePlanDc.getItem().getEndDate());
                            commitContext.addInstanceToCommit(assignedPerformancePlan);
                        }
                    });
                    dataManager.commit(commitContext);
                    assignedPerformancePlansDl.load();
                })
                .build().show();
    }

    @Subscribe("startDate")
    protected void onStartDateValueChange(HasValue.ValueChangeEvent<Date> event) {
        Date accessibilityStartDate = performancePlanDc.getItem().getAccessibilityStartDate();
        if (accessibilityStartDate != null && event.getValue() != null
                && accessibilityStartDate.before(event.getValue())) {
            notifications.create().withPosition(Notifications.Position.BOTTOM_RIGHT)
                    .withCaption(messageBundle.getMessage("accessStartDateNotBeEarlier")).show();
        }
    }

    @Subscribe("accessibilityStartDate")
    protected void onAccessibilityStartDateValueChange(HasValue.ValueChangeEvent<Date> event) {
        Date startDate = performancePlanDc.getItem().getStartDate();
        if (startDate != null && event.getValue() != null
                && event.getValue().before(startDate)) {
            notifications.create().withPosition(Notifications.Position.BOTTOM_RIGHT)
                    .withCaption(messageBundle.getMessage("accessStartDateNotBeEarlier")).show();
        }
    }

    @Subscribe("assignedPerformancePlanTable.massGoals")
    protected void onAssignedPerformancePlanTableMassGoals(Action.ActionPerformedEvent event) {
        Set<AssignedPerformancePlan> assignedPerformancePlans = assignedPerformancePlanTable.getSelected();
        CommitContext commitContext = new CommitContext();
        try {
            assignedPerformancePlans.forEach(assignedPerformancePlan -> {
                AssignmentExt currentAssignment = assignedPerformancePlan.getAssignedPerson().getCurrentAssignment();
                List<PositionGroupGoalLink> positionGoalList = currentAssignment.getPositionGroup() != null
                        ? currentAssignment.getPositionGroup().getGoals()
                        : Collections.emptyList();
                List<JobGroupGoalLink> jobGoalList = currentAssignment.getJobGroup() != null
                        ? currentAssignment.getJobGroup().getGoals()
                        : Collections.emptyList();
                List<OrganizationGroupGoalLink> orgGoalList = currentAssignment.getOrganizationGroup() != null
                        ? currentAssignment.getOrganizationGroup().getGoals()
                        : Collections.emptyList();
                List<OrganizationGroupGoalLink> orgStructureGoal =
                        kpiService.getHierarchyGoals(currentAssignment.getOrganizationGroup() != null
                                ? currentAssignment.getOrganizationGroup().getId()
                                : null);
                Set<Goal> processedGoals = new HashSet<>();
                for (PositionGroupGoalLink positionGoal : positionGoalList) {
                    AssignedGoal newAssignedGoal = metadata.create(AssignedGoal.class);
                    newAssignedGoal.setAssignedPerformancePlan(assignedPerformancePlan);
                    newAssignedGoal.setGoal(positionGoal.getGoal());
                    newAssignedGoal.setGoalString(positionGoal.getGoal().getGoalName());
                    newAssignedGoal.setWeight(Double.valueOf(positionGoal.getWeight()));
                    newAssignedGoal.setPositionGroup(positionGoal.getPositionGroup());
                    newAssignedGoal.setCategory(positionGoal.getGoal() != null
                            && positionGoal.getGoal().getLibrary() != null
                            ? positionGoal.getGoal().getLibrary().getCategory()
                            : null);
                    newAssignedGoal.setGoalType(AssignedGoalTypeEnum.LIBRARY);
                    newAssignedGoal.setGoalLibrary(positionGoal.getGoal().getLibrary());
                    commitContext.addInstanceToCommit(newAssignedGoal);
                    processedGoals.add(positionGoal.getGoal());
                }
                for (JobGroupGoalLink jobGoal : jobGoalList) {
                    if (processedGoals.contains(jobGoal.getGoal())) continue;

                    AssignedGoal newAssignedGoal = metadata.create(AssignedGoal.class);
                    newAssignedGoal.setAssignedPerformancePlan(assignedPerformancePlan);
                    newAssignedGoal.setGoal(jobGoal.getGoal());
                    newAssignedGoal.setGoalString(jobGoal.getGoal().getGoalName());
                    newAssignedGoal.setWeight(Double.valueOf(jobGoal.getWeight()));
                    newAssignedGoal.setJobGroup(jobGoal.getJobGroup());
                    newAssignedGoal.setCategory(jobGoal.getGoal() != null
                            && jobGoal.getGoal().getLibrary() != null
                            ? jobGoal.getGoal().getLibrary().getCategory()
                            : null);
                    newAssignedGoal.setGoalType(AssignedGoalTypeEnum.LIBRARY);
                    newAssignedGoal.setGoalLibrary(jobGoal.getGoal().getLibrary());
                    commitContext.addInstanceToCommit(newAssignedGoal);
                    processedGoals.add(jobGoal.getGoal());
                }
                for (OrganizationGroupGoalLink organizationGoal : orgGoalList) {
                    if (processedGoals.contains(organizationGoal.getGoal())) continue;

                    AssignedGoal newAssignedGoal = metadata.create(AssignedGoal.class);
                    newAssignedGoal.setAssignedPerformancePlan(assignedPerformancePlan);
                    newAssignedGoal.setGoal(organizationGoal.getGoal());
                    newAssignedGoal.setGoalString(organizationGoal.getGoal().getGoalName());
                    newAssignedGoal.setWeight(Double.valueOf(organizationGoal.getWeight()));
                    newAssignedGoal.setOrganizationGroup(organizationGoal.getOrganizationGroup());
                    newAssignedGoal.setCategory(organizationGoal.getGoal() != null
                            && organizationGoal.getGoal().getLibrary() != null
                            ? organizationGoal.getGoal().getLibrary().getCategory()
                            : null);
                    newAssignedGoal.setGoalType(AssignedGoalTypeEnum.LIBRARY);
                    newAssignedGoal.setGoalLibrary(organizationGoal.getGoal().getLibrary());
                    commitContext.addInstanceToCommit(newAssignedGoal);
                    processedGoals.add(organizationGoal.getGoal());
                }
                for (OrganizationGroupGoalLink structureGoal : orgStructureGoal) {
                    structureGoal = dataManager.reload(structureGoal, "organizationGroupGoalLink-view");

                    if (processedGoals.contains(structureGoal.getGoal())) continue;

                    AssignedGoal newAssignedGoal = metadata.create(AssignedGoal.class);
                    newAssignedGoal.setAssignedPerformancePlan(assignedPerformancePlan);
                    newAssignedGoal.setGoal(structureGoal.getGoal());
                    newAssignedGoal.setGoalString(structureGoal.getGoal().getGoalName());
                    newAssignedGoal.setWeight(Double.valueOf(structureGoal.getWeight()));
                    newAssignedGoal.setOrganizationGroup(structureGoal.getOrganizationGroup());
                    newAssignedGoal.setCategory(structureGoal.getGoal() != null
                            && structureGoal.getGoal().getLibrary() != null
                            ? structureGoal.getGoal().getLibrary().getCategory()
                            : null);
                    newAssignedGoal.setGoalType(AssignedGoalTypeEnum.LIBRARY);
                    newAssignedGoal.setGoalLibrary(structureGoal.getGoal().getLibrary());
                    commitContext.addInstanceToCommit(newAssignedGoal);
                    processedGoals.add(structureGoal.getGoal());
                }
            });
            dataManager.commit(commitContext);
            notifications.create().withPosition(Notifications.Position.BOTTOM_RIGHT)
                    .withCaption(messageBundle.getMessage("addMassGoalSuccess")).show();
        } catch (Exception e) {
            e.printStackTrace();
            notifications.create().withPosition(Notifications.Position.BOTTOM_RIGHT)
                    .withCaption(messageBundle.getMessage("addMassGoalNotSuccess")).show();
        }
    }

    @Subscribe("scoreSettingTable.create")
    protected void onScoreSettingTableCreate(Action.ActionPerformedEvent event) {
        screenBuilders.editor(scoreSettingTable)
                .newEntity()
                .withInitializer(scoreSetting -> scoreSetting.setPerformancePlan(performancePlanDc.getItem()))
                .build().show()
                .addAfterCloseListener(afterCloseEvent -> scoreSettingDl.load());
    }

    @Subscribe("assignedPerformancePlanTable.calculateGZP")
    protected void onAssignedPerformancePlanTableCalculateGZP(Action.ActionPerformedEvent event) {
        Set<AssignedPerformancePlan> assignedPerformancePlans = assignedPerformancePlanTable.getSelected();
        CommitContext commitContext = new CommitContext();
        try {
            assignedPerformancePlans.forEach(assignedPerformancePlan -> {
                AssignmentExt currentAssignment = assignedPerformancePlan.getAssignedPerson() != null
                        ? assignedPerformancePlan.getAssignedPerson().getCurrentAssignment()
                        : null;
                assignedPerformancePlan.setGzp(
                        extAppPropertiesConfig.getIncludeAbsence()
                                ? kpiService.calculationOfGzpWithAbsences(currentAssignment != null
                                        ? currentAssignment.getPersonGroup()
                                        : null,
                                performancePlanDc.getItem().getStartDate(),
                                performancePlanDc.getItem().getEndDate())
                                : BigDecimal.valueOf(kpiService.calculationOfGZP(currentAssignment != null
                                        ? currentAssignment.getGroup()
                                        : null,
                                performancePlanDc.getItem().getStartDate(), performancePlanDc.getItem().getEndDate())));
                assignedPerformancePlan.setMaxBonus(assignedPerformancePlan.getGzp().multiply(
                        BigDecimal.valueOf(assignedPerformancePlan.getMaxBonusPercent() != null
                                ? assignedPerformancePlan.getMaxBonusPercent()
                                : currentAssignment != null
                                && currentAssignment.getGradeGroup() != null
                                && currentAssignment.getGradeGroup().getGrade() != null
                                ? currentAssignment.getGradeGroup().getGrade().getBonusPercent()
                                : 0)).divide(BigDecimal.valueOf(100)));
                assignedPerformancePlan.setKpiScore(getFinalScore(assignedPerformancePlan.getResult()));
                assignedPerformancePlan.setFinalScore(assignedPerformancePlan.getKpiScore()
                        + (assignedPerformancePlan.getExtraPoint() != null
                        ? assignedPerformancePlan.getExtraPoint()
                        : 0.0));
                assignedPerformancePlan.setCompanyBonus(calculateCompanyBonus(assignedPerformancePlan.getMaxBonus(),
                        currentAssignment).doubleValue());
                assignedPerformancePlan.setPersonalBonus(calculatePersonalBonus(assignedPerformancePlan.getMaxBonus()
                        , assignedPerformancePlan.getFinalScore()));
                assignedPerformancePlan.setFinalBonus(assignedPerformancePlan.getCompanyBonus()
                        + assignedPerformancePlan.getPersonalBonus());
                commitContext.addInstanceToCommit(assignedPerformancePlan);
            });
            dataManager.commit(commitContext);
            assignedPerformancePlansDl.load();
            notifications.create().withPosition(Notifications.Position.BOTTOM_RIGHT)
                    .withCaption(messageBundle.getMessage("calculatedSuccessfully")).show();
        } catch (Exception ignored) {
        }
    }

    private Double calculatePersonalBonus(BigDecimal maxBonus, Double finalScore) {
        return maxBonus.doubleValue() *
                ((100 - correctionCoefDc.getItems().get(0).getGroupEfficiencyPercent()) / 100) * (finalScore
                / extAppPropertiesConfig.getIndividualScore());
    }

    private BigDecimal calculateCompanyBonus(BigDecimal maxBonus, AssignmentExt currentAssignment) {
        DicCompany currentCompany = currentAssignment.getOrganizationGroup() != null
                ? currentAssignment.getOrganizationGroup().getCompany()
                : null;
        CorrectionCoefficient correctionCoefficient = correctionCoefDc.getItems().stream()
                .filter(correctionCoefficient1 -> correctionCoefficient1.getCompany() != null
                        && correctionCoefficient1.getCompany().equals(currentCompany))
                .findFirst().orElse(null);
        if (correctionCoefficient != null) {
            return maxBonus.multiply(BigDecimal.valueOf(correctionCoefficient.getGroupEfficiencyPercent())
                    .divide(BigDecimal.valueOf(100)))
                    .multiply(BigDecimal.valueOf(correctionCoefficient.getCompanyResult())
                            .divide(BigDecimal.valueOf(100)));
        }
//        else {
//            notifications.create().withPosition(Notifications.Position.BOTTOM_RIGHT)
//                    .withCaption(messageBundle.getMessage("correctionCoefIsNull")).show();
//            assignedPerformancePlansDl.load();
//        }
        return BigDecimal.ZERO;
    }

    private Double getFinalScore(Double result) {
        List<ScoreSetting> scoreSettingList = dataManager.load(ScoreSetting.class)
                .query("select e from tsadv_ScoreSetting e " +
                        " where :result between e.minPercent and e.maxPercent " +
                        " and e.performancePlan = :performancePlan")
                .parameter("result", result)
                .parameter("performancePlan", performancePlanDc.getItem())
                .list();
        if (!scoreSettingList.isEmpty()) {
            return Double.valueOf(scoreSettingList.get(0).getFinalScore());
        }
//        else {
//            notifications.create().withPosition(Notifications.Position.BOTTOM_RIGHT)
//                    .withCaption(messageBundle.getMessage("notScoreSetting")).show();
//            assignedPerformancePlansDl.load();
//        }
        return 0.0;
    }

    @Subscribe("correctionCoefTable.create")
    protected void onCorrectionCoefTableCreate(Action.ActionPerformedEvent event) {
        screenBuilders.editor(correctionCoefTable)
                .newEntity()
                .withInitializer(correctionCoefficient ->
                        correctionCoefficient.setPerformancePlan(performancePlanDc.getItem()))
                .build().show()
                .addAfterCloseListener(afterCloseEvent -> correctionCoefDl.load());
    }

    @Subscribe("assignedPerformancePlanTable.importExcel")
    protected void onAssignedPerformancePlanTableImportExcel(Action.ActionPerformedEvent event) {
        FileUploadDialog dialog = (FileUploadDialog) screens.create("fileUploadDialog", OpenMode.DIALOG);
        dialog.addAfterCloseListener((d) -> {
            UUID fileId = dialog.getFileId();

            String fileName = dialog.getFileName();
            int indexOfDot = 0;
            String fileFormat = "";
            if (fileId != null) {
                indexOfDot = fileName.indexOf(".");
                fileFormat = fileName.substring(indexOfDot + 1);
                File file = fileUploadingAPI.getFile(fileId);
//             Read XSL file
                HSSFWorkbook hssfWorkbook = null;
                XSSFWorkbook xssfWorkbook = null;
                try {
                    assert file != null;
                    inputStream = new FileInputStream(file.getAbsolutePath());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                if (fileFormat.equals("xls")) {
                    try {
                        xls(hssfWorkbook);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (fileFormat.equals("xlsx")) {
                    try {
                        xlsx(xssfWorkbook);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    notifications.create().withCaption(messageBundle.getMessage("incorrectFormat")).show();
                }
            }
        });
        screens.show(dialog);
    }

    private void xls(HSSFWorkbook hssfWorkbook) throws IOException {
        // Get the workbook instance for XLS file
        try {
            hssfWorkbook = new HSSFWorkbook(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Get first sheet from the workbook
        assert hssfWorkbook != null;
        HSSFSheet sheet = hssfWorkbook.getSheetAt(0);

        String assignedPerformancePlanId = String.valueOf(sheet.getRow(1).getCell(0).getColumnIndex());
        int adjustedScore = sheet.getRow(1).getCell(12).getColumnIndex();
        int adjustedBonus = sheet.getRow(1).getCell(13).getColumnIndex();
        int rowCount = sheet.getLastRowNum();

        for (int i = 1; i <= rowCount; i++) {
            HSSFRow row = sheet.getRow(i);
            try {
                if (row.getCell(Integer.parseInt(assignedPerformancePlanId)) != null
                        && (row.getCell(adjustedScore).getNumericCellValue() > 0
                        || row.getCell(adjustedBonus).getNumericCellValue() > 0)) {
                    boolean isTrue = addToBase(row.getCell(Integer.parseInt(assignedPerformancePlanId)).getStringCellValue(),
                            row.getCell(adjustedScore).getNumericCellValue(),
                            row.getCell(adjustedBonus).getNumericCellValue());
                    Cell statusCell = createStatusCell(row);
                    if (isTrue) {
                        statusCell.setCellValue("OK");
                    } else {
                        statusCell.setCellValue(messageBundle.getMessage("xlsError")
                                + messageBundle.getMessage("assignedPerformancePlanNotFound"));
                    }
                }
            } catch (Exception e) {
                Cell statusCell = createStatusCell(row);
                statusCell.setCellValue(messageBundle.getMessage("xlsError") + e.getMessage());
            }
        }
        assignedPerformancePlansDl.load();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            hssfWorkbook.write(bos);
        } finally {
            bos.close();
        }
        byte[] bytes = bos.toByteArray();
        exportDisplay.show(new ByteArrayDataProvider(bytes), "KPI CARD", ExportFormat.XLS);
        hssfWorkbook.close();
    }

    private HSSFCell createStatusCell(HSSFRow row) {
        return row.getCell(14) != null
                ? row.getCell(14)
                : row.createCell(14);
    }

    private void xlsx(XSSFWorkbook xssfWorkbook) throws IOException {
        // Get the workbook instance for XLS file
        try {
            xssfWorkbook = new XSSFWorkbook(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Get first sheet from the workbook
        assert xssfWorkbook != null;
        XSSFSheet sheet = xssfWorkbook.getSheetAt(0);

        String assignedPerformancePlanId = String.valueOf(sheet.getRow(1).getCell(0).getColumnIndex());
        int adjustedScore = sheet.getRow(1).getCell(12).getColumnIndex();
        int adjustedBonus = sheet.getRow(1).getCell(13).getColumnIndex();
        int rowCount = sheet.getLastRowNum();

        for (int i = 1; i <= rowCount; i++) {
            XSSFRow row = sheet.getRow(i);
            try {
                if (row.getCell(Integer.parseInt(assignedPerformancePlanId)) != null
                        && (row.getCell(adjustedScore).getNumericCellValue() > 0
                        || row.getCell(adjustedBonus).getNumericCellValue() > 0)) {
                    boolean isTrue = addToBase(row.getCell(Integer.parseInt(assignedPerformancePlanId)).getStringCellValue(),
                            row.getCell(adjustedScore).getNumericCellValue(),
                            row.getCell(adjustedBonus).getNumericCellValue());
                    Cell statusCell = createStatusCell(row);
                    if (isTrue) {
                        statusCell.setCellValue("OK");
                    } else {
                        statusCell.setCellValue(messageBundle.getMessage("xlsError")
                                + messageBundle.getMessage("assignedPerformancePlanNotFound"));
                    }
                }
            } catch (Exception e) {
                Cell statusCell = createStatusCell(row);
                statusCell.setCellValue(messageBundle.getMessage("xlsError") + e.getMessage());
            }
        }
        assignedPerformancePlansDl.load();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            xssfWorkbook.write(bos);
        } finally {
            bos.close();
        }
        byte[] bytes = bos.toByteArray();
        exportDisplay.show(new ByteArrayDataProvider(bytes), "KPI CARD", ExportFormat.XLS);
        xssfWorkbook.close();
    }

    private Cell createStatusCell(XSSFRow row) {
        return row.getCell(14) != null
                ? row.getCell(14)
                : row.createCell(14);
    }

    private Boolean addToBase(String assignedPerformancePlanId, double adjustedScore,
                              double adjustedBonus) {
        AssignedPerformancePlan assignedPerformancePlan = dataManager.load(AssignedPerformancePlan.class)
                .query("select e from tsadv$AssignedPerformancePlan e " +
                        " where e.performancePlan = :performancePlan " +
                        " and e.id = :assignedPerformancePlanId ")
                .parameter("performancePlan", performancePlanDc.getItem())
                .parameter("assignedPerformancePlanId", UUID.fromString(assignedPerformancePlanId))
                .list().stream().findFirst().orElse(null);
        if (assignedPerformancePlan != null) {
            assignedPerformancePlan.setAdjustedScore(adjustedScore);
            assignedPerformancePlan.setAdjustedBonus(adjustedBonus);
            dataManager.commit(assignedPerformancePlan);
            return true;
        }
        return false;
    }

    @Subscribe("assignedPerformancePlanTable.exportExcel")
    protected void onAssignedPerformancePlanTableExportExcel(Action.ActionPerformedEvent event) {
        Report report = dataManager.load(LoadContext.create(Report.class)
                .setQuery(LoadContext.createQuery("select e from report$Report e where e.code = 'KPI'")));
        reportGuiManager.printReport(report,
                ParamsMap.of("id", assignedPerformancePlanTable.getSelected()));
    }

    @Subscribe("assignedPerformancePlanTable.sendLetterHappiness")
    protected void onAssignedPerformancePlanTableSendLetterHappiness(Action.ActionPerformedEvent event) {
        for (AssignedPerformancePlan assignedPerformancePlan : assignedPerformancePlanTable.getSelected()) {
            TsadvUser tsadvUser = dataManager.load(TsadvUser.class)
                    .query("select e from tsadv$UserExt e " +
                            " where e.personGroup = :personGroup")
                    .parameter("personGroup", assignedPerformancePlan.getAssignedPerson())
                    .view("userExt.edit")
                    .list().stream().findFirst().orElse(null);
            if (tsadvUser != null) {
                Map<String, Object> params = new HashMap<>();

                RuleBasedNumberFormat nfRu = getAmountINText("ru");
                RuleBasedNumberFormat nfEn = getAmountINText("en");

                params.put("fullNameRu", assignedPerformancePlan.getAssignedPerson().getPerson().getFirstName()
                        + " "
                        + assignedPerformancePlan.getAssignedPerson().getPerson().getLastName());
                params.put("middleNameRu", assignedPerformancePlan.getAssignedPerson().getPerson().getMiddleName() != null
                        && !assignedPerformancePlan.getAssignedPerson().getPerson().getMiddleName().isEmpty()
                        ? assignedPerformancePlan.getAssignedPerson().getPerson().getMiddleName()
                        : "");
                params.put("fullNameEn", assignedPerformancePlan.getAssignedPerson().getPerson().getFirstNameLatin()
                        + " "
                        + assignedPerformancePlan.getAssignedPerson().getPerson().getLastNameLatin());
                params.put("middleNameEn", assignedPerformancePlan.getAssignedPerson().getPerson().getMiddleNameLatin() != null
                        && !assignedPerformancePlan.getAssignedPerson().getPerson().getMiddleNameLatin().isEmpty()
                        ? assignedPerformancePlan.getAssignedPerson().getPerson().getMiddleNameLatin()
                        : "");
                params.put("firstNameRu", assignedPerformancePlan.getAssignedPerson().getPerson().getFirstName());
                params.put("firstNameEn", assignedPerformancePlan.getAssignedPerson().getPerson().getFirstNameLatin());
                params.put("finalBonus", assignedPerformancePlan.getAdjustedBonus() != null
                        && assignedPerformancePlan.getAdjustedBonus() > 0
                        ? assignedPerformancePlan.getAdjustedBonus()
                        : assignedPerformancePlan.getFinalBonus() != null
                        && assignedPerformancePlan.getFinalBonus() > 0
                        ? assignedPerformancePlan.getFinalBonus()
                        : "");
                DicCurrency currency = getSalaryCurrency(assignedPerformancePlan.getAssignedPerson().getCurrentAssignment());
                params.put("currency", currency != null
                        && currency.getCode() != null
                        && !currency.getCode().isEmpty()
                        ? currency.getCode()
                        : "");
                params.put("amountInTextRu", assignedPerformancePlan.getAdjustedBonus() != null
                        && assignedPerformancePlan.getAdjustedBonus() > 0
                        ? nfRu.format(assignedPerformancePlan.getAdjustedBonus())
                        : assignedPerformancePlan.getFinalBonus() != null
                        && assignedPerformancePlan.getFinalBonus() > 0
                        ? nfRu.format(assignedPerformancePlan.getFinalBonus())
                        : "");
                params.put("amountInTextEn", assignedPerformancePlan.getAdjustedBonus() != null
                        && assignedPerformancePlan.getAdjustedBonus() > 0
                        ? nfEn.format(assignedPerformancePlan.getAdjustedBonus())
                        : assignedPerformancePlan.getFinalBonus() != null
                        && assignedPerformancePlan.getFinalBonus() > 0
                        ? nfEn.format(assignedPerformancePlan.getFinalBonus())
                        : "");
                params.put("year", yearFormat.format(assignedPerformancePlan.getPerformancePlan().getStartDate()));
                params.put("nextYear", yearFormat.format(DateUtils.addYears(assignedPerformancePlan.getPerformancePlan().getStartDate(), 1)));
                params.put("currentDateRu", monthTextFormatRu.format(BaseCommonUtils.getSystemDate()));
                params.put("currentDateEn", monthTextFormatEn.format(BaseCommonUtils.getSystemDate()));
                params.put("monthYearRu", assignedPerformancePlan.getPerformancePlan().getAccessibilityEndDate() != null
                        ? monthYearTextFormatRu.format(assignedPerformancePlan.getPerformancePlan().getAccessibilityEndDate())
                        : "");
                params.put("monthYearEn", assignedPerformancePlan.getPerformancePlan().getAccessibilityEndDate() != null
                        ? monthYearTextFormatEn.format(assignedPerformancePlan.getPerformancePlan().getAccessibilityEndDate())
                        : "");
                CorrectionCoefficient correctionCoefficient = getSigner(assignedPerformancePlan.getPerformancePlan(),
                        assignedPerformancePlan.getAssignedPerson());
                params.put("signerRu", correctionCoefficient != null
                        && correctionCoefficient.getFullName() != null
                        && !correctionCoefficient.getFullName().isEmpty()
                        ? correctionCoefficient.getFullName()
                        : "");
                params.put("jobRu", correctionCoefficient != null
                        && correctionCoefficient.getJobText() != null
                        && !correctionCoefficient.getJobText().isEmpty()
                        ? correctionCoefficient.getJobText()
                        : "");
                params.put("signerEn", correctionCoefficient != null
                        && correctionCoefficient.getFullNameEn() != null
                        && !correctionCoefficient.getFullNameEn().isEmpty()
                        ? correctionCoefficient.getFullNameEn()
                        : "");
                params.put("jobEn", correctionCoefficient != null
                        && correctionCoefficient.getJobTextEn() != null
                        && !correctionCoefficient.getJobTextEn().isEmpty()
                        ? correctionCoefficient.getJobTextEn()
                        : "");
                notificationService.sendParametrizedNotification("kpi.letter.of.happiness", tsadvUser, params);
            }
        }
    }

    protected DicCurrency getSalaryCurrency(AssignmentExt currentAssignment) {
        return dataManager.load(DicCurrency.class)
                .query("select e.currency from tsadv$Salary e " +
                        " where e.assignmentGroup = :assignmentGroup " +
                        " and current_date between e.startDate and e.endDate")
                .parameter("assignmentGroup", currentAssignment.getGroup())
                .view("")
                .list().stream().findFirst().orElse(null);
    }

    protected CorrectionCoefficient getSigner(PerformancePlan performancePlan, PersonGroupExt personGroupExt) {
        return dataManager.load(CorrectionCoefficient.class)
                .query("select e from tsadv_CorrectionCoefficient e " +
                        " where e.performancePlan = :performancePlan" +
                        " and e.company = :company ")
                .parameter("performancePlan", performancePlan)
                .parameter("company", personGroupExt.getCompany())
                .view("correctionCoefficient.edit")
                .list().stream().findFirst().orElse(null);
    }

    protected RuleBasedNumberFormat getAmountINText(String language) {
        return new RuleBasedNumberFormat(Locale.forLanguageTag(language),
                RuleBasedNumberFormat.SPELLOUT);
    }

    @Subscribe("assignedPerformancePlanTable.startLetters")
    protected void onAssignedPerformancePlanTableStartLetters(Action.ActionPerformedEvent event) {
        dialogs.createOptionDialog()
                .withCaption(messageBundle.getMessage("confirmation"))
                .withMessage(messageBundle.getMessage("aUSure"))
                .withType(Dialogs.MessageType.CONFIRMATION)
                .withActions(
                        new DialogAction(DialogAction.Type.YES)
                                .withHandler(actionPerformedEvent -> {
                                            for (AssignedPerformancePlan assignedPerformancePlan : assignedPerformancePlanTable.getSelected()) {
                                                TsadvUser tsadvUser = dataManager.load(TsadvUser.class)
                                                        .query("select e from tsadv$UserExt e " +
                                                                " where e.personGroup = :personGroup")
                                                        .parameter("personGroup", assignedPerformancePlan.getAssignedPerson())
                                                        .view("userExt.edit")
                                                        .list().stream().findFirst().orElse(null);
                                                if (tsadvUser != null) {
                                                    Map<String, Object> params = new HashMap<>();
                                                    params.put("fullNameRu", assignedPerformancePlan.getAssignedPerson().getPerson().getFirstName()
                                                            + " "
                                                            + assignedPerformancePlan.getAssignedPerson().getPerson().getLastName());
                                                    params.put("middleNameRu", assignedPerformancePlan.getAssignedPerson().getPerson().getMiddleName() != null
                                                            && !assignedPerformancePlan.getAssignedPerson().getPerson().getMiddleName().isEmpty()
                                                            ? assignedPerformancePlan.getAssignedPerson().getPerson().getMiddleName()
                                                            : "");
                                                    String requestLink = "<a href=\"" + globalConfig.getWebAppUrl() + "/open?screen=" +
                                                            "tsadv$PerformancePlan.edit" +
                                                            "&item=" + "tsadv$PerformancePlan" + "-" + assignedPerformancePlan.getPerformancePlan().getId() +
                                                            "\" target=\"_blank\">%s " + "</a>";
                                                    params.put("requestLink", requestLink);
                                                    notificationService.sendParametrizedNotification("kpi.start.letter", tsadvUser, params);
                                                }
                                            }
                                        }
                                ),
                        new DialogAction(DialogAction.Type.NO))
                .show();
    }
}