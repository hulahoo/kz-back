package kz.uco.tsadv.modules.personal.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.haulmont.cuba.core.entity.annotation.Listeners;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.entity.tb.dictionary.DicRateType;

@Listeners("tsadv_FlightTimeRateListener")
@Table(name = "TSADV_FLIGHT_TIME_RATE")
@Entity(name = "tsadv$FlightTimeRate")
public class FlightTimeRate extends AbstractParentEntity {
    private static final long serialVersionUID = -6626748602950772129L;

    @Column(name = "VALUE_")
    protected Double value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POSITION_GROUP_NAME_ID")
    protected PositionGroupExt positionGroupName;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DIC_RATE_TYPE_ID")
    protected DicRateType dicRateType;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_FROM")
    protected Date dateFrom;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_TO")
    protected Date dateTo;

    @Column(name = "HOURS_FROM")
    protected Double hoursFrom;

    @Column(name = "HOURS_TO")
    protected Double hoursTo;

    public void setDicRateType(DicRateType dicRateType) {
        this.dicRateType = dicRateType;
    }

    public DicRateType getDicRateType() {
        return dicRateType;
    }


    public Double getHoursFrom() {
        return hoursFrom;
    }

    public void setHoursFrom(Double hoursFrom) {
        this.hoursFrom = hoursFrom;
    }

    public Double getHoursTo() {
        return hoursTo;
    }

    public void setHoursTo(Double hoursTo) {
        this.hoursTo = hoursTo;
    }



    public void setPositionGroupName(PositionGroupExt positionGroupName) {
        this.positionGroupName = positionGroupName;
    }

    public PositionGroupExt getPositionGroupName() {
        return positionGroupName;
    }





    public void setValue(Double value) {
        this.value = value;
    }

    public Double getValue() {
        return value;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public Date getDateTo() {
        return dateTo;
    }






}