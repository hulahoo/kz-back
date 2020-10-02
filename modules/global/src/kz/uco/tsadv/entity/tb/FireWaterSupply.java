package kz.uco.tsadv.entity.tb;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

@Table(name = "TSADV_FIRE_WATER_SUPPLY")
@Entity(name = "tsadv$FireWaterSupply")
public class FireWaterSupply extends AbstractParentEntity {
    private static final long serialVersionUID = -6711438694188434728L;

    @Column(name = "EQUIPMENT", nullable = false, length = 120)
    protected String equipment;

    @Column(name = "INVENTORY_NUMBER", length = 30)
    protected String inventoryNumber;

    @Column(name = "IS_WORKING", nullable = false)
    protected Boolean isWorking = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BUILDING_FIRE_SAFETY_ID")
    protected BuildingFireSafety buildingFireSafety;

    public void setBuildingFireSafety(BuildingFireSafety buildingFireSafety) {
        this.buildingFireSafety = buildingFireSafety;
    }

    public BuildingFireSafety getBuildingFireSafety() {
        return buildingFireSafety;
    }


    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setInventoryNumber(String inventoryNumber) {
        this.inventoryNumber = inventoryNumber;
    }

    public String getInventoryNumber() {
        return inventoryNumber;
    }

    public void setIsWorking(Boolean isWorking) {
        this.isWorking = isWorking;
    }

    public Boolean getIsWorking() {
        return isWorking;
    }


}