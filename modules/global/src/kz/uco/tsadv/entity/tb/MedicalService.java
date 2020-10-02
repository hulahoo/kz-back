package kz.uco.tsadv.entity.tb;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Table(name = "TSADV_MEDICAL_SERVICE")
@Entity(name = "tsadv$MedicalService")
public class MedicalService extends AbstractParentEntity {
    private static final long serialVersionUID = -4545932386128780844L;

    @Temporal(TemporalType.DATE)
    @Column(name = "AGREEMENT_DATE", nullable = false)
    protected Date agreementDate;

    @Column(name = "AGREEMENT_NUMBER", nullable = false)
    protected Long agreementNumber;

    @Column(name = "MEDICAL_CENTER")
    protected String medicalCenter;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_FROM")
    protected Date dateFrom;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_TO")
    protected Date dateTo;

    @Column(name = "COST")
    protected BigDecimal cost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEDICAL_INSPECTION_ID")
    protected MedicalInspection medicalInspection;

    public void setMedicalInspection(MedicalInspection medicalInspection) {
        this.medicalInspection = medicalInspection;
    }

    public MedicalInspection getMedicalInspection() {
        return medicalInspection;
    }


    public void setAgreementDate(Date agreementDate) {
        this.agreementDate = agreementDate;
    }

    public Date getAgreementDate() {
        return agreementDate;
    }

    public void setAgreementNumber(Long agreementNumber) {
        this.agreementNumber = agreementNumber;
    }

    public Long getAgreementNumber() {
        return agreementNumber;
    }

    public void setMedicalCenter(String medicalCenter) {
        this.medicalCenter = medicalCenter;
    }

    public String getMedicalCenter() {
        return medicalCenter;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public BigDecimal getCost() {
        return cost;
    }


}