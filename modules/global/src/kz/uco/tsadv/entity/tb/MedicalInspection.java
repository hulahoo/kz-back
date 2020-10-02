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
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import java.util.List;
import javax.persistence.OneToMany;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;

@Table(name = "TSADV_MEDICAL_INSPECTION")
@Entity(name = "tsadv$MedicalInspection")
public class MedicalInspection extends AbstractParentEntity {
    private static final long serialVersionUID = 9068970682417205902L;

    @Temporal(TemporalType.DATE)
    @Column(name = "ENTRY_DATE", nullable = false)
    protected Date entryDate;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "medicalInspection")
    protected List<MedicalService> service;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "medicalInspection")
    protected List<Attachment> attachment;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "medicalInspection")
    protected List<MedicalInspectionResult> result;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_ID")
    protected OrganizationGroupExt organization;

    @Column(name = "FACT_EMP_NUMBER")
    protected Long factEmpNumber;

    @Column(name = "SUBJECT_INSPECTION")
    protected Long subjectInspection;

    @Column(name = "SUBJECT_INSPECTION_WOMAN")
    protected Long subjectInspectionWoman;

    @Column(name = "PASSED_INSPECTION")
    protected Long passedInspection;

    public OrganizationGroupExt getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationGroupExt organization) {
        this.organization = organization;
    }


    public void setService(List<MedicalService> service) {
        this.service = service;
    }

    public List<MedicalService> getService() {
        return service;
    }

    public void setAttachment(List<Attachment> attachment) {
        this.attachment = attachment;
    }

    public List<Attachment> getAttachment() {
        return attachment;
    }

    public void setResult(List<MedicalInspectionResult> result) {
        this.result = result;
    }

    public List<MedicalInspectionResult> getResult() {
        return result;
    }


    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public void setFactEmpNumber(Long factEmpNumber) {
        this.factEmpNumber = factEmpNumber;
    }

    public Long getFactEmpNumber() {
        return factEmpNumber;
    }

    public void setSubjectInspection(Long subjectInspection) {
        this.subjectInspection = subjectInspection;
    }

    public Long getSubjectInspection() {
        return subjectInspection;
    }

    public void setSubjectInspectionWoman(Long subjectInspectionWoman) {
        this.subjectInspectionWoman = subjectInspectionWoman;
    }

    public Long getSubjectInspectionWoman() {
        return subjectInspectionWoman;
    }

    public void setPassedInspection(Long passedInspection) {
        this.passedInspection = passedInspection;
    }

    public Long getPassedInspection() {
        return passedInspection;
    }


}