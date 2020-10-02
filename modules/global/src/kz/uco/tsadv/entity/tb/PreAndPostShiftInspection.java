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
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.entity.tb.dictionary.InspectionType;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

@Table(name = "TSADV_PRE_AND_POST_SHIFT_INSPECTION")
@Entity(name = "tsadv$PreAndPostShiftInspection")
public class PreAndPostShiftInspection extends AbstractParentEntity {
    private static final long serialVersionUID = -1621489127728008204L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ORGANIZATION_ID")
    protected OrganizationGroupExt organization;

    @Temporal(TemporalType.DATE)
    @Column(name = "ENTRY_DATE", nullable = false)
    protected Date entryDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "INSPECTION_TYPE_ID")
    protected InspectionType inspectionType;

    @Column(name = "INSPECTED_NUMBER")
    protected Long inspectedNumber;

    @Column(name = "PASSED_PRE_SHIFT_INSPECTION")
    protected Long passedPreShiftInspection;

    @Column(name = "PASSED_POST_SHIFT_INSPECTION")
    protected Long passedPostShiftInspection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HARMFULL_FACTORS_ID")
    protected HarmfullFactors harmfullFactors;

    public void setHarmfullFactors(HarmfullFactors harmfullFactors) {
        this.harmfullFactors = harmfullFactors;
    }

    public HarmfullFactors getHarmfullFactors() {
        return harmfullFactors;
    }


    public InspectionType getInspectionType() {
        return inspectionType;
    }

    public void setInspectionType(InspectionType inspectionType) {
        this.inspectionType = inspectionType;
    }


    public void setOrganization(OrganizationGroupExt organization) {
        this.organization = organization;
    }

    public OrganizationGroupExt getOrganization() {
        return organization;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public void setInspectedNumber(Long inspectedNumber) {
        this.inspectedNumber = inspectedNumber;
    }

    public Long getInspectedNumber() {
        return inspectedNumber;
    }

    public void setPassedPreShiftInspection(Long passedPreShiftInspection) {
        this.passedPreShiftInspection = passedPreShiftInspection;
    }

    public Long getPassedPreShiftInspection() {
        return passedPreShiftInspection;
    }

    public void setPassedPostShiftInspection(Long passedPostShiftInspection) {
        this.passedPostShiftInspection = passedPostShiftInspection;
    }

    public Long getPassedPostShiftInspection() {
        return passedPostShiftInspection;
    }


}