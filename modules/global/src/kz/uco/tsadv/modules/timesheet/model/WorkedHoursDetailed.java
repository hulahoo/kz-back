package kz.uco.tsadv.modules.timesheet.model;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.timesheet.dictionary.DicScheduleElementType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Listeners("tsadv_WorkedHoursDetailedListener")
@Table(name = "TSADV_WORKED_HOURS_DETAILED")
@Entity(name = "tsadv$WorkedHoursDetailed")
public class WorkedHoursDetailed extends AbstractParentEntity {
    private static final long serialVersionUID = 3356688961952138551L;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "WORKED_HOURS_SUMMARY_ID")
    protected WorkedHoursSummary workedHoursSummary;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TIMECARD_HEADER_ID")
    protected TimecardHeader timecardHeader;

    @Temporal(TemporalType.DATE)
    @Column(name = "WORKED_DATE", nullable = false)
    protected Date workedDate;

    @NotNull
    @Column(name = "HOURS", nullable = false)
    protected Double hours;

    @NotNull
    @Column(name = "PLAN_HOURS", nullable = false)
    protected Double planHours;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TIME_IN")
    protected Date timeIn;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TIME_OUT")
    protected Date timeOut;

    @NotNull
    @Lookup(type = LookupType.DROPDOWN, actions = {"lookup"})
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SCHEDULE_ELEMENT_TYPE_ID")
    protected DicScheduleElementType scheduleElementType;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ACTUAL_TIME_IN")
    protected Date actualTimeIn;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ACTUAL_TIME_OUT")
    protected Date actualTimeOut;

    @Transient
    @MetaProperty
    protected Boolean isNeedToCheckAndCreateAdditionalHours;

    public void setTimecardHeader(TimecardHeader timecardHeader) {
        this.timecardHeader = timecardHeader;
    }

    public TimecardHeader getTimecardHeader() {
        return timecardHeader;
    }


    public void setPlanHours(Double planHours) {
        this.planHours = planHours;
    }

    public Double getPlanHours() {
        return planHours;
    }


    public void setIsNeedToCheckAndCreateAdditionalHours(Boolean isNeedToCheckAndCreateAdditionalHours) {
        this.isNeedToCheckAndCreateAdditionalHours = isNeedToCheckAndCreateAdditionalHours;
    }

    public Boolean getIsNeedToCheckAndCreateAdditionalHours() {
        return isNeedToCheckAndCreateAdditionalHours;
    }


    public void setWorkedHoursSummary(WorkedHoursSummary workedHoursSummary) {
        this.workedHoursSummary = workedHoursSummary;
    }

    public WorkedHoursSummary getWorkedHoursSummary() {
        return workedHoursSummary;
    }


    public Double getHours() {
        return hours;
    }

    public void setHours(Double hours) {
        this.hours = hours;
    }


    public void setScheduleElementType(DicScheduleElementType scheduleElementType) {
        this.scheduleElementType = scheduleElementType;
    }

    public DicScheduleElementType getScheduleElementType() {
        return scheduleElementType;
    }

    public void setActualTimeIn(Date actualTimeIn) {
        this.actualTimeIn = actualTimeIn;
    }

    public Date getActualTimeIn() {
        return actualTimeIn;
    }

    public void setActualTimeOut(Date actualTimeOut) {
        this.actualTimeOut = actualTimeOut;
    }

    public Date getActualTimeOut() {
        return actualTimeOut;
    }


    public void setWorkedDate(Date workedDate) {
        this.workedDate = workedDate;
    }

    public Date getWorkedDate() {
        return workedDate;
    }


    public void setTimeIn(Date timeIn) {
        this.timeIn = timeIn;
    }

    public Date getTimeIn() {
        return timeIn;
    }

    public void setTimeOut(Date timeOut) {
        this.timeOut = timeOut;
    }

    public Date getTimeOut() {
        return timeOut;
    }


}