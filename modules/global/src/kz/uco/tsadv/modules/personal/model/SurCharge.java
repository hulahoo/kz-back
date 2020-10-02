package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.MetaProperty;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;

import javax.persistence.*;
import java.util.Date;
import kz.uco.tsadv.modules.personal.enums.GrossNet;
import kz.uco.base.entity.dictionary.DicCurrency;
import com.haulmont.cuba.core.entity.annotation.Listeners;

@Listeners("tsadv_SurChargeListener")
@Table(name = "TSADV_SUR_CHARGE")
@Entity(name = "tsadv$SurCharge")
public class SurCharge extends AbstractParentEntity {
    private static final long serialVersionUID = 5240735534774285837L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "NAME_ID")
    protected SurChargeName name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CURRENCY_ID")
    protected DicCurrency currency;

    @Column(name = "REASON", length = 1000)
    protected String reason;

    @Column(name = "GROSS_NET")
    protected String grossNet;

    @Column(name = "PERIOD", nullable = false)
    protected Integer period;

    @Column(name = "TYPE_", nullable = false)
    protected Integer type;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_FROM")
    protected Date dateFrom;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_TO")
    protected Date dateTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POSITION_GROUP_ID")
    protected PositionGroupExt positionGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ASSIGNMENT_GROUP_ID")
    protected AssignmentGroupExt assignmentGroup;

    @Column(name = "VALUE_", nullable = false)
    protected Double value;

    @Transient
    @MetaProperty
    protected String calculate;

    public void setCurrency(DicCurrency currency) {
        this.currency = currency;
    }

    public DicCurrency getCurrency() {
        return currency;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }


    public void setGrossNet(GrossNet grossNet) {
        this.grossNet = grossNet == null ? null : grossNet.getId();
    }

    public GrossNet getGrossNet() {
        return grossNet == null ? null : GrossNet.fromId(grossNet);
    }


    public String getCalculate() {
        return calculate;
    }

    public void setCalculate(String calculate) {
        this.calculate = calculate;
    }


    public void setValue(Double value) {
        this.value = value;
    }

    public Double getValue() {
        return value;
    }


    public void setAssignmentGroup(AssignmentGroupExt assignmentGroup) {
        this.assignmentGroup = assignmentGroup;
    }

    public AssignmentGroupExt getAssignmentGroup() {
        return assignmentGroup;
    }


    public void setPositionGroup(PositionGroupExt positionGroup) {
        this.positionGroup = positionGroup;
    }

    public PositionGroupExt getPositionGroup() {
        return positionGroup;
    }


    public void setPeriod(kz.uco.tsadv.modules.personal.model.SurChargePeriod period) {
        this.period = period == null ? null : period.getId();
    }

    public kz.uco.tsadv.modules.personal.model.SurChargePeriod getPeriod() {
        return period == null ? null : SurChargePeriod.fromId(period);
    }

    public void setType(SurChargeType type) {
        this.type = type == null ? null : type.getId();
    }

    public SurChargeType getType() {
        return type == null ? null : SurChargeType.fromId(type);
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


    public void setName(SurChargeName name) {
        this.name = name;
    }

    public SurChargeName getName() {
        return name;
    }


}