package kz.uco.tsadv.entity.tb;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import java.util.UUID;

@Table(name = "TSADV_REPAIRS_DISMANTLING")
@Entity(name = "tsadv$RepairsDismantling")
public class RepairsDismantling extends AbstractParentEntity {
    private static final long serialVersionUID = -6861080904954395142L;


    @Column(name = "WORK_TYPE", nullable = false)
    protected String workType;

    @Column(name = "WORK_NUMBER", nullable = false)
    protected Long workNumber;

    @Column(name = "WORK_COST", nullable = false)
    protected Long workCost;




    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BUILDINGS_ID")
    protected Buildings buildings;

    public void setBuildings(Buildings buildings) {
        this.buildings = buildings;
    }

    public Buildings getBuildings() {
        return buildings;
    }


    public void setWorkType(String workType) {
        this.workType = workType;
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkNumber(Long workNumber) {
        this.workNumber = workNumber;
    }

    public Long getWorkNumber() {
        return workNumber;
    }

    public void setWorkCost(Long workCost) {
        this.workCost = workCost;
    }

    public Long getWorkCost() {
        return workCost;
    }



}