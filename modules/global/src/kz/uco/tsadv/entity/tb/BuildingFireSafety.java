package kz.uco.tsadv.entity.tb;

import javax.persistence.*;

import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.entity.tb.dictionary.DicFireResistance;
import kz.uco.tsadv.entity.tb.dictionary.DicFireSafetyCategory;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import java.util.List;

@Table(name = "TSADV_BUILDING_FIRE_SAFETY")
@Entity(name = "tsadv$BuildingFireSafety")
public class BuildingFireSafety extends AbstractParentEntity {
    private static final long serialVersionUID = 8572342754332287977L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FS_BUILDING_CATEGORY_ID")
    protected DicFireSafetyCategory fsBuildingCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FS_OUTDOOR_CATEGORY_ID")
    protected DicFireSafetyCategory fsOutdoorCategory;

    @Lob
    @Column(name = "AGREEMENT")
    protected String agreement;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "buildingFireSafety")
    protected List<FireWaterSupply> fireWaterSupply;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FIRE_RESISTANCE_ID")
    protected DicFireResistance fireResistance;

    @Column(name = "FIRE_AUTOMATION_REQUIRED", nullable = false)
    protected Boolean fireAutomationRequired = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BUILDING_TECHNICAL_INSPECTIONS_ID")
    protected BuildingTechnicalInspections buildingTechnicalInspections;

    public void setAgreement(String agreement) {
        this.agreement = agreement;
    }

    public String getAgreement() {
        return agreement;
    }


    public void setFireWaterSupply(List<FireWaterSupply> fireWaterSupply) {
        this.fireWaterSupply = fireWaterSupply;
    }

    public List<FireWaterSupply> getFireWaterSupply() {
        return fireWaterSupply;
    }


    public void setBuildingTechnicalInspections(BuildingTechnicalInspections buildingTechnicalInspections) {
        this.buildingTechnicalInspections = buildingTechnicalInspections;
    }

    public BuildingTechnicalInspections getBuildingTechnicalInspections() {
        return buildingTechnicalInspections;
    }


    public void setFsBuildingCategory(DicFireSafetyCategory fsBuildingCategory) {
        this.fsBuildingCategory = fsBuildingCategory;
    }

    public DicFireSafetyCategory getFsBuildingCategory() {
        return fsBuildingCategory;
    }

    public void setFsOutdoorCategory(DicFireSafetyCategory fsOutdoorCategory) {
        this.fsOutdoorCategory = fsOutdoorCategory;
    }

    public DicFireSafetyCategory getFsOutdoorCategory() {
        return fsOutdoorCategory;
    }

    public void setFireResistance(DicFireResistance fireResistance) {
        this.fireResistance = fireResistance;
    }

    public DicFireResistance getFireResistance() {
        return fireResistance;
    }


    public void setFireAutomationRequired(Boolean fireAutomationRequired) {
        this.fireAutomationRequired = fireAutomationRequired;
    }

    public Boolean getFireAutomationRequired() {
        return fireAutomationRequired;
    }


}