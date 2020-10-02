package kz.uco.tsadv.entity.tb;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;
import javax.persistence.Column;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Table(name = "TSADV_MEDICAL_INSPECTION_RESULT")
@Entity(name = "tsadv$MedicalInspectionResult")
public class MedicalInspectionResult extends AbstractParentEntity {
    private static final long serialVersionUID = 4296557789182519897L;

    @Column(name = "WITH_DISEASES", nullable = false)
    protected Integer withDiseases;

    @Column(name = "RISK_GROUP")
    protected Integer riskGroup;

    @Column(name = "TRADEUNION_CENTER_SENT")
    protected Integer tradeunionCenterSent;

    @Column(name = "TEMPORARY_UNFIT")
    protected Integer temporaryUnfit;

    @Column(name = "CONSTANTLY_UNFIT")
    protected Integer constantlyUnfit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEDICAL_INSPECTION_ID")
    protected MedicalInspection medicalInspection;

    public void setMedicalInspection(MedicalInspection medicalInspection) {
        this.medicalInspection = medicalInspection;
    }

    public MedicalInspection getMedicalInspection() {
        return medicalInspection;
    }


    public void setWithDiseases(Integer withDiseases) {
        this.withDiseases = withDiseases;
    }

    public Integer getWithDiseases() {
        return withDiseases;
    }

    public void setRiskGroup(Integer riskGroup) {
        this.riskGroup = riskGroup;
    }

    public Integer getRiskGroup() {
        return riskGroup;
    }

    public void setTradeunionCenterSent(Integer tradeunionCenterSent) {
        this.tradeunionCenterSent = tradeunionCenterSent;
    }

    public Integer getTradeunionCenterSent() {
        return tradeunionCenterSent;
    }

    public void setTemporaryUnfit(Integer temporaryUnfit) {
        this.temporaryUnfit = temporaryUnfit;
    }

    public Integer getTemporaryUnfit() {
        return temporaryUnfit;
    }

    public void setConstantlyUnfit(Integer constantlyUnfit) {
        this.constantlyUnfit = constantlyUnfit;
    }

    public Integer getConstantlyUnfit() {
        return constantlyUnfit;
    }


}