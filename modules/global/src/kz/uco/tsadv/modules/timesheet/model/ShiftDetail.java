package kz.uco.tsadv.modules.timesheet.model;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.tsadv.modules.timesheet.dictionary.DicScheduleElementType;
import kz.uco.tsadv.modules.timesheet.model.*;
import kz.uco.tsadv.modules.timesheet.model.Shift;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.*;
import java.util.Date;
import com.haulmont.cuba.core.entity.annotation.Listeners;

@Listeners("tsadv_ShiftDetailListener")
@NamePattern("%s|id")
@Table(name = "TSADV_SHIFT_DETAIL")
@Entity(name = "tsadv$ShiftDetail")
public class ShiftDetail extends AbstractParentEntity {
    private static final long serialVersionUID = -6151194044342760032L;

    @Column(name = "NAME", nullable = false)
    protected String name;

    @Temporal(TemporalType.TIME)
    @Column(name = "TIME_FROM", nullable = false)
    protected Date timeFrom;

    @Temporal(TemporalType.TIME)
    @Column(name = "TIME_TO", nullable = false)
    protected Date timeTo;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SHIFT_ID")
    protected kz.uco.tsadv.modules.timesheet.model.Shift shift;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ELEMENT_TYPE_ID")
    protected DicScheduleElementType elementType;

    @Column(name = "DAY_FROM")
    protected Integer dayFrom;

    @Column(name = "DAY_TO")
    protected Integer dayTo;


    public void setElementType(DicScheduleElementType elementType) {
        this.elementType = elementType;
    }

    public DicScheduleElementType getElementType() {
        return elementType;
    }


    public void setShift(kz.uco.tsadv.modules.timesheet.model.Shift shift) {
        this.shift = shift;
    }

    public Shift getShift() {
        return shift;
    }


    public void setDayFrom(Integer dayFrom) {
        this.dayFrom = dayFrom;
    }

    public Integer getDayFrom() {
        return dayFrom;
    }

    public void setDayTo(Integer dayTo) {
        this.dayTo = dayTo;
    }

    public Integer getDayTo() {
        return dayTo;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setTimeFrom(Date timeFrom) {
        this.timeFrom = timeFrom;
    }

    public Date getTimeFrom() {
        return timeFrom;
    }

    public void setTimeTo(Date timeTo) {
        this.timeTo = timeTo;
    }

    public Date getTimeTo() {
        return timeTo;
    }


}