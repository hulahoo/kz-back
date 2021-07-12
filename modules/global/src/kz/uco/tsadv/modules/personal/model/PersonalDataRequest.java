package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.annotation.PublishEntityChangedEvents;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.global.dictionary.DicNationality;
import kz.uco.tsadv.modules.personal.dictionary.DicCitizenship;
import kz.uco.tsadv.modules.personal.dictionary.DicMaritalStatus;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@PublishEntityChangedEvents
@Table(name = "TSADV_PERSONAL_DATA_REQUEST")
@Entity(name = "tsadv$PersonalDataRequest")
@NamePattern("%s|personGroup")
public class PersonalDataRequest extends AbstractBprocRequest {
    private static final long serialVersionUID = -1704847249874220670L;

    public static final String PROCESS_DEFINITION_KEY = "personalDataRequest";

    @Column(name = "LAST_NAME")
    protected String lastName;

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
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @OrderBy("name")
    @JoinTable(name = "TSADV_PERSONAL_DATA_REQUEST_FILE_DESCRIPTOR_LINK",
            joinColumns = @JoinColumn(name = "PERSONAL_DATA_REQUEST_ID"),
            inverseJoinColumns = @JoinColumn(name = "FILE_DESCRIPTOR_ID"))
    @ManyToMany
    protected List<FileDescriptor> attachments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NATIONALITY_ID")
    private DicNationality nationality;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CITIZENSHIP_ID")
    private DicCitizenship citizenship;

    @Column(name = "NATIONAL_IDENTIFIER")
    private String nationalIdentifier;

    public String getNationalIdentifier() {
        return nationalIdentifier;
    }

    public void setNationalIdentifier(String nationalIdentifier) {
        this.nationalIdentifier = nationalIdentifier;
    }

    public void setNationality(DicNationality nationality) {
        this.nationality = nationality;
    }

    public DicNationality getNationality() {
        return nationality;
    }

    public DicCitizenship getCitizenship() {
        return citizenship;
    }

    public void setCitizenship(DicCitizenship citizenship) {
        this.citizenship = citizenship;
    }

    public void setAttachments(List<FileDescriptor> attachments) {
        this.attachments = attachments;
    }

    public List<FileDescriptor> getAttachments() {
        return attachments;
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

    @Override
    public String getProcessDefinitionKey() {
        return PROCESS_DEFINITION_KEY;
    }
}