package kz.uco.tsadv.modules.timesheet.model;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.core.global.PersistenceHelper;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.timesheet.dictionary.DicScheduleElementType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@NamePattern("%s|id")
@Table(name = "TSADV_SCHEDULE_SUMMARY")
@Entity(name = "tsadv$ScheduleSummary")
public class ScheduleSummary extends AbstractParentEntity {
    private static final long serialVersionUID = -4234505695614000548L;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "HEADER_ID")
    protected ScheduleHeader header;

    @Column(name = "DAY_", nullable = false)
    protected Integer day;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "DAY_DATE", nullable = false)
    protected Date dayDate;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SHIFT_ID")
    protected Shift shift;

    @NotNull
    @Column(name = "HOURS", nullable = false)
    protected Double hours;

    @Column(name = "BASE_HOURS", nullable = false)
    protected Double baseHours;

    @Temporal(TemporalType.TIME)
    @Column(name = "START_TIME")
    protected Date startTime;

    @Temporal(TemporalType.TIME)
    @Column(name = "END_TIME")
    protected Date endTime;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ELEMENT_TYPE_ID")
    protected DicScheduleElementType elementType;

    @Column(name = "CORRECTION_FLAG", nullable = false)
    protected Boolean correctionFlag = false;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "summary")
    protected List<ScheduleDetail> details;

    @Column(name = "DISPLAY_VALUE")
    protected String displayValue;

    @Transient
    protected StandardShift standardShift;

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }


    public void setBaseHours(Double baseHours) {
        this.baseHours = baseHours;
    }

    public Double getBaseHours() {
        return baseHours;
    }


    public void setStandardShift(StandardShift standardShift) {
        this.standardShift = standardShift;
    }

    public StandardShift getStandardShift() {
        return standardShift;
    }


    public void setDayDate(Date dayDate) {
        this.dayDate = dayDate;
    }

    public Date getDayDate() {
        return dayDate;
    }


    public void setDetails(List<ScheduleDetail> details) {
        this.details = details;
    }

    public List<ScheduleDetail> getDetails() {
        return details;
    }


    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public Shift getShift() {
        return shift;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getEndTime() {
        return endTime;
    }


    public void setCorrectionFlag(Boolean correctionFlag) {
        this.correctionFlag = correctionFlag;
    }

    public Boolean getCorrectionFlag() {
        return correctionFlag;
    }


    public void setHeader(ScheduleHeader header) {
        this.header = header;
    }

    public ScheduleHeader getHeader() {
        return header;
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

    public void setElementType(DicScheduleElementType elementType) {
        this.elementType = elementType;
    }

    public DicScheduleElementType getElementType() {
        return elementType;
    }

    @MetaProperty(related = {"shift", "elementType"})
    public String getShiftName() {
        if (shift != null
                && PersistenceHelper.isLoaded(this, "shift")
                && PersistenceHelper.isLoaded(shift, "code")) {
            return shift.getCode();
        }

        if (elementType != null
                && PersistenceHelper.isLoaded(this, "elementType")
                && PersistenceHelper.isLoaded(elementType, "shortName")) {
            return elementType.getShortName();
        }

        return null;
    }


}