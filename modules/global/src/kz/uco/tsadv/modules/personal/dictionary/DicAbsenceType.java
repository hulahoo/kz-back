package kz.uco.tsadv.modules.personal.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import kz.uco.tsadv.modules.personal.enums.VacationDurationType;

@NamePattern("%s %s|langValue,description")
@Table(name = "TSADV_DIC_ABSENCE_TYPE")
@Entity(name = "tsadv$DicAbsenceType")
public class DicAbsenceType extends AbstractDictionary {
    private static final long serialVersionUID = 3023064116187118104L;

    @Column(name = "USE_IN_SELF_SERVICE", nullable = false)
    protected Boolean useInSelfService = false;

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

}