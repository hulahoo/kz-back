package kz.uco.tsadv.modules.personal.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import com.haulmont.cuba.core.entity.annotation.Listeners;

@Listeners("tsadv_FlySurChargeListener")
@Table(name = "TSADV_FLY_SUR_CHARGE")
@Entity(name = "tsadv$FlySurCharge")
public class FlySurCharge extends AbstractParentEntity {
    private static final long serialVersionUID = 7634790492008930210L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POSITION_GROUP_ID_ID")
    protected PositionGroupExt positionGroupId;

    @Column(name = "ALLOWED")
    protected Boolean allowed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ASSIGNMENT_GROUP_ID_ID")
    protected AssignmentGroupExt assignmentGroupId;

    @Column(name = "LEVEL_")
    protected String level;

    @Column(name = "FLYING_HOURS")
    protected Double flyingHours;

    @Column(name = "FLYING_SURCHARGE")
    protected Double flyingSurcharge;

    @Column(name = "BONUS")
    protected Double bonus;

    @Column(name = "RATE_PER_HOUR")
    protected Double ratePerHour;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_FROM")
    protected Date dateFrom;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_TO")
    protected Date dateTo;

    public void setAllowed(Boolean allowed) {
        this.allowed = allowed;
    }

    public Boolean getAllowed() {
        return allowed;
    }


    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }


    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }




    public void setPositionGroupId(PositionGroupExt positionGroupId) {
        this.positionGroupId = positionGroupId;
    }

    public PositionGroupExt getPositionGroupId() {
        return positionGroupId;
    }

    public void setAssignmentGroupId(AssignmentGroupExt assignmentGroupId) {
        this.assignmentGroupId = assignmentGroupId;
    }

    public AssignmentGroupExt getAssignmentGroupId() {
        return assignmentGroupId;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getLevel() {
        return level;
    }

    public void setFlyingHours(Double flyingHours) {
        this.flyingHours = flyingHours;
    }

    public Double getFlyingHours() {
        return flyingHours;
    }

    public void setFlyingSurcharge(Double flyingSurcharge) {
        this.flyingSurcharge = flyingSurcharge;
    }

    public Double getFlyingSurcharge() {
        return flyingSurcharge;
    }

    public void setBonus(Double bonus) {
        this.bonus = bonus;
    }

    public Double getBonus() {
        return bonus;
    }

    public void setRatePerHour(Double ratePerHour) {
        this.ratePerHour = ratePerHour;
    }

    public Double getRatePerHour() {
        return ratePerHour;
    }






}