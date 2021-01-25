package kz.uco.tsadv.web.modules.performance.performanceplan;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.*;
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
import kz.uco.base.common.BaseCommonUtils;
import kz.uco.base.entity.dictionary.DicCompany;
import kz.uco.tsadv.config.ExtAppPropertiesConfig;
import kz.uco.tsadv.modules.performance.enums.AssignedGoalTypeEnum;
import kz.uco.tsadv.modules.performance.enums.CardStatusEnum;
import kz.uco.tsadv.modules.performance.model.*;
import kz.uco.tsadv.modules.personal.model.*;
import kz.uco.tsadv.service.KpiService;
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
    private FileInputStream inputStream;

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
                        for (AssignedPerformancePlan item : assignedPerformancePlansDc.getItems()) {
                            if (item.getAssignedPerson().equals(personExt.getGroup())) {
                                isNew = false;
                                break;
                            }
                        }
                        if (isNew) {
                            AssignedPerformancePlan assignedPerformancePlan = metadata
                                    .create(AssignedPerformancePlan.class);
                            assignedPerformancePlan.setPerformancePlan(performancePlanDc.getItem());
                            assignedPerformancePlan.setAssignedPerson(personExt.getGroup());
                            assignedPerformancePlan.setStatus(CardStatusEnum.DRAFT);
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
                List<OrganizationGroupGoalLink> orgGoalList = currentAssignment.getOrganizationGroup() != null
                        ? currentAssignment.getOrganizationGroup().getGoals()
                        : Collections.emptyList();
                List<PositionGroupGoalLink> positionGoalList = currentAssignment.getPositionGroup() != null
                        ? currentAssignment.getPositionGroup().getGoals()
                        : Collections.emptyList();
                List<JobGroupGoalLink> jobGoalList = currentAssignment.getJobGroup() != null
                        ? currentAssignment.getJobGroup().getGoals()
                        : Collections.emptyList();
                for (OrganizationGroupGoalLink organizationGoal : orgGoalList) {
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
                }
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
                }
                for (JobGroupGoalLink jobGoal : jobGoalList) {
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
                }
            });
            dataManager.commit(commitContext);
            notifications.create().withPosition(Notifications.Position.BOTTOM_RIGHT)
                    .withCaption(messageBundle.getMessage("addMassGoalSuccess")).show();
        } catch (Exception e) {
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
                assignedPerformancePlan.setGzp(BigDecimal.valueOf(
                        kpiService.calculationOfGZP(currentAssignment != null
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
        } else {
            notifications.create().withPosition(Notifications.Position.BOTTOM_RIGHT)
                    .withCaption(messageBundle.getMessage("correctionCoefIsNull")).show();
            assignedPerformancePlansDl.load();
        }
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
        } else {
            notifications.create().withPosition(Notifications.Position.BOTTOM_RIGHT)
                    .withCaption(messageBundle.getMessage("notScoreSetting")).show();
            assignedPerformancePlansDl.load();
        }
        return null;
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
        int adjustedScore = sheet.getRow(1).getCell(10).getColumnIndex();
        int adjustedBonus = sheet.getRow(1).getCell(11).getColumnIndex();
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
        return row.getCell(12) != null
                ? row.getCell(12)
                : row.createCell(12);
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
        int adjustedScore = sheet.getRow(1).getCell(10).getColumnIndex();
        int adjustedBonus = sheet.getRow(1).getCell(11).getColumnIndex();
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
        return row.getCell(12) != null
                ? row.getCell(12)
                : row.createCell(12);
    }

    private Boolean addToBase(String assignedPerformancePlanId, double adjustedScore,
                              double adjustedBonus) {
        AssignedPerformancePlan assignedPerformancePlan = dataManager.load(AssignedPerformancePlan.class)
                .query("select e from tsadv$AssignedPerformancePlan e " +
                        " where e.id = :assignedPerformancePlanId ")
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
                ParamsMap.of("performancePlanId", performancePlanDc.getItem().getId()));
    }
}