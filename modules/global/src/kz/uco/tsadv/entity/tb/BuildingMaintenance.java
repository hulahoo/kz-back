package kz.uco.tsadv.entity.tb;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

@Table(name = "TSADV_BUILDING_MAINTENANCE")
@Entity(name = "tsadv$BuildingMaintenance")
public class BuildingMaintenance extends AbstractParentEntity {
    private static final long serialVersionUID = -727610389794508455L;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MANAGER_FULL_NAME_ID")
    protected PersonGroupExt managerFullName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RESPONSIBLE_FULL_NAME_ID")
    protected PersonGroupExt responsibleFullName;

    @Column(name = "INSPECTION_REPORT")
    protected String inspectionReport;

    @Column(name = "TECHNICAL_RESOLUTION")
    protected String technicalResolution;

    @Column(name = "MAINTENANCE_PROHIBITION")
    protected Boolean maintenanceProhibition;

    @Column(name = "TECHNICAL_JOURNAL_RECORD")
    protected String technicalJournalRecord;

    @Column(name = "CURRENT_REPAIRS")
    protected String currentRepairs;

    @Column(name = "REBUILDING")
    protected String rebuilding;

    @Column(name = "CONTRACTING_ORGANIZATION")
    protected String contractingOrganization;

    @Column(name = "ON_BALANCE")
    protected Boolean onBalance;

    @Column(name = "DISMANTLED")
    protected Boolean dismantled;

    @Column(name = "UNMAINTENANCE")
    protected Boolean unmaintenance;

    @Column(name = "ON_CONSERVATION")
    protected Boolean onConservation;

    @Temporal(TemporalType.DATE)
    @Column(name = "WRITE_OF_DATE")
    protected Date writeOfDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "DISMANTLING_DATE")
    protected Date dismantlingDate;




    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BUILDINGS_ID")
    protected Buildings buildings;

    public void setBuildings(Buildings buildings) {
        this.buildings = buildings;
    }

    public Buildings getBuildings() {
        return buildings;
    }


    public PersonGroupExt getManagerFullName() {
        return managerFullName;
    }

    public void setManagerFullName(PersonGroupExt managerFullName) {
        this.managerFullName = managerFullName;
    }


    public PersonGroupExt getResponsibleFullName() {
        return responsibleFullName;
    }

    public void setResponsibleFullName(PersonGroupExt responsibleFullName) {
        this.responsibleFullName = responsibleFullName;
    }



    public void setInspectionReport(String inspectionReport) {
        this.inspectionReport = inspectionReport;
    }

    public String getInspectionReport() {
        return inspectionReport;
    }

    public void setTechnicalResolution(String technicalResolution) {
        this.technicalResolution = technicalResolution;
    }

    public String getTechnicalResolution() {
        return technicalResolution;
    }

    public void setMaintenanceProhibition(Boolean maintenanceProhibition) {
        this.maintenanceProhibition = maintenanceProhibition;
    }

    public Boolean getMaintenanceProhibition() {
        return maintenanceProhibition;
    }

    public void setTechnicalJournalRecord(String technicalJournalRecord) {
        this.technicalJournalRecord = technicalJournalRecord;
    }

    public String getTechnicalJournalRecord() {
        return technicalJournalRecord;
    }

    public void setCurrentRepairs(String currentRepairs) {
        this.currentRepairs = currentRepairs;
    }

    public String getCurrentRepairs() {
        return currentRepairs;
    }

    public void setContractingOrganization(String contractingOrganization) {
        this.contractingOrganization = contractingOrganization;
    }

    public String getContractingOrganization() {
        return contractingOrganization;
    }

    public void setOnBalance(Boolean onBalance) {
        this.onBalance = onBalance;
    }

    public Boolean getOnBalance() {
        return onBalance;
    }

    public void setOnConservation(Boolean onConservation) {
        this.onConservation = onConservation;
    }

    public Boolean getOnConservation() {
        return onConservation;
    }

    public void setWriteOfDate(Date writeOfDate) {
        this.writeOfDate = writeOfDate;
    }

    public Date getWriteOfDate() {
        return writeOfDate;
    }

    public void setDismantlingDate(Date dismantlingDate) {
        this.dismantlingDate = dismantlingDate;
    }

    public Date getDismantlingDate() {
        return dismantlingDate;
    }


    public void setRebuilding(String rebuilding) {
        this.rebuilding = rebuilding;
    }

    public String getRebuilding() {
        return rebuilding;
    }

    public void setDismantled(Boolean dismantled) {
        this.dismantled = dismantled;
    }

    public Boolean getDismantled() {
        return dismantled;
    }

    public void setUnmaintenance(Boolean unmaintenance) {
        this.unmaintenance = unmaintenance;
    }

    public Boolean getUnmaintenance() {
        return unmaintenance;
    }


}