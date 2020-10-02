package kz.uco.tsadv.entity.tb;

import javax.persistence.*;
import java.util.Date;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.validation.constraints.NotNull;
import kz.uco.tsadv.entity.tb.dictionary.ObjectType;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import java.util.List;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;

@NamePattern("%s|name")
@Table(name = "TSADV_BUILDINGS")
@Entity(name = "tsadv$Buildings")
public class Buildings extends AbstractParentEntity {
    private static final long serialVersionUID = 7163374577401323427L;

    @Column(name = "NAME", nullable = false)
    protected String name;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "buildings")
    protected List<RepairsDismantling> dismantling;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "buildings")
    protected List<BuildingMaintenance> maintenance;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "buildings")
    protected List<Attachment> attachment;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "buildings",cascade = CascadeType.ALL)
    protected List<BuildingTechnicalInspections> inspection;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "buildings")
    protected List<TechnicalStatus> techStatus;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "buildings")
    protected List<BuildingCost> cost;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "OBJECT_TYPE_ID")
    protected ObjectType objectType;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ORGANIZATION_ID")
    protected OrganizationGroupExt organization;

    @Temporal(TemporalType.DATE)
    @Column(name = "COMMISSIONING", nullable = false)
    protected Date commissioning;

    @Column(name = "BUILDING_VOLUME", nullable = false)
    protected Long buildingVolume;

    @Column(name = "BUILDING_AREA", nullable = false)
    protected Long buildingArea;

    @Column(name = "TOTAL_AREA", nullable = false)
    protected Long totalArea;

    @Column(name = "TECHNICAL_PASSPORT")
    protected Boolean technicalPassport;

    @Column(name = "BUILDING_PASSPORT")
    protected Boolean buildingPassport;

    @Column(name = "TECHNICAL_JOURNAL")
    protected Boolean technicalJournal;

    @Column(name = "INVENTORY_NUMBER", nullable = false)
    protected String inventoryNumber;

    @Column(name = "OLD_INVENTORY_NUMBER", nullable = false)
    protected String oldInventoryNumber;

    public void setDismantling(List<RepairsDismantling> dismantling) {
        this.dismantling = dismantling;
    }

    public List<RepairsDismantling> getDismantling() {
        return dismantling;
    }


    public void setMaintenance(List<BuildingMaintenance> maintenance) {
        this.maintenance = maintenance;
    }

    public List<BuildingMaintenance> getMaintenance() {
        return maintenance;
    }


    public void setAttachment(List<Attachment> attachment) {
        this.attachment = attachment;
    }

    public List<Attachment> getAttachment() {
        return attachment;
    }


    public void setInspection(List<BuildingTechnicalInspections> inspection) {
        this.inspection = inspection;
    }

    public List<BuildingTechnicalInspections> getInspection() {
        return inspection;
    }


    public void setTechStatus(List<TechnicalStatus> techStatus) {
        this.techStatus = techStatus;
    }

    public List<TechnicalStatus> getTechStatus() {
        return techStatus;
    }


    public void setCost(List<BuildingCost> cost) {
        this.cost = cost;
    }

    public List<BuildingCost> getCost() {
        return cost;
    }


    public OrganizationGroupExt getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationGroupExt organization) {
        this.organization = organization;
    }









    public ObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }




    public void setBuildingVolume(Long buildingVolume) {
        this.buildingVolume = buildingVolume;
    }

    public Long getBuildingVolume() {
        return buildingVolume;
    }

    public void setBuildingArea(Long buildingArea) {
        this.buildingArea = buildingArea;
    }

    public Long getBuildingArea() {
        return buildingArea;
    }

    public void setTotalArea(Long totalArea) {
        this.totalArea = totalArea;
    }

    public Long getTotalArea() {
        return totalArea;
    }

    public void setTechnicalPassport(Boolean technicalPassport) {
        this.technicalPassport = technicalPassport;
    }

    public Boolean getTechnicalPassport() {
        return technicalPassport;
    }

    public void setBuildingPassport(Boolean buildingPassport) {
        this.buildingPassport = buildingPassport;
    }

    public Boolean getBuildingPassport() {
        return buildingPassport;
    }

    public void setTechnicalJournal(Boolean technicalJournal) {
        this.technicalJournal = technicalJournal;
    }

    public Boolean getTechnicalJournal() {
        return technicalJournal;
    }

    public void setInventoryNumber(String inventoryNumber) {
        this.inventoryNumber = inventoryNumber;
    }

    public String getInventoryNumber() {
        return inventoryNumber;
    }

    public void setOldInventoryNumber(String oldInventoryNumber) {
        this.oldInventoryNumber = oldInventoryNumber;
    }

    public String getOldInventoryNumber() {
        return oldInventoryNumber;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCommissioning(Date commissioning) {
        this.commissioning = commissioning;
    }

    public Date getCommissioning() {
        return commissioning;
    }


}