package kz.uco.tsadv.modules.personal.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import com.haulmont.cuba.core.entity.StandardEntity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import kz.uco.base.entity.abstraction.AbstractTimeBasedEntity;
import kz.uco.tsadv.modules.personal.dictionary.DicMaritalStatus;
import com.haulmont.cuba.core.entity.FileDescriptor;
import java.util.Date;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import java.util.List;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;

@Table(name = "TSADV_PERSONAL_DATA_REQUEST")
@Entity(name = "tsadv$PersonalDataRequest")
public class PersonalDataRequest extends StandardEntity {
    private static final long serialVersionUID = -1704847249874220670L;

    @Column(name = "LAST_NAME")
    protected String lastName;

    @Column(name = "REQUEST_NUMBER")
    protected Long requestNumber;

    @Column(name = "FIRST_NAME")
    protected String firstName;

    @Column(name = "MIDDLE_NAME")
    protected String middleName;

    @Column(name = "LAST_NAME_LATIN")
    protected String lastNameLatin;

    @Column(name = "FIRST_NAME_LATIN")
    protected String firstNameLatin;

    @Column(name = "MIDDLE_NAME_LATIN")
    protected String middleNameLatin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MARITAL_STATUS_ID")
    protected DicMaritalStatus maritalStatus;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_OF_BIRTH")
    protected Date dateOfBirth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ATTACHMENT_ID")
    protected FileDescriptor attachment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STATUS_ID")
    protected DicRequestStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @OrderBy("name")
    @JoinTable(name = "TSADV_PERSONAL_DATA_REQUEST_FILE_DESCRIPTOR_LINK",
        joinColumns = @JoinColumn(name = "PERSONAL_DATA_REQUEST_ID"),
        inverseJoinColumns = @JoinColumn(name = "FILE_DESCRIPTOR_ID"))
    @ManyToMany
    protected List<FileDescriptor> attachments;

    public void setAttachments(List<FileDescriptor> attachments) {
        this.attachments = attachments;
    }

    public List<FileDescriptor> getAttachments() {
        return attachments;
    }

    public void setRequestNumber(Long requestNumber) {
        this.requestNumber = requestNumber;
    }

    public Long getRequestNumber() {
        return requestNumber;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setAttachment(FileDescriptor attachment) {
        this.attachment = attachment;
    }

    public FileDescriptor getAttachment() {
        return attachment;
    }

    public void setStatus(DicRequestStatus status) {
        this.status = status;
    }

    public DicRequestStatus getStatus() {
        return status;
    }

    public void setMaritalStatus(DicMaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public DicMaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setLastNameLatin(String lastNameLatin) {
        this.lastNameLatin = lastNameLatin;
    }

    public String getLastNameLatin() {
        return lastNameLatin;
    }

    public void setFirstNameLatin(String firstNameLatin) {
        this.firstNameLatin = firstNameLatin;
    }

    public String getFirstNameLatin() {
        return firstNameLatin;
    }

    public void setMiddleNameLatin(String middleNameLatin) {
        this.middleNameLatin = middleNameLatin;
    }

    public String getMiddleNameLatin() {
        return middleNameLatin;
    }

}