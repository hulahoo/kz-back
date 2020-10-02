package kz.uco.tsadv.entity.tb;

import javax.persistence.*;
import java.util.Date;

import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.entity.tb.dictionary.TechnicalStatusDictionary;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.entity.tb.dictionary.EventType;
import kz.uco.tsadv.entity.tb.dictionary.InspectionCategory;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import java.util.List;

@Table(name = "TSADV_BUILDING_TECHNICAL_INSPECTIONS")
@Entity(name = "tsadv$BuildingTechnicalInspections")
public class BuildingTechnicalInspections extends AbstractParentEntity {
    private static final long serialVersionUID = 5794120043488852002L;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INSPECTION_CATEGORY_ID")
    protected InspectionCategory inspectionCategory;


    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "buildingTechnicalInspections")
    protected List<BuildingFireSafety> buildingFireSafety;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "INSPECTION_TYPE_ID")
    protected EventType inspectionType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "INSPECTING_FULL_NAME_ID")
    protected PersonGroupExt inspectingFullName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "BUILD_TECHNICAL_STATUS_ID")
    protected TechnicalStatusDictionary buildTechnicalStatus;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TECHNICAL_STATUS_BUILDSTRUCTURES_ID")
    protected TechnicalStatusDictionary technicalStatusBuildstructures;

    @Temporal(TemporalType.DATE)
    @Column(name = "INSPECTION_DATE", nullable = false)
    protected Date inspectionDate;

    @Column(name = "PRESCRIPTION_NUMBER", nullable = false)
    protected Long prescriptionNumber;

    @Lob
    @Column(name = "DAMAGE_DESCRIPTION")
    protected String damageDescription;

    @Lob
    @Column(name = "RECOMMENDATIONS")
    protected String recommendations;

    @Lob
    @Column(name = "TECHNICAL_JOURNAL_RECORD")
    protected String technicalJournalRecord;

    @Temporal(TemporalType.DATE)
    @Column(name = "PREPARATION_DEADLINE")
    protected Date preparationDeadline;

    @Column(name = "ELIMINATE_EVENT")
    protected Boolean eliminateEvent;

    @Temporal(TemporalType.DATE)
    @Column(name = "ELIMINATION_DEADLINE")
    protected Date eliminationDeadline;

    @Column(name = "ELIMINATION_OR_POSTPONEMENT_NOTIFICATION")
    protected Boolean eliminationOrPostponementNotification;

    @Column(name = "REPAIR_PROGRAM")
    protected Boolean repairProgram;




    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BUILDINGS_ID")
    protected Buildings buildings;




    public void setBuildingFireSafety(List<BuildingFireSafety> buildingFireSafety) {
        this.buildingFireSafety = buildingFireSafety;
    }

    public List<BuildingFireSafety> getBuildingFireSafety() {
        return buildingFireSafety;
    }


    public void setBuildings(Buildings buildings) {
        this.buildings = buildings;
    }

    public Buildings getBuildings() {
        return buildings;
    }


    public void setInspectionCategory(InspectionCategory inspectionCategory) {
        this.inspectionCategory = inspectionCategory;
    }

    public InspectionCategory getInspectionCategory() {
        return inspectionCategory;
    }

    public void setInspectionType(EventType inspectionType) {
        this.inspectionType = inspectionType;
    }

    public EventType getInspectionType() {
        return inspectionType;
    }


    public void setInspectingFullName(PersonGroupExt inspectingFullName) {
        this.inspectingFullName = inspectingFullName;
    }

    public PersonGroupExt getInspectingFullName() {
        return inspectingFullName;
    }


    public TechnicalStatusDictionary getBuildTechnicalStatus() {
        return buildTechnicalStatus;
    }

    public void setBuildTechnicalStatus(TechnicalStatusDictionary buildTechnicalStatus) {
        this.buildTechnicalStatus = buildTechnicalStatus;
    }


    public TechnicalStatusDictionary getTechnicalStatusBuildstructures() {
        return technicalStatusBuildstructures;
    }

    public void setTechnicalStatusBuildstructures(TechnicalStatusDictionary technicalStatusBuildstructures) {
        this.technicalStatusBuildstructures = technicalStatusBuildstructures;
    }



    public void setPrescriptionNumber(Long prescriptionNumber) {
        this.prescriptionNumber = prescriptionNumber;
    }

    public Long getPrescriptionNumber() {
        return prescriptionNumber;
    }

    public void setDamageDescription(String damageDescription) {
        this.damageDescription = damageDescription;
    }

    public String getDamageDescription() {
        return damageDescription;
    }

    public void setTechnicalJournalRecord(String technicalJournalRecord) {
        this.technicalJournalRecord = technicalJournalRecord;
    }

    public String getTechnicalJournalRecord() {
        return technicalJournalRecord;
    }

    public void setPreparationDeadline(Date preparationDeadline) {
        this.preparationDeadline = preparationDeadline;
    }

    public Date getPreparationDeadline() {
        return preparationDeadline;
    }

    public void setEliminateEvent(Boolean eliminateEvent) {
        this.eliminateEvent = eliminateEvent;
    }

    public Boolean getEliminateEvent() {
        return eliminateEvent;
    }

    public void setEliminationDeadline(Date eliminationDeadline) {
        this.eliminationDeadline = eliminationDeadline;
    }

    public Date getEliminationDeadline() {
        return eliminationDeadline;
    }

    public void setEliminationOrPostponementNotification(Boolean eliminationOrPostponementNotification) {
        this.eliminationOrPostponementNotification = eliminationOrPostponementNotification;
    }

    public Boolean getEliminationOrPostponementNotification() {
        return eliminationOrPostponementNotification;
    }

    public void setRepairProgram(Boolean repairProgram) {
        this.repairProgram = repairProgram;
    }

    public Boolean getRepairProgram() {
        return repairProgram;
    }


    public void setInspectionDate(Date inspectionDate) {
        this.inspectionDate = inspectionDate;
    }

    public Date getInspectionDate() {
        return inspectionDate;
    }


    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
    }

    public String getRecommendations() {
        return recommendations;
    }


}