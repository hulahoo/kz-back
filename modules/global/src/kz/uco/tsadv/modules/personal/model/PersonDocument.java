package kz.uco.tsadv.modules.personal.model;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.dictionary.DicApprovalStatus;
import kz.uco.tsadv.modules.personal.dictionary.DicDocumentType;
import kz.uco.tsadv.modules.personal.dictionary.DicIssuingAuthority;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Table(name = "TSADV_PERSON_DOCUMENT")
@Entity(name = "tsadv$PersonDocument")
public class PersonDocument extends AbstractParentEntity implements HasApprovalStatus, HasPersonGroup {
    private static final long serialVersionUID = -5120637632865960188L;

    @Temporal(TemporalType.DATE)
    @Column(name = "ISSUE_DATE", nullable = false)
    protected Date issueDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE")
    private Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE")
    private Date endDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "EXPIRED_DATE", nullable = false)
    protected Date expiredDate;

    @Column(name = "ISSUED_BY", length = 500)
    protected String issuedBy;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ISSUING_AUTHORITY_ID")
    protected DicIssuingAuthority issuingAuthority;

    @Column(name = "DESCRIPTION", length = 2000)
    protected String description;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "DOCUMENT_TYPE_ID")
    protected DicDocumentType documentType;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @NotNull
    @Column(name = "DOCUMENT_NUMBER", nullable = false)
    protected String documentNumber;

    @Column(name = "SERIES")
    protected String series;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "STATUS_ID")
    protected DicApprovalStatus status;

    @OnDeleteInverse(DeletePolicy.UNLINK)
    @OnDelete(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_ID")
    protected FileDescriptor file;

    @OrderBy("name")
    @JoinTable(name = "TSADV_PERSON_DOCUMENT_FILE_DESCRIPTOR_LINK",
            joinColumns = @JoinColumn(name = "PERSON_DOCUMENT_ID"),
            inverseJoinColumns = @JoinColumn(name = "FILE_DESCRIPTOR_ID"))
    @ManyToMany
    private List<FileDescriptor> attachments;

    public List<FileDescriptor> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<FileDescriptor> attachments) {
        this.attachments = attachments;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public DicIssuingAuthority getIssuingAuthority() {
        return issuingAuthority;
    }

    public void setIssuingAuthority(DicIssuingAuthority issuingAuthority) {
        this.issuingAuthority = issuingAuthority;
    }


    public void setSeries(String series) {
        this.series = series;
    }

    public String getSeries() {
        return series;
    }


    public void setFile(FileDescriptor file) {
        this.file = file;
    }

    public FileDescriptor getFile() {
        return file;
    }


    public void setStatus(DicApprovalStatus status) {
        this.status = status;
    }

    public DicApprovalStatus getStatus() {
        return status;
    }


    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }


    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }


    public void setDocumentType(DicDocumentType documentType) {
        this.documentType = documentType;
    }

    public DicDocumentType getDocumentType() {
        return documentType;
    }


    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setExpiredDate(Date expiredDate) {
        this.expiredDate = expiredDate;
    }

    public Date getExpiredDate() {
        return expiredDate;
    }

    public void setIssuedBy(String issuedBy) {
        this.issuedBy = issuedBy;
    }

    public String getIssuedBy() {
        return issuedBy;
    }


    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }


}