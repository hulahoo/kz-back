package kz.uco.tsadv.modules.personal.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.haulmont.cuba.core.entity.FileDescriptor;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import kz.uco.tsadv.modules.personal.dictionary.DicCostCenter;
import kz.uco.tsadv.modules.personal.enums.PositionChangeRequestType;
import kz.uco.tsadv.modules.personal.group.GradeGroup;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;

@NamePattern("%s|requestNumber")
@Table(name = "TSADV_POSITION_CHANGE_REQUEST")
@Entity(name = "tsadv$PositionChangeRequest")
public class PositionChangeRequest extends AbstractParentEntity {
    private static final long serialVersionUID = -6928777510611563798L;

    @NotNull
    @Column(name = "REQUEST_NUMBER", nullable = false)
    protected Long requestNumber;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "REQUEST_DATE", nullable = false)
    protected Date requestDate;

    @NotNull
    @Column(name = "REQUEST_TYPE", nullable = false)
    protected String requestType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_GROUP_ID")
    protected OrganizationGroupExt organizationGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POSITION_GROUP_ID")
    protected PositionGroupExt positionGroup;

    @Column(name = "JOB_NAME_LANG1", length = 1000)
    protected String jobNameLang1;

    @Column(name = "JOB_NAME_LANG2", length = 1000)
    protected String jobNameLang2;

    @Column(name = "JOB_NAME_LANG3", length = 1000)
    protected String jobNameLang3;

    @Column(name = "LOCATION", length = 500)
    protected String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GRADE_GROUP_ID")
    protected GradeGroup gradeGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COST_CENTER_ID")
    protected DicCostCenter costCenter;

    @Column(name = "FTE")
    protected Integer fte;

    @Temporal(TemporalType.DATE)
    @Column(name = "EFFECTIVE_DATE")
    protected Date effectiveDate;

    @Column(name = "COMMENTS", length = 2000)
    protected String comments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_POSITION_GROUP_ID")
    protected PositionGroupExt parentPositionGroup;

    @NotNull
    @Column(name = "MATERIAL_LIABILITY_AGREEMENT_REQUIRED", nullable = false)
    protected Boolean materialLiabilityAgreementRequired = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MRG_FORM_ID")
    protected FileDescriptor mrgForm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JOB_INSTRUCTION_ID")
    protected FileDescriptor jobInstruction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ATTACHMENT_ID")
    protected FileDescriptor attachment;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STATUS_ID")
    protected DicRequestStatus status;
    public Long getRequestNumber() {
        return requestNumber;
    }

    public void setRequestNumber(Long requestNumber) {
        this.requestNumber = requestNumber;
    }



    public void setStatus(DicRequestStatus status) {
        this.status = status;
    }

    public DicRequestStatus getStatus() {
        return status;
    }


    public void setAttachment(FileDescriptor attachment) {
        this.attachment = attachment;
    }

    public FileDescriptor getAttachment() {
        return attachment;
    }


    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getComments() {
        return comments;
    }




    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestType(PositionChangeRequestType requestType) {
        this.requestType = requestType == null ? null : requestType.getId();
    }

    public PositionChangeRequestType getRequestType() {
        return requestType == null ? null : PositionChangeRequestType.fromId(requestType);
    }

    public void setOrganizationGroup(OrganizationGroupExt organizationGroup) {
        this.organizationGroup = organizationGroup;
    }

    public OrganizationGroupExt getOrganizationGroup() {
        return organizationGroup;
    }

    public void setPositionGroup(PositionGroupExt positionGroup) {
        this.positionGroup = positionGroup;
    }

    public PositionGroupExt getPositionGroup() {
        return positionGroup;
    }

    public void setJobNameLang1(String jobNameLang1) {
        this.jobNameLang1 = jobNameLang1;
    }

    public String getJobNameLang1() {
        return jobNameLang1;
    }

    public void setJobNameLang2(String jobNameLang2) {
        this.jobNameLang2 = jobNameLang2;
    }

    public String getJobNameLang2() {
        return jobNameLang2;
    }

    public void setJobNameLang3(String jobNameLang3) {
        this.jobNameLang3 = jobNameLang3;
    }

    public String getJobNameLang3() {
        return jobNameLang3;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setGradeGroup(GradeGroup gradeGroup) {
        this.gradeGroup = gradeGroup;
    }

    public GradeGroup getGradeGroup() {
        return gradeGroup;
    }

    public void setCostCenter(DicCostCenter costCenter) {
        this.costCenter = costCenter;
    }

    public DicCostCenter getCostCenter() {
        return costCenter;
    }

    public void setFte(Integer fte) {
        this.fte = fte;
    }

    public Integer getFte() {
        return fte;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setParentPositionGroup(PositionGroupExt parentPositionGroup) {
        this.parentPositionGroup = parentPositionGroup;
    }

    public PositionGroupExt getParentPositionGroup() {
        return parentPositionGroup;
    }

    public void setMaterialLiabilityAgreementRequired(Boolean materialLiabilityAgreementRequired) {
        this.materialLiabilityAgreementRequired = materialLiabilityAgreementRequired;
    }

    public Boolean getMaterialLiabilityAgreementRequired() {
        return materialLiabilityAgreementRequired;
    }

    public void setMrgForm(FileDescriptor mrgForm) {
        this.mrgForm = mrgForm;
    }

    public FileDescriptor getMrgForm() {
        return mrgForm;
    }

    public void setJobInstruction(FileDescriptor jobInstruction) {
        this.jobInstruction = jobInstruction;
    }

    public FileDescriptor getJobInstruction() {
        return jobInstruction;
    }

    public String getFullPositionName(String lang) {
        StringBuilder fullName = new StringBuilder();
        fullName.append(lang.equalsIgnoreCase("ru") ?
                (jobNameLang1 != null ? jobNameLang1 : "") :
                (jobNameLang3 != null ? jobNameLang3 : ""));
        if (location != null) {
            fullName.append('.').append(location);
        }
        if (costCenter != null && (lang.equalsIgnoreCase("ru") ? costCenter.getLangValue1() != null : costCenter.getLangValue3() != null)) {
            fullName.append('.')
                    .append(lang.equalsIgnoreCase("ru") ? costCenter.getLangValue1() : costCenter.getLangValue3());
        }
        return fullName.toString();
    }

}