package kz.uco.tsadv.modules.personal.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;
import kz.uco.tsadv.modules.personal.enums.VacationDurationType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@NamePattern("%s %s|langValue,description")
@Table(name = "TSADV_DIC_ABSENCE_TYPE")
@Entity(name = "tsadv$DicAbsenceType")
public class DicAbsenceType extends AbstractDictionary {
    private static final long serialVersionUID = 3023064116187118104L;

    @Column(name = "USE_IN_SELF_SERVICE", nullable = false)
    protected Boolean useInSelfService = false;

    @Column(name = "ECOLOGICAL_ABSENCE", nullable = false)
    @NotNull
    private Boolean isEcologicalAbsence = false;

    @Column(name = "AVAILABLE_TO_MANAGER")
    private Boolean availableToManager;

    @Column(name = "VACATION_DURATION_TYPE")
    protected String vacationDurationType;

    @NotNull
    @Column(name = "ELMA_TRANSFER", nullable = false)
    protected Boolean elmaTransfer = false;

    @NotNull
    @Column(name = "USE_IN_BALANCE", nullable = false)
    protected Boolean useInBalance = false;

    @NotNull
    @Column(name = "IGNORE_HOLIDAYS", nullable = false)
    protected Boolean ignoreHolidays = false;

    @Column(name = "IS_ONLY_WORKING_DAY")
    protected Boolean isOnlyWorkingDay = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ABSENCE_CATEGORY_ID")
    protected DicAbsenceCategory absenceCategory;

    @Column(name = "TIMESHEET_CODE")
    protected String timesheetCode;

    @NotNull
    @Column(name = "IS_WORKING_DAY", nullable = false)
    protected Boolean isWorkingDay = false;

    @Column(name = "USE_ONLY_ABSENCE_TYPE")
    protected Boolean useOnlyAbsenceType;

    @Column(name = "DISPLAY_ABSENCE")
    protected Boolean displayAbsence;

    @Column(name = "CANCEL_PARENT_ABSENCE")
    protected Boolean cancelParentAbsence;

    @NotNull
    @Column(name = "AVAILABLE_FOR_TIMECARD", nullable = false)
    protected Boolean availableForTimecard = false;

    @NotNull
    @Column(name = "IS_REQUIRED_ORDER_NUMBER", nullable = false)
    protected Boolean isRequiredOrderNumber = false;

    @NotNull
    @Column(name = "INCLUDE_CALC_GZP", nullable = false)
    protected Boolean includeCalcGzp = false;

    @Column(name = "MAX_DAY")
    private Integer maxDay;

    @Column(name = "MIN_DAY")
    private Integer minDay;

    @Column(name = "DAYS_ADVANCE")
    private Integer daysAdvance;

    @Column(name = "DAYS_BEFORE_ABSENCE")
    protected Integer daysBeforeAbsence;

    @Column(name = "MANY_DAYS")
    private Integer manyDays;

    @NotNull
    @Column(name = "AVAILABLE_FOR_RECALL_ABSENCE", nullable = false)
    protected Boolean availableForRecallAbsence = false;

    @NotNull
    @Column(name = "AVAILABLE_FOR_CHANGE_DATE", nullable = false)
    protected Boolean availableForChangeDate = false;

    @NotNull
    @Column(name = "AVAILABLE_FOR_LEAVING_VACATION", nullable = false)
    protected Boolean availableForLeavingVacation = false;

    @NotNull
    @Column(name = "IS_JUST_REQUIRED", nullable = false)
    protected Boolean isJustRequired = false;

    @NotNull
    @Column(name = "IS_ORIGINAL_SHEET", nullable = false)
    protected Boolean isOriginalSheet = false;

    @NotNull
    @Column(name = "IS_CHECK_WORK", nullable = false)
    protected Boolean isCheckWork = false;

    @NotNull
    @Column(name = "IS_VACATION_DATE", nullable = false)
    protected Boolean isVacationDate = false;

    @Column(name = "WORK_ON_WEEKEND")
    protected Boolean workOnWeekend;

    @Column(name = "TEMPORARY_TRANSFER")
    protected Boolean temporaryTransfer;

    @Column(name = "OVERTIME_WORK")
    protected Boolean overtimeWork;

    @Column(name = "NUM_DAYS_CALENDAR_YEAR")
    protected Integer numDaysCalendarYear;

    public void setIsEcologicalAbsence(Boolean isEcologicalAbsence) {
        this.isEcologicalAbsence = isEcologicalAbsence;
    }

    public Boolean getIsEcologicalAbsence() {
        return isEcologicalAbsence;
    }

    public Integer getNumDaysCalendarYear() {
        return numDaysCalendarYear;
    }

    public void setNumDaysCalendarYear(Integer numDaysCalendarYear) {
        this.numDaysCalendarYear = numDaysCalendarYear;
    }

    public Boolean getIsVacationDate() {
        return isVacationDate;
    }

    public void setIsVacationDate(Boolean isVacationDate) {
        this.isVacationDate = isVacationDate;
    }

    public Boolean getIsCheckWork() {
        return isCheckWork;
    }

    public void setIsCheckWork(Boolean isCheckWork) {
        this.isCheckWork = isCheckWork;
    }

    public Boolean getIsOriginalSheet() {
        return isOriginalSheet;
    }

    public void setIsOriginalSheet(Boolean isOriginalSheet) {
        this.isOriginalSheet = isOriginalSheet;
    }

    public Boolean getIsJustRequired() {
        return isJustRequired;
    }

    public void setIsJustRequired(Boolean isJustRequired) {
        this.isJustRequired = isJustRequired;
    }

    public Boolean getAvailableForLeavingVacation() {
        return availableForLeavingVacation;
    }

    public void setAvailableForLeavingVacation(Boolean availableForLeavingVacation) {
        this.availableForLeavingVacation = availableForLeavingVacation;
    }

    public Boolean getAvailableForChangeDate() {
        return availableForChangeDate;
    }

    public void setAvailableForChangeDate(Boolean availableForChangeDate) {
        this.availableForChangeDate = availableForChangeDate;
    }

    public Boolean getAvailableForRecallAbsence() {
        return availableForRecallAbsence;
    }

    public void setAvailableForRecallAbsence(Boolean availableForRecallAbsence) {
        this.availableForRecallAbsence = availableForRecallAbsence;
    }

    public Boolean getIncludeCalcGzp() {
        return includeCalcGzp;
    }

    public void setIncludeCalcGzp(Boolean includeCalcGzp) {
        this.includeCalcGzp = includeCalcGzp;
    }

    public Integer getDaysBeforeAbsence() {
        return daysBeforeAbsence;
    }

    public void setDaysBeforeAbsence(Integer daysBeforeAbsence) {
        this.daysBeforeAbsence = daysBeforeAbsence;
    }

    public void setManyDays(Integer manyDays) {
        this.manyDays = manyDays;
    }

    public Integer getManyDays() {
        return manyDays;
    }

    public Integer getDaysAdvance() {
        return daysAdvance;
    }

    public void setDaysAdvance(Integer daysAdvance) {
        this.daysAdvance = daysAdvance;
    }

    public Integer getMinDay() {
        return minDay;
    }

    public void setMinDay(Integer minDay) {
        this.minDay = minDay;
    }

    public Integer getMaxDay() {
        return maxDay;
    }

    public void setMaxDay(Integer maxDay) {
        this.maxDay = maxDay;
    }

    public Boolean getAvailableToManager() {
        return availableToManager;
    }

    public void setAvailableToManager(Boolean availableToManager) {
        this.availableToManager = availableToManager;
    }

    public void setVacationDurationType(VacationDurationType vacationDurationType) {
        this.vacationDurationType = vacationDurationType == null ? null : vacationDurationType.getId();
    }

    public VacationDurationType getVacationDurationType() {
        return vacationDurationType == null ? null : VacationDurationType.fromId(vacationDurationType);
    }

    public void setIsRequiredOrderNumber(Boolean isRequiredOrderNumber) {
        this.isRequiredOrderNumber = isRequiredOrderNumber;
    }

    public Boolean getIsRequiredOrderNumber() {
        return isRequiredOrderNumber;
    }

    public void setElmaTransfer(Boolean elmaTransfer) {
        this.elmaTransfer = elmaTransfer;
    }

    public Boolean getElmaTransfer() {
        return elmaTransfer;
    }

    public void setAvailableForTimecard(Boolean availableForTimecard) {
        this.availableForTimecard = availableForTimecard;
    }

    public Boolean getAvailableForTimecard() {
        return availableForTimecard;
    }

    public void setUseInBalance(Boolean useInBalance) {
        this.useInBalance = useInBalance;
    }

    public Boolean getUseInBalance() {
        return useInBalance;
    }

    public void setIgnoreHolidays(Boolean ignoreHolidays) {
        this.ignoreHolidays = ignoreHolidays;
    }

    public Boolean getIgnoreHolidays() {
        return ignoreHolidays;
    }

    public void setCancelParentAbsence(Boolean cancelParentAbsence) {
        this.cancelParentAbsence = cancelParentAbsence;
    }

    public Boolean getCancelParentAbsence() {
        return cancelParentAbsence;
    }

    public void setDisplayAbsence(Boolean displayAbsence) {
        this.displayAbsence = displayAbsence;
    }

    public Boolean getDisplayAbsence() {
        return displayAbsence;
    }

    public void setUseOnlyAbsenceType(Boolean useOnlyAbsenceType) {
        this.useOnlyAbsenceType = useOnlyAbsenceType;
    }

    public Boolean getUseOnlyAbsenceType() {
        return useOnlyAbsenceType;
    }

    public void setIsWorkingDay(Boolean isWorkingDay) {
        this.isWorkingDay = isWorkingDay;
    }

    public Boolean getIsWorkingDay() {
        return isWorkingDay;
    }

    public void setIsOnlyWorkingDay(Boolean isOnlyWorkingDay) {
        this.isOnlyWorkingDay = isOnlyWorkingDay;
    }

    public Boolean getIsOnlyWorkingDay() {
        return isOnlyWorkingDay;
    }

    public void setAbsenceCategory(DicAbsenceCategory absenceCategory) {
        this.absenceCategory = absenceCategory;
    }

    public DicAbsenceCategory getAbsenceCategory() {
        return absenceCategory;
    }

    public void setTimesheetCode(String timesheetCode) {
        this.timesheetCode = timesheetCode;
    }

    public String getTimesheetCode() {
        return timesheetCode;
    }

    public void setUseInSelfService(Boolean useInSelfService) {
        this.useInSelfService = useInSelfService;
    }

    public Boolean getUseInSelfService() {
        return useInSelfService;
    }

    public Boolean getWorkOnWeekend() {
        return workOnWeekend;
    }

    public void setWorkOnWeekend(Boolean workOnWeekend) {
        this.workOnWeekend = workOnWeekend;
    }

    public Boolean getTemporaryTransfer() {
        return temporaryTransfer;
    }

    public void setTemporaryTransfer(Boolean temporaryTransfer) {
        this.temporaryTransfer = temporaryTransfer;
    }

    public Boolean getOvertimeWork() {
        return overtimeWork;
    }

    public void setOvertimeWork(Boolean overtimeWork) {
        this.overtimeWork = overtimeWork;
    }
}