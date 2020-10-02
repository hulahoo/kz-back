package kz.uco.tsadv.entity.tb;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import java.util.UUID;

@Table(name = "TSADV_BUILDING_COST")
@Entity(name = "tsadv$BuildingCost")
public class BuildingCost extends AbstractParentEntity {
    private static final long serialVersionUID = -6014330822308069617L;

    @Temporal(TemporalType.DATE)
    @Column(name = "COST_DATE", nullable = false)
    protected Date costDate;


    @NotNull
    @Column(name = "BALANCE_COST", nullable = false)
    protected Long balanceCost;

    @Column(name = "RESIDUAL_VALUE", nullable = false)
    protected Long residualValue;




    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BUILDINGS_ID")
    protected Buildings buildings;

    public void setBuildings(Buildings buildings) {
        this.buildings = buildings;
    }

    public Buildings getBuildings() {
        return buildings;
    }


    public void setCostDate(Date costDate) {
        this.costDate = costDate;
    }

    public Date getCostDate() {
        return costDate;
    }

    public void setBalanceCost(Long balanceCost) {
        this.balanceCost = balanceCost;
    }

    public Long getBalanceCost() {
        return balanceCost;
    }

    public void setResidualValue(Long residualValue) {
        this.residualValue = residualValue;
    }

    public Long getResidualValue() {
        return residualValue;
    }




}