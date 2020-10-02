package kz.uco.tsadv.modules.timesheet.model;

import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.dictionary.DicPayroll;
import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;
import kz.uco.tsadv.modules.timesheet.enums.TimecardHeaderStatusEnum;
import kz.uco.tsadv.modules.timesheet.enums.TimecardHeaderTypeEnum;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Table(name = "TSADV_TIMECARD_HEADER")
@Entity(name = "tsadv$TimecardHeader")
public class TimecardHeader extends AbstractParentEntity {
    private static final long serialVersionUID = -7231820392641535539L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ASSIGNMENT_SCHEDULE_ID")
    protected AssignmentSchedule assignmentSchedule;

    @OneToMany(mappedBy = "timecardHeader")
    protected List<WorkedHoursSummary> workedHoursSummaries;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ASSIGNMENT_GROUP_ID")
    protected AssignmentGroupExt assignmentGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PAYROLL_ID")
    protected DicPayroll payroll;

    @Temporal(TemporalType.DATE)
    @Column(name = "PERIOD_START_DATE")
    protected Date periodStartDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "PERIOD_END_DATE")
    protected Date periodEndDate;


    @NotNull
    @Temporal(TemporalType.DATE)
    @Column(name = "EFFECTIVE_START_DATE", nullable = false)
    protected Date effectiveStartDate;

    @NotNull
    @Temporal(TemporalType.DATE)
    @Column(name = "EFFECTIVE_END_DATE", nullable = false)
    protected Date effectiveEndDate;

    @Column(name = "DOCUMENT_VERSION")
    protected Integer documentVersion;

    @Column(name = "STATUS")
    protected Integer status;

    @Column(name = "TYPE_")
    protected String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TIMECARD_CORRECTION_ID")
    protected TimecardCorrection timecardCorrection;

    @NotNull
    @Column(name = "BASE_WORK_HOURS", nullable = false)
    protected Double baseWorkHours;

    @NotNull
    @Column(name = "BASE_WORK_DAYS", nullable = false)
    protected Integer baseWorkDays;

    @NotNull
    @Column(name = "PLAN_WORK_DAYS", nullable = false)
    protected Integer planWorkDays;

    @NotNull
    @Column(name = "PLAN_WORK_HOURS", nullable = false)
    protected Double planWorkHours;

    @NotNull
    @Column(name = "PLAN_WORK_HOURS_PART", nullable = false)
    protected Double planWorkHoursPart;

    @NotNull
    @Column(name = "FACT_WORK_DAYS", nullable = false)
    protected Integer factWorkDays;

    @NotNull
    @Column(name = "FACT_HOURS_WITHOUT_OVERTIME", nullable = false)
    protected Double factHoursWithoutOvertime;

    @NotNull
    @Column(name = "WEEKEND_HOURS", nullable = false)
    protected Double weekendHours;

    @NotNull
    @Column(name = "WEEKEND_DAYS", nullable = false)
    protected Integer weekendDays;

    @NotNull
    @Column(name = "DAY_HOURS", nullable = false)
    protected Double dayHours;

    @NotNull
    @Column(name = "NIGHT_HOURS", nullable = false)
    protected Double nightHours;

    @NotNull
    @Column(name = "HOLIDAY_HOURS", nullable = false)
    protected Double holidayHours;

    @NotNull
    @Column(name = "HOLIDAY_DAYS", nullable = false)
    protected Integer holidayDays;

    @NotNull
    @Column(name = "ANNUAL_VACATION_DAYS", nullable = false)
    protected Integer annualVacationDays;

    @NotNull
    @Column(name = "UNPAID_VACATION_DAYS", nullable = false)
    protected Integer unpaidVacationDays;

    @NotNull
    @Column(name = "MATERNITY_VACATION_DAYS", nullable = false)
    protected Integer maternityVacationDays;

    @NotNull
    @Column(name = "CHILDCARE_VACATION_DAYS", nullable = false)
    protected Integer childcareVacationDays;

    @NotNull
    @Column(name = "SICK_DAYS", nullable = false)
    protected Integer sickDays;

    @NotNull
    @Column(name = "ABSENCE_DAYS", nullable = false)
    protected Integer absenceDays;

    @NotNull
    @Column(name = "TOTAL_FREE_DAYS", nullable = false)
    protected Integer totalFreeDays;

    @NotNull
    @Column(name = "BUS_TRIP_DAYS", nullable = false)
    protected Integer busTripDays;

    @NotNull
    @Column(name = "TOTAL_WORKED_DAYS", nullable = false)
    protected Integer totalWorkedDays;

    @NotNull
    @Column(name = "TOTAL_ABSENCE", nullable = false)
    protected Integer totalAbsence;

    @NotNull
    @Column(name = "GRAND_TOTAL_DAYS", nullable = false)
    protected Integer grandTotalDays;

    @NotNull
    @Column(name = "OVERTIME_HOURS", nullable = false)
    protected Double overtimeHours;

    @Column(name = "ATTRIBUTE1")
    protected String attribute1; //hooky days count

    @Column(name = "ATTRIBUTE2")
    protected String attribute2; //assignment id

    @Column(name = "ATTRIBUTE3")
    protected String attribute3; //innerTrainingDays

    @Column(name = "ATTRIBUTE4")
    protected String attribute4; //otherTypesCodesDays

    @Column(name = "ATTRIBUTE5")
    protected String attribute5;

    @Column(name = "ATTRIBUTE6")
    protected String attribute6;

    @Column(name = "ATTRIBUTE7")
    protected String attribute7;

    @Column(name = "ATTRIBUTE8")
    protected String attribute8;

    @Column(name = "ATTRIBUTE9")
    protected String attribute9;

    @Column(name = "ATTRIBUTE10")
    protected String attribute10;

    @Column(name = "ATTRIBUTE11")
    protected String attribute11;

    @Column(name = "ATTRIBUTE12")
    protected String attribute12;

    @Column(name = "ATTRIBUTE13")
    protected String attribute13;

    @Column(name = "ATTRIBUTE14")
    protected String attribute14;

    @Column(name = "ATTRIBUTE15")
    protected String attribute15;

    @Column(name = "ATTRIBUTE16")
    protected String attribute16;

    @Column(name = "ATTRIBUTE17")
    protected String attribute17;

    @Column(name = "ATTRIBUTE18")
    protected String attribute18;

    @Column(name = "ATTRIBUTE19")
    protected String attribute19;

    @Column(name = "ATTRIBUTE20")
    protected String attribute20;

    @Column(name = "ATTRIBUTE21")
    protected String attribute21;

    @Column(name = "ATTRIBUTE22")
    protected String attribute22;

    @Column(name = "ATTRIBUTE23")
    protected String attribute23;

    @Column(name = "ATTRIBUTE24")
    protected String attribute24;

    @Column(name = "ATTRIBUTE25")
    protected String attribute25;

    @Column(name = "ATTRIBUTE26")
    protected String attribute26;

    @Column(name = "ATTRIBUTE27")
    protected String attribute27;

    @Column(name = "ATTRIBUTE28")
    protected String attribute28;

    @Column(name = "ATTRIBUTE29")
    protected String attribute29;

    @Column(name = "ATTRIBUTE30")
    protected String attribute30;

    @Column(name = "ATTRIBUTE31")
    protected String attribute31;

    @Column(name = "ATTRIBUTE32")
    protected String attribute32;

    @Column(name = "ATTRIBUTE33")
    protected String attribute33;

    @Column(name = "ATTRIBUTE34")
    protected String attribute34;

    @Column(name = "ATTRIBUTE35")
    protected String attribute35;

    @Column(name = "ATTRIBUTE36")
    protected String attribute36;

    @Column(name = "ATTRIBUTE37")
    protected String attribute37;

    @Column(name = "ATTRIBUTE38")
    protected String attribute38;

    @Column(name = "ATTRIBUTE39")
    protected String attribute39;

    @Column(name = "ATTRIBUTE40")
    protected String attribute40;

    @Column(name = "ATTRIBUTE41")
    protected String attribute41;

    @Column(name = "ATTRIBUTE42")
    protected String attribute42;

    @Column(name = "ATTRIBUTE43")
    protected String attribute43;

    @Column(name = "ATTRIBUTE44")
    protected String attribute44;

    @Column(name = "ATTRIBUTE45")
    protected String attribute45;

    @Column(name = "ATTRIBUTE46")
    protected String attribute46;

    @Column(name = "ATTRIBUTE47")
    protected String attribute47;

    @Column(name = "ATTRIBUTE48")
    protected String attribute48;

    @Column(name = "ATTRIBUTE49")
    protected String attribute49;

    @Column(name = "ATTRIBUTE50")
    protected String attribute50;

    @Column(name = "ATTRIBUTE51")
    protected String attribute51;

    @Column(name = "ATTRIBUTE52")
    protected String attribute52;

    @Column(name = "ATTRIBUTE53")
    protected String attribute53;

    @Column(name = "ATTRIBUTE54")
    protected String attribute54;

    @Column(name = "ATTRIBUTE55")
    protected String attribute55;

    @Column(name = "ATTRIBUTE56")
    protected String attribute56;

    @Column(name = "ATTRIBUTE57")
    protected String attribute57;

    @Column(name = "ATTRIBUTE58")
    protected String attribute58;

    @Column(name = "ATTRIBUTE59")
    protected String attribute59;

    @Column(name = "ATTRIBUTE60")
    protected String attribute60;

    @Column(name = "ATTRIBUTE61")
    protected String attribute61;

    @Column(name = "ATTRIBUTE62")
    protected String attribute62;

    @Column(name = "ATTRIBUTE63")
    protected String attribute63;

    @Column(name = "ATTRIBUTE64")
    protected String attribute64;

    @Column(name = "ATTRIBUTE65")
    protected String attribute65;

    @Column(name = "ATTRIBUTE66")
    protected String attribute66;

    @Column(name = "ATTRIBUTE67")
    protected String attribute67;

    @Column(name = "ATTRIBUTE68")
    protected String attribute68;

    @Column(name = "ATTRIBUTE69")
    protected String attribute69;

    @Column(name = "ATTRIBUTE70")
    protected String attribute70;

    @Column(name = "ATTRIBUTE71")
    protected String attribute71;

    @Column(name = "ATTRIBUTE72")
    protected String attribute72;

    @Column(name = "ATTRIBUTE73")
    protected String attribute73;

    @Column(name = "ATTRIBUTE74")
    protected String attribute74;

    @Column(name = "ATTRIBUTE75")
    protected String attribute75;

    @Column(name = "ATTRIBUTE76")
    protected String attribute76;

    @Column(name = "ATTRIBUTE77")
    protected String attribute77;

    @Column(name = "ATTRIBUTE78")
    protected String attribute78;

    @Column(name = "ATTRIBUTE79")
    protected String attribute79;

    @Column(name = "ATTRIBUTE80")
    protected String attribute80;

    public void setTimecardCorrection(TimecardCorrection timecardCorrection) {
        this.timecardCorrection = timecardCorrection;
    }

    public TimecardCorrection getTimecardCorrection() {
        return timecardCorrection;
    }

    public void setWorkedHoursSummaries(List<WorkedHoursSummary> workedHoursSummaries) {
        this.workedHoursSummaries = workedHoursSummaries;
    }

    public List<WorkedHoursSummary> getWorkedHoursSummaries() {
        return workedHoursSummaries;
    }


    public Double getPlanWorkHours() {
        return planWorkHours;
    }

    public void setPlanWorkHours(Double planWorkHours) {
        this.planWorkHours = planWorkHours;
    }


    public Double getPlanWorkHoursPart() {
        return planWorkHoursPart;
    }

    public void setPlanWorkHoursPart(Double planWorkHoursPart) {
        this.planWorkHoursPart = planWorkHoursPart;
    }


    public Double getFactHoursWithoutOvertime() {
        return factHoursWithoutOvertime;
    }

    public void setFactHoursWithoutOvertime(Double factHoursWithoutOvertime) {
        this.factHoursWithoutOvertime = factHoursWithoutOvertime;
    }


    public Double getWeekendHours() {
        return weekendHours;
    }

    public void setWeekendHours(Double weekendHours) {
        this.weekendHours = weekendHours;
    }

    public Double getDayHours() {
        return dayHours;
    }

    public void setDayHours(Double dayHours) {
        this.dayHours = dayHours;
    }

    public Double getNightHours() {
        return nightHours;
    }

    public void setNightHours(Double nightHours) {
        this.nightHours = nightHours;
    }


    public Double getHolidayHours() {
        return holidayHours;
    }

    public void setHolidayHours(Double holidayHours) {
        this.holidayHours = holidayHours;
    }


    public Double getOvertimeHours() {
        return overtimeHours;
    }

    public void setOvertimeHours(Double overtimeHours) {
        this.overtimeHours = overtimeHours;
    }

    public Double getBaseWorkHours() {
        return baseWorkHours;
    }

    public void setBaseWorkHours(Double baseWorkHours) {
        this.baseWorkHours = baseWorkHours;
    }


    public void setAssignmentGroup(AssignmentGroupExt assignmentGroup) {
        this.assignmentGroup = assignmentGroup;
    }

    public AssignmentGroupExt getAssignmentGroup() {
        return assignmentGroup;
    }


    public void setAssignmentSchedule(AssignmentSchedule assignmentSchedule) {
        this.assignmentSchedule = assignmentSchedule;
    }

    public AssignmentSchedule getAssignmentSchedule() {
        return assignmentSchedule;
    }


    public void setTotalFreeDays(Integer totalFreeDays) {
        this.totalFreeDays = totalFreeDays;
    }

    public Integer getTotalFreeDays() {
        return totalFreeDays;
    }

    public void setBusTripDays(Integer busTripDays) {
        this.busTripDays = busTripDays;
    }

    public Integer getBusTripDays() {
        return busTripDays;
    }

    public void setTotalWorkedDays(Integer totalWorkedDays) {
        this.totalWorkedDays = totalWorkedDays;
    }

    public Integer getTotalWorkedDays() {
        return totalWorkedDays;
    }

    public void setTotalAbsence(Integer totalAbsence) {
        this.totalAbsence = totalAbsence;
    }

    public Integer getTotalAbsence() {
        return totalAbsence;
    }

    public void setGrandTotalDays(Integer grandTotalDays) {
        this.grandTotalDays = grandTotalDays;
    }

    public Integer getGrandTotalDays() {
        return grandTotalDays;
    }


    public void setWeekendDays(Integer weekendDays) {
        this.weekendDays = weekendDays;
    }

    public Integer getWeekendDays() {
        return weekendDays;
    }


    public void setHolidayDays(Integer holidayDays) {
        this.holidayDays = holidayDays;
    }

    public Integer getHolidayDays() {
        return holidayDays;
    }

    public void setAnnualVacationDays(Integer annualVacationDays) {
        this.annualVacationDays = annualVacationDays;
    }

    public Integer getAnnualVacationDays() {
        return annualVacationDays;
    }

    public void setUnpaidVacationDays(Integer unpaidVacationDays) {
        this.unpaidVacationDays = unpaidVacationDays;
    }

    public Integer getUnpaidVacationDays() {
        return unpaidVacationDays;
    }

    public void setMaternityVacationDays(Integer maternityVacationDays) {
        this.maternityVacationDays = maternityVacationDays;
    }

    public Integer getMaternityVacationDays() {
        return maternityVacationDays;
    }

    public void setChildcareVacationDays(Integer childcareVacationDays) {
        this.childcareVacationDays = childcareVacationDays;
    }

    public Integer getChildcareVacationDays() {
        return childcareVacationDays;
    }

    public void setSickDays(Integer sickDays) {
        this.sickDays = sickDays;
    }

    public Integer getSickDays() {
        return sickDays;
    }

    public void setAbsenceDays(Integer absenceDays) {
        this.absenceDays = absenceDays;
    }

    public Integer getAbsenceDays() {
        return absenceDays;
    }


    public void setFactWorkDays(Integer factWorkDays) {
        this.factWorkDays = factWorkDays;
    }

    public Integer getFactWorkDays() {
        return factWorkDays;
    }


    public void setBaseWorkDays(Integer baseWorkDays) {
        this.baseWorkDays = baseWorkDays;
    }

    public Integer getBaseWorkDays() {
        return baseWorkDays;
    }

    public void setPlanWorkDays(Integer planWorkDays) {
        this.planWorkDays = planWorkDays;
    }

    public Integer getPlanWorkDays() {
        return planWorkDays;
    }


    public void setType(TimecardHeaderTypeEnum type) {
        this.type = type == null ? null : type.getId();
    }

    public TimecardHeaderTypeEnum getType() {
        return type == null ? null : TimecardHeaderTypeEnum.fromId(type);
    }


    public void setStatus(TimecardHeaderStatusEnum status) {
        this.status = status == null ? null : status.getId();
    }

    public TimecardHeaderStatusEnum getStatus() {
        return status == null ? null : TimecardHeaderStatusEnum.fromId(status);
    }


    public void setEffectiveStartDate(Date effectiveStartDate) {
        this.effectiveStartDate = effectiveStartDate;
    }

    public Date getEffectiveStartDate() {
        return effectiveStartDate;
    }

    public void setEffectiveEndDate(Date effectiveEndDate) {
        this.effectiveEndDate = effectiveEndDate;
    }

    public Date getEffectiveEndDate() {
        return effectiveEndDate;
    }

    public void setDocumentVersion(Integer documentVersion) {
        this.documentVersion = documentVersion;
    }

    public Integer getDocumentVersion() {
        return documentVersion;
    }


    public void setPayroll(DicPayroll payroll) {
        this.payroll = payroll;
    }

    public DicPayroll getPayroll() {
        return payroll;
    }

    public void setPeriodStartDate(Date periodStartDate) {
        this.periodStartDate = periodStartDate;
    }

    public Date getPeriodStartDate() {
        return periodStartDate;
    }

    public void setPeriodEndDate(Date periodEndDate) {
        this.periodEndDate = periodEndDate;
    }

    public Date getPeriodEndDate() {
        return periodEndDate;
    }


    public void setAttribute1(String attribute1) {
        this.attribute1 = attribute1;
    }

    public String getAttribute1() {
        return attribute1;
    }

    public void setAttribute2(String attribute) {
        this.attribute2 = attribute;
    }

    public String getAttribute2() {
        return attribute2;
    }

    public void setAttribute3(String attribute) {
        this.attribute3 = attribute;
    }

    public String getAttribute3() {
        return attribute3;
    }

    public void setAttribute4(String attribute) {
        this.attribute4 = attribute;
    }

    public String getAttribute4() {
        return attribute4;
    }

    public void setAttribute5(String attribute) {
        this.attribute5 = attribute;
    }

    public String getAttribute5() {
        return attribute5;
    }

    public void setAttribute6(String attribute) {
        this.attribute6 = attribute;
    }

    public String getAttribute6() {
        return attribute6;
    }

    public void setAttribute7(String attribute) {
        this.attribute7 = attribute;
    }

    public String getAttribute7() {
        return attribute7;
    }

    public void setAttribute8(String attribute) {
        this.attribute8 = attribute;
    }

    public String getAttribute8() {
        return attribute8;
    }

    public void setAttribute9(String attribute) {
        this.attribute9 = attribute;
    }

    public String getAttribute9() {
        return attribute9;
    }

    public void setAttribute10(String attribute) {
        this.attribute10 = attribute;
    }

    public String getAttribute10() {
        return attribute10;
    }

    public void setAttribute11(String attribute) {
        this.attribute11 = attribute;
    }

    public String getAttribute11() {
        return attribute11;
    }

    public void setAttribute12(String attribute) {
        this.attribute12 = attribute;
    }

    public String getAttribute12() {
        return attribute12;
    }

    public void setAttribute13(String attribute) {
        this.attribute13 = attribute;
    }

    public String getAttribute13() {
        return attribute13;
    }

    public void setAttribute14(String attribute) {
        this.attribute14 = attribute;
    }

    public String getAttribute14() {
        return attribute14;
    }

    public void setAttribute15(String attribute) {
        this.attribute15 = attribute;
    }

    public String getAttribute15() {
        return attribute15;
    }

    public void setAttribute16(String attribute) {
        this.attribute16 = attribute;
    }

    public String getAttribute16() {
        return attribute16;
    }

    public void setAttribute17(String attribute) {
        this.attribute17 = attribute;
    }

    public String getAttribute17() {
        return attribute17;
    }

    public void setAttribute18(String attribute) {
        this.attribute18 = attribute;
    }

    public String getAttribute18() {
        return attribute18;
    }

    public void setAttribute19(String attribute) {
        this.attribute19 = attribute;
    }

    public String getAttribute19() {
        return attribute19;
    }

    public void setAttribute20(String attribute) {
        this.attribute20 = attribute;
    }

    public String getAttribute20() {
        return attribute20;
    }

    public void setAttribute21(String attribute) {
        this.attribute21 = attribute;
    }

    public String getAttribute21() {
        return attribute21;
    }

    public void setAttribute22(String attribute) {
        this.attribute22 = attribute;
    }

    public String getAttribute22() {
        return attribute22;
    }

    public void setAttribute23(String attribute) {
        this.attribute23 = attribute;
    }

    public String getAttribute23() {
        return attribute23;
    }

    public void setAttribute24(String attribute) {
        this.attribute24 = attribute;
    }

    public String getAttribute24() {
        return attribute24;
    }

    public void setAttribute25(String attribute) {
        this.attribute25 = attribute;
    }

    public String getAttribute25() {
        return attribute25;
    }

    public void setAttribute26(String attribute) {
        this.attribute26 = attribute;
    }

    public String getAttribute26() {
        return attribute26;
    }

    public void setAttribute27(String attribute) {
        this.attribute27 = attribute;
    }

    public String getAttribute27() {
        return attribute27;
    }

    public void setAttribute28(String attribute) {
        this.attribute28 = attribute;
    }

    public String getAttribute28() {
        return attribute28;
    }

    public void setAttribute29(String attribute) {
        this.attribute29 = attribute;
    }

    public String getAttribute29() {
        return attribute29;
    }

    public void setAttribute30(String attribute) {
        this.attribute30 = attribute;
    }

    public String getAttribute30() {
        return attribute30;
    }

    public void setAttribute31(String attribute) {
        this.attribute31 = attribute;
    }

    public String getAttribute31() {
        return attribute31;
    }

    public void setAttribute32(String attribute) {
        this.attribute32 = attribute;
    }

    public String getAttribute32() {
        return attribute32;
    }

    public void setAttribute33(String attribute) {
        this.attribute33 = attribute;
    }

    public String getAttribute33() {
        return attribute33;
    }

    public void setAttribute34(String attribute) {
        this.attribute34 = attribute;
    }

    public String getAttribute34() {
        return attribute34;
    }

    public void setAttribute35(String attribute) {
        this.attribute35 = attribute;
    }

    public String getAttribute35() {
        return attribute35;
    }

    public void setAttribute36(String attribute) {
        this.attribute36 = attribute;
    }

    public String getAttribute36() {
        return attribute36;
    }

    public void setAttribute37(String attribute) {
        this.attribute37 = attribute;
    }

    public String getAttribute37() {
        return attribute37;
    }

    public void setAttribute38(String attribute) {
        this.attribute38 = attribute;
    }

    public String getAttribute38() {
        return attribute38;
    }

    public void setAttribute39(String attribute) {
        this.attribute39 = attribute;
    }

    public String getAttribute39() {
        return attribute39;
    }

    public void setAttribute40(String attribute) {
        this.attribute40 = attribute;
    }

    public String getAttribute40() {
        return attribute40;
    }

    public void setAttribute41(String attribute) {
        this.attribute41 = attribute;
    }

    public String getAttribute41() {
        return attribute41;
    }

    public void setAttribute42(String attribute) {
        this.attribute42 = attribute;
    }

    public String getAttribute42() {
        return attribute42;
    }

    public void setAttribute43(String attribute) {
        this.attribute43 = attribute;
    }

    public String getAttribute43() {
        return attribute43;
    }

    public void setAttribute44(String attribute) {
        this.attribute44 = attribute;
    }

    public String getAttribute44() {
        return attribute44;
    }

    public void setAttribute45(String attribute) {
        this.attribute45 = attribute;
    }

    public String getAttribute45() {
        return attribute45;
    }

    public void setAttribute46(String attribute) {
        this.attribute46 = attribute;
    }

    public String getAttribute46() {
        return attribute46;
    }

    public void setAttribute47(String attribute) {
        this.attribute47 = attribute;
    }

    public String getAttribute47() {
        return attribute47;
    }

    public void setAttribute48(String attribute) {
        this.attribute48 = attribute;
    }

    public String getAttribute48() {
        return attribute48;
    }

    public void setAttribute49(String attribute) {
        this.attribute49 = attribute;
    }

    public String getAttribute49() {
        return attribute49;
    }

    public void setAttribute50(String attribute) {
        this.attribute50 = attribute;
    }

    public String getAttribute50() {
        return attribute50;
    }

    public void setAttribute51(String attribute) {
        this.attribute51 = attribute;
    }

    public String getAttribute51() {
        return attribute51;
    }

    public void setAttribute52(String attribute) {
        this.attribute52 = attribute;
    }

    public String getAttribute52() {
        return attribute52;
    }

    public void setAttribute53(String attribute) {
        this.attribute53 = attribute;
    }

    public String getAttribute53() {
        return attribute53;
    }

    public void setAttribute54(String attribute) {
        this.attribute54 = attribute;
    }

    public String getAttribute54() {
        return attribute54;
    }

    public void setAttribute55(String attribute) {
        this.attribute55 = attribute;
    }

    public String getAttribute55() {
        return attribute55;
    }

    public void setAttribute56(String attribute) {
        this.attribute56 = attribute;
    }

    public String getAttribute56() {
        return attribute56;
    }

    public void setAttribute57(String attribute) {
        this.attribute57 = attribute;
    }

    public String getAttribute57() {
        return attribute57;
    }

    public void setAttribute58(String attribute) {
        this.attribute58 = attribute;
    }

    public String getAttribute58() {
        return attribute58;
    }

    public void setAttribute59(String attribute) {
        this.attribute59 = attribute;
    }

    public String getAttribute59() {
        return attribute59;
    }

    public void setAttribute60(String attribute) {
        this.attribute60 = attribute;
    }

    public String getAttribute60() {
        return attribute60;
    }

    public void setAttribute61(String attribute) {
        this.attribute61 = attribute;
    }

    public String getAttribute61() {
        return attribute61;
    }

    public void setAttribute62(String attribute) {
        this.attribute62 = attribute;
    }

    public String getAttribute62() {
        return attribute62;
    }

    public void setAttribute63(String attribute) {
        this.attribute63 = attribute;
    }

    public String getAttribute63() {
        return attribute63;
    }

    public void setAttribute64(String attribute) {
        this.attribute64 = attribute;
    }

    public String getAttribute64() {
        return attribute64;
    }

    public void setAttribute65(String attribute) {
        this.attribute65 = attribute;
    }

    public String getAttribute65() {
        return attribute65;
    }

    public void setAttribute66(String attribute) {
        this.attribute66 = attribute;
    }

    public String getAttribute66() {
        return attribute66;
    }

    public void setAttribute67(String attribute) {
        this.attribute67 = attribute;
    }

    public String getAttribute67() {
        return attribute67;
    }

    public void setAttribute68(String attribute) {
        this.attribute68 = attribute;
    }

    public String getAttribute68() {
        return attribute68;
    }

    public void setAttribute69(String attribute) {
        this.attribute69 = attribute;
    }

    public String getAttribute69() {
        return attribute69;
    }

    public void setAttribute70(String attribute) {
        this.attribute70 = attribute;
    }

    public String getAttribute70() {
        return attribute70;
    }

    public void setAttribute71(String attribute) {
        this.attribute71 = attribute;
    }

    public String getAttribute71() {
        return attribute71;
    }

    public void setAttribute72(String attribute) {
        this.attribute72 = attribute;
    }

    public String getAttribute72() {
        return attribute72;
    }

    public void setAttribute73(String attribute) {
        this.attribute73 = attribute;
    }

    public String getAttribute73() {
        return attribute73;
    }

    public void setAttribute74(String attribute) {
        this.attribute74 = attribute;
    }

    public String getAttribute74() {
        return attribute74;
    }

    public void setAttribute75(String attribute) {
        this.attribute75 = attribute;
    }

    public String getAttribute75() {
        return attribute75;
    }

    public void setAttribute76(String attribute) {
        this.attribute76 = attribute;
    }

    public String getAttribute76() {
        return attribute76;
    }

    public void setAttribute77(String attribute) {
        this.attribute77 = attribute;
    }

    public String getAttribute77() {
        return attribute77;
    }

    public void setAttribute78(String attribute) {
        this.attribute78 = attribute;
    }

    public String getAttribute78() {
        return attribute78;
    }

    public void setAttribute79(String attribute) {
        this.attribute79 = attribute;
    }

    public String getAttribute79() {
        return attribute79;
    }

    public void setAttribute80(String attribute) {
        this.attribute80 = attribute;
    }

    public String getAttribute80() {
        return attribute80;
    }
}