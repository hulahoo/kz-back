package kz.uco.tsadv.modules.personal.model;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.dictionary.DicApprovalStatus;
import kz.uco.tsadv.modules.personal.dictionary.DicDocumentType;
import kz.uco.tsadv.modules.personal.dictionary.DicIssuingAuthority;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Table(name = "TSADV_PERSON_DOCUMENT_REQUEST")
@Entity(name = "tsadv_PersonDocumentRequest")
public class PersonDocumentRequest extends AbstractParentEntity {
    private static final long serialVersionUID = -4050457943892676964L;

    @Temporal(TemporalType.DATE)
    @Column(name = "ISSUE_DATE", nullable = false)
    protected Date issueDate;

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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "REQUEST_STATUS_ID")
    @NotNull
    private DicRequestStatus requestStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EDITED_PERSON_DOCUMENT_ID")
    private PersonDocument editedPersonDocument;

    public PersonDocument getEditedPersonDocument() {
        return editedPersonDocument;
    }

    public void setEditedPersonDocument(PersonDocument editedPersonDocument) {
        this.editedPersonDocument = editedPersonDocument;
    }

    public void setRequestStatus(DicRequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    public DicRequestStatus getRequestStatus() {
        return requestStatus;
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
