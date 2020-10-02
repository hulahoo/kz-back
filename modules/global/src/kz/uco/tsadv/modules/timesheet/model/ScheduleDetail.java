package kz.uco.tsadv.modules.timesheet.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.timesheet.dictionary.DicScheduleElementType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@NamePattern("%s|id")
@Table(name = "TSADV_SCHEDULE_DETAIL")
@Entity(name = "tsadv$ScheduleDetail")
public class ScheduleDetail extends AbstractParentEntity {
    private static final long serialVersionUID = -1017012951814664679L;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SUMMARY_ID")
    protected ScheduleSummary summary;

    @NotNull
    @Column(name = "DAY_", nullable = false)
    protected Integer day;

    @Temporal(TemporalType.DATE)
    @Column(name = "DAY_DATE", nullable = false)
    protected Date dayDate;

    @NotNull
    @Column(name = "HOURS", nullable = false)
    protected Double hours;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TIME_IN")
    protected Date timeIn;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TIME_OUT")
    protected Date timeOut;

    @NotNull
    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ELEMENT_TYPE_ID")
    protected DicScheduleElementType elementType;

    public void setDayDate(Date dayDate) {
        this.dayDate = dayDate;
    }

    public Date getDayDate() {
        return dayDate;
    }


    public void setSummary(ScheduleSummary summary) {
        this.summary = summary;
    }

    public ScheduleSummary getSummary() {
        return summary;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getDay() {
        return day;
    }

    public void setHours(Double hours) {
        this.hours = hours;
    }

    public Double getHours() {
        return hours;
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

    public void setElementType(DicScheduleElementType elementType) {
        this.elementType = elementType;
    }

    public DicScheduleElementType getElementType() {
        return elementType;
    }


}