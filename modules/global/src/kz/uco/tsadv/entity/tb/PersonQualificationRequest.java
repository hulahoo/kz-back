package kz.uco.tsadv.entity.tb;

import com.haulmont.cuba.core.entity.FileDescriptor;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.entity.tb.dictionary.DicPersonQualificationType;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Table(name = "TSADV_PERSON_QUALIFICATION_REQUEST")
@Entity(name = "tsadv_PersonQualificationRequest")
public class PersonQualificationRequest extends AbstractParentEntity {
    private static final long serialVersionUID = 4597791528393692563L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TYPE_ID")
    protected DicPersonQualificationType type;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @NotNull
    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE", nullable = false)
    protected Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE")
    protected Date endDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "ASSIGN_VALIDATION_DATE")
    protected Date assignValidationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ATTACHMENT_ID")
    protected FileDescriptor attachment;

    @Column(name = "NOTE", length = 3000)
    protected String note;

    @Column(name = "EDUCATIONAL_INSTITUTION_NAME", length = 2000)
    private String educationalInstitutionName;

    @Column(name = "DIPLOMA", length = 2000)
    private String diploma;

    @Column(name = "TYPE_NAME", length = 2000)
    private String typeName;

    @Temporal(TemporalType.DATE)
    @Column(name = "ISSUED_DATE")
    private Date issuedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUEST_STATUS_ID")
    private DicRequestStatus requestStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_ID")
    private FileDescriptor file;

    @OrderBy("name")
    @JoinTable(name = "TSADV_PERSON_QUALIFICATION_REQUEST_FILE_DESCRIPTOR_LINK",
            joinColumns = @JoinColumn(name = "PERSON_QUALIFICATION_REQUEST_ID"),
            inverseJoinColumns = @JoinColumn(name = "FILE_DESCRIPTOR_ID"))
    @ManyToMany
    private List<FileDescriptor> attachments;

    public List<FileDescriptor> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<FileDescriptor> attachments) {
        this.attachments = attachments;
    }

    public DicRequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(DicRequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    public FileDescriptor getFile() {
        return file;
    }

    public void setFile(FileDescriptor file) {
        this.file = file;
    }

    public Date getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(Date issuedDate) {
        this.issuedDate = issuedDate;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getDiploma() {
        return diploma;
    }

    public void setDiploma(String diploma) {
        this.diploma = diploma;
    }

    public String getEducationalInstitutionName() {
        return educationalInstitutionName;
    }

    public void setEducationalInstitutionName(String educationalInstitutionName) {
        this.educationalInstitutionName = educationalInstitutionName;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setType(DicPersonQualificationType type) {
        this.type = type;
    }

    public DicPersonQualificationType getType() {
        return type;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setAssignValidationDate(Date assignValidationDate) {
        this.assignValidationDate = assignValidationDate;
    }

    public Date getAssignValidationDate() {
        return assignValidationDate;
    }

    public void setAttachment(FileDescriptor attachment) {
        this.attachment = attachment;
    }

    public FileDescriptor getAttachment() {
        return attachment;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getNote() {
        return note;
    }

}