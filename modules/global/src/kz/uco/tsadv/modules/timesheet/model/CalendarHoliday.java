package kz.uco.tsadv.modules.timesheet.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.timesheet.enums.DayType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import com.haulmont.cuba.core.entity.annotation.Listeners;

@Listeners("tsadv_CalendarHolidayListener")
@NamePattern("%s|id")
@Table(name = "TSADV_CALENDAR_HOLIDAY")
@Entity(name = "tsadv$CalendarHoliday")
public class CalendarHoliday extends AbstractParentEntity {
    private static final long serialVersionUID = 6631196472596565779L;

    @NotNull
    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CALENDAR_ID")
    protected Calendar calendar;

    @Column(name = "NAME", nullable = false, length = 100)
    protected String name;


    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE", nullable = false)
    protected Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE", nullable = false)
    protected Date endDate;

    @NotNull
    @Temporal(TemporalType.DATE)
    @Column(name = "ACTION_DATE_FROM", nullable = false)
    protected Date actionDateFrom;

    @NotNull
    @Temporal(TemporalType.DATE)
    @Column(name = "ACTION_DATE_TO", nullable = false)
    protected Date actionDateTo;

    @NotNull
    @Column(name = "STATE", nullable = false)
    protected Boolean state = false;


    @NotNull
    @Column(name = "DAY_TYPE", nullable = false)
    protected String dayType;


    @Temporal(TemporalType.DATE)
    @Column(name = "TRANSFER_START_DATE")
    protected Date transferStartDate;


    @Temporal(TemporalType.DATE)
    @Column(name = "TRANSFER_END_DATE")
    protected Date transferEndDate;


    public void setActionDateFrom(Date actionDateFrom) {
        this.actionDateFrom = actionDateFrom;
    }

    public Date getActionDateFrom() {
        return actionDateFrom;
    }

    public void setActionDateTo(Date actionDateTo) {
        this.actionDateTo = actionDateTo;
    }

    public void setTransferStartDate(Date transferStartDate) {
        this.transferStartDate = transferStartDate;
    }

    public Date getTransferStartDate() {
        return transferStartDate;
    }

    public void setTransferEndDate(Date transferEndDate) {
        this.transferEndDate = transferEndDate;
    }

    public Date getTransferEndDate() {
        return transferEndDate;
    }

    public Date getActionDateTo() {
        return actionDateTo;
    }




    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public Calendar getCalendar() {
        return calendar;
    }


    public void setDayType(DayType dayType) {
        this.dayType = dayType == null ? null : dayType.getId();
    }

    public DayType getDayType() {
        return dayType == null ? null : DayType.fromId(dayType);
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public void setState(Boolean state) {
        this.state = state;
    }

    public Boolean getState() {
        return state;
    }


}