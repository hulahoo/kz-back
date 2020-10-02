package kz.uco.tsadv.modules.timesheet.model;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

import java.util.List;
import java.util.UUID;

@MetaClass(name = "tsadv$Timecard")
public class Timecard extends BaseUuidEntity {
    private static final long serialVersionUID = 592782907046788843L;

    @MetaProperty
    String name;

    @MetaProperty
    protected String costCenterString;

    @MetaProperty
    protected List<WorkedHoursSummary> workedHoursSummaries;

    @MetaProperty
    protected UUID assignmentGroupId;

    @MetaProperty
    protected String assignmentGroupLegacyId; //to do need to move to assignmentGroup field (create)

    @MetaProperty
    protected List<TimecardHeader> timecardHeaders;

    @MetaProperty
    String positionName;

    @MetaProperty
    String schedules;

    @MetaProperty
    Boolean corrective;

    @MetaProperty
    WorkedHoursSummary summary1;

    @MetaProperty
    WorkedHoursSummary summary2;

    @MetaProperty
    WorkedHoursSummary summary3;

    @MetaProperty
    WorkedHoursSummary summary4;

    @MetaProperty
    WorkedHoursSummary summary5;

    @MetaProperty
    WorkedHoursSummary summary6;

    @MetaProperty
    WorkedHoursSummary summary7;

    @MetaProperty
    WorkedHoursSummary summary8;

    @MetaProperty
    WorkedHoursSummary summary9;

    @MetaProperty
    WorkedHoursSummary summary10;

    @MetaProperty
    WorkedHoursSummary summary11;

    @MetaProperty
    WorkedHoursSummary summary12;

    @MetaProperty
    WorkedHoursSummary summary13;

    @MetaProperty
    WorkedHoursSummary summary14;

    @MetaProperty
    WorkedHoursSummary summary15;

    @MetaProperty
    WorkedHoursSummary summary16;

    @MetaProperty
    WorkedHoursSummary summary17;

    @MetaProperty
    WorkedHoursSummary summary18;

    @MetaProperty
    WorkedHoursSummary summary19;

    @MetaProperty
    WorkedHoursSummary summary20;

    @MetaProperty
    WorkedHoursSummary summary21;

    @MetaProperty
    WorkedHoursSummary summary22;

    @MetaProperty
    WorkedHoursSummary summary23;

    @MetaProperty
    WorkedHoursSummary summary24;

    @MetaProperty
    WorkedHoursSummary summary25;

    @MetaProperty
    WorkedHoursSummary summary26;

    @MetaProperty
    WorkedHoursSummary summary27;

    @MetaProperty
    WorkedHoursSummary summary28;

    @MetaProperty
    WorkedHoursSummary summary29;

    @MetaProperty
    WorkedHoursSummary summary30;

    @MetaProperty
    WorkedHoursSummary summary31;

    @MetaProperty
    Double baseWorkHours;

    @MetaProperty
    Integer planWorkDays;

    @MetaProperty
    Double planWorkHours;

    @MetaProperty
    Integer baseWorkDays;

    @MetaProperty
    Double factHoursWithoutOvertime;

    @MetaProperty
    Double overtimeHours;

    @MetaProperty
    Integer factWorkDays;

    @MetaProperty
    Double dayHours;

    @MetaProperty
    Double nightHours;

    @MetaProperty
    Integer annualVacationDays;

    @MetaProperty
    Integer unpaidVacationDays;

    @MetaProperty
    Integer sickDays;

    @MetaProperty
    Integer absenceDays;

    @MetaProperty
    Integer totalFreeDays;

    @MetaProperty
    Integer bussinessTrip;

    @MetaProperty
    Integer totalAbsence;

    @MetaProperty
    Integer totalWorkedDays;

    @MetaProperty
    Integer grandTotalDays;

    @MetaProperty
    protected Integer downtime;

    @MetaProperty
    protected Integer quarantine;

    public void setDowntime(Integer downtime) {
        this.downtime = downtime;
    }

    public Integer getDowntime() {
        return downtime;
    }

    public void setQuarantine(Integer quarantine) {
        this.quarantine = quarantine;
    }

    public Integer getQuarantine() {
        return quarantine;
    }

    public void setDayHours(Double dayHours) {
        this.dayHours = dayHours;
    }

    public Double getDayHours() {
        return dayHours;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public List<WorkedHoursSummary> getWorkedHoursSummaries() {
        return workedHoursSummaries;
    }

    public void setWorkedHoursSummaries(List<WorkedHoursSummary> workedHoursSummaries) {
        this.workedHoursSummaries = workedHoursSummaries;
    }

    public UUID getAssignmentGroupId() {
        return assignmentGroupId;
    }

    public void setAssignmentGroupId(UUID assignmentGroupId) {
        this.assignmentGroupId = assignmentGroupId;
    }

    public List<TimecardHeader> getTimecardHeaders() {
        return timecardHeaders;
    }

    public void setTimecardHeaders(List<TimecardHeader> timecardHeaders) {
        this.timecardHeaders = timecardHeaders;
    }

    public String getSchedules() {
        return schedules;
    }

    public void setSchedules(String schedules) {
        this.schedules = schedules;
    }

    public Double getPlanWorkHours() {
        return planWorkHours;
    }

    public void setPlanWorkHours(Double planWorkHours) {
        this.planWorkHours = planWorkHours;
    }

    public Double getBaseWorkHours() {
        return baseWorkHours;
    }

    public void setBaseWorkHours(Double baseWorkHours) {
        this.baseWorkHours = baseWorkHours;
    }

    public Integer getPlanWorkDays() {
        return planWorkDays;
    }

    public void setPlanWorkDays(Integer planWorkDays) {
        this.planWorkDays = planWorkDays;
    }

    public Integer getBaseWorkDays() {
        return baseWorkDays;
    }

    public void setBaseWorkDays(Integer baseWorkDays) {
        this.baseWorkDays = baseWorkDays;
    }

    public Double getFactHoursWithoutOvertime() {
        return factHoursWithoutOvertime;
    }

    public void setFactHoursWithoutOvertime(Double factHoursWithoutOvertime) {
        this.factHoursWithoutOvertime = factHoursWithoutOvertime;
    }

    public Integer getFactWorkDays() {
        return factWorkDays;
    }

    public void setFactWorkDays(Integer factWorkDays) {
        this.factWorkDays = factWorkDays;
    }

    public Double getNightHours() {
        return nightHours;
    }

    public void setNightHours(Double nightHours) {
        this.nightHours = nightHours;
    }

    public Integer getAnnualVacationDays() {
        return annualVacationDays;
    }

    public void setAnnualVacationDays(Integer annualVacationDays) {
        this.annualVacationDays = annualVacationDays;
    }

    public Integer getUnpaidVacationDays() {
        return unpaidVacationDays;
    }

    public void setUnpaidVacationDays(Integer unpaidVacationDays) {
        this.unpaidVacationDays = unpaidVacationDays;
    }

    public Integer getSickDays() {
        return sickDays;
    }

    public void setSickDays(Integer sickDays) {
        this.sickDays = sickDays;
    }

    public Integer getAbsenceDays() {
        return absenceDays;
    }

    public void setAbsenceDays(Integer absenceDays) {
        this.absenceDays = absenceDays;
    }

    public Integer getTotalFreeDays() {
        return totalFreeDays;
    }

    public void setTotalFreeDays(Integer totalFreeDays) {
        this.totalFreeDays = totalFreeDays;
    }

    public Integer getBussinessTrip() {
        return bussinessTrip;
    }

    public void setBussinessTrip(Integer bussinessTrip) {
        this.bussinessTrip = bussinessTrip;
    }

    public Integer getTotalAbsence() {
        return totalAbsence;
    }

    public void setTotalAbsence(Integer totalAbsence) {
        this.totalAbsence = totalAbsence;
    }

    public Integer getTotalWorkedDays() {
        return totalWorkedDays;
    }

    public void setTotalWorkedDays(Integer totalWorkedDays) {
        this.totalWorkedDays = totalWorkedDays;
    }

    public Integer getGrandTotalDays() {
        return grandTotalDays;
    }

    public void setGrandTotalDays(Integer grandTotalDays) {
        this.grandTotalDays = grandTotalDays;
    }

    public Boolean getCorrective() {
        return corrective;
    }

    public void setCorrective(Boolean corrective) {
        this.corrective = corrective;
    }

    public WorkedHoursSummary getSummary1() {
        return summary1;
    }

    public void setSummary1(WorkedHoursSummary summary1) {
        this.summary1 = summary1;
    }

    public WorkedHoursSummary getSummary2() {
        return summary2;
    }

    public void setSummary2(WorkedHoursSummary summary2) {
        this.summary2 = summary2;
    }

    public WorkedHoursSummary getSummary3() {
        return summary3;
    }

    public void setSummary3(WorkedHoursSummary summary3) {
        this.summary3 = summary3;
    }

    public WorkedHoursSummary getSummary4() {
        return summary4;
    }

    public void setSummary4(WorkedHoursSummary summary4) {
        this.summary4 = summary4;
    }

    public WorkedHoursSummary getSummary5() {
        return summary5;
    }

    public void setSummary5(WorkedHoursSummary summary5) {
        this.summary5 = summary5;
    }

    public WorkedHoursSummary getSummary6() {
        return summary6;
    }

    public void setSummary6(WorkedHoursSummary summary6) {
        this.summary6 = summary6;
    }

    public WorkedHoursSummary getSummary7() {
        return summary7;
    }

    public void setSummary7(WorkedHoursSummary summary7) {
        this.summary7 = summary7;
    }

    public WorkedHoursSummary getSummary8() {
        return summary8;
    }

    public void setSummary8(WorkedHoursSummary summary8) {
        this.summary8 = summary8;
    }

    public WorkedHoursSummary getSummary9() {
        return summary9;
    }

    public void setSummary9(WorkedHoursSummary summary9) {
        this.summary9 = summary9;
    }

    public WorkedHoursSummary getSummary10() {
        return summary10;
    }

    public void setSummary10(WorkedHoursSummary summary10) {
        this.summary10 = summary10;
    }

    public WorkedHoursSummary getSummary11() {
        return summary11;
    }

    public void setSummary11(WorkedHoursSummary summary11) {
        this.summary11 = summary11;
    }

    public WorkedHoursSummary getSummary12() {
        return summary12;
    }

    public void setSummary12(WorkedHoursSummary summary12) {
        this.summary12 = summary12;
    }

    public WorkedHoursSummary getSummary13() {
        return summary13;
    }

    public void setSummary13(WorkedHoursSummary summary13) {
        this.summary13 = summary13;
    }

    public WorkedHoursSummary getSummary14() {
        return summary14;
    }

    public void setSummary14(WorkedHoursSummary summary14) {
        this.summary14 = summary14;
    }

    public WorkedHoursSummary getSummary15() {
        return summary15;
    }

    public void setSummary15(WorkedHoursSummary summary15) {
        this.summary15 = summary15;
    }

    public WorkedHoursSummary getSummary16() {
        return summary16;
    }

    public void setSummary16(WorkedHoursSummary summary16) {
        this.summary16 = summary16;
    }

    public WorkedHoursSummary getSummary17() {
        return summary17;
    }

    public void setSummary17(WorkedHoursSummary summary17) {
        this.summary17 = summary17;
    }

    public WorkedHoursSummary getSummary18() {
        return summary18;
    }

    public void setSummary18(WorkedHoursSummary summary18) {
        this.summary18 = summary18;
    }

    public WorkedHoursSummary getSummary19() {
        return summary19;
    }

    public void setSummary19(WorkedHoursSummary summary19) {
        this.summary19 = summary19;
    }

    public WorkedHoursSummary getSummary20() {
        return summary20;
    }

    public void setSummary20(WorkedHoursSummary summary20) {
        this.summary20 = summary20;
    }

    public WorkedHoursSummary getSummary21() {
        return summary21;
    }

    public void setSummary21(WorkedHoursSummary summary21) {
        this.summary21 = summary21;
    }

    public WorkedHoursSummary getSummary22() {
        return summary22;
    }

    public void setSummary22(WorkedHoursSummary summary22) {
        this.summary22 = summary22;
    }

    public WorkedHoursSummary getSummary23() {
        return summary23;
    }

    public void setSummary23(WorkedHoursSummary summary23) {
        this.summary23 = summary23;
    }

    public WorkedHoursSummary getSummary24() {
        return summary24;
    }

    public void setSummary24(WorkedHoursSummary summary24) {
        this.summary24 = summary24;
    }

    public WorkedHoursSummary getSummary25() {
        return summary25;
    }

    public void setSummary25(WorkedHoursSummary summary25) {
        this.summary25 = summary25;
    }

    public WorkedHoursSummary getSummary26() {
        return summary26;
    }

    public void setSummary26(WorkedHoursSummary summary26) {
        this.summary26 = summary26;
    }

    public WorkedHoursSummary getSummary27() {
        return summary27;
    }

    public void setSummary27(WorkedHoursSummary summary27) {
        this.summary27 = summary27;
    }

    public WorkedHoursSummary getSummary28() {
        return summary28;
    }

    public void setSummary28(WorkedHoursSummary summary28) {
        this.summary28 = summary28;
    }

    public WorkedHoursSummary getSummary29() {
        return summary29;
    }

    public void setSummary29(WorkedHoursSummary summary29) {
        this.summary29 = summary29;
    }

    public WorkedHoursSummary getSummary30() {
        return summary30;
    }

    public void setSummary30(WorkedHoursSummary summary30) {
        this.summary30 = summary30;
    }

    public WorkedHoursSummary getSummary31() {
        return summary31;
    }

    public void setSummary31(WorkedHoursSummary summary31) {
        this.summary31 = summary31;
    }

    public String getAssignmentGroupLegacyId() {
        return assignmentGroupLegacyId;
    }

    public void setAssignmentGroupLegacyId(String assignmentGroupLegacyId) {
        this.assignmentGroupLegacyId = assignmentGroupLegacyId;
    }

    public String getCostCenterString() {
        return costCenterString;
    }

    public void setCostCenterString(String costCenterString) {
        this.costCenterString = costCenterString;
    }

    public Double getOvertimeHours() {
        return overtimeHours;
    }

    public void setOvertimeHours(Double overtimeHours) {
        this.overtimeHours = overtimeHours;
    }
}