package kz.uco.tsadv.modules.personal.model;

import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.dictionary.DicRelationshipType;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import java.util.Date;

@Table(name = "TSADV_BENEFICIARY_REQUEST")
@Entity(name = "tsadv_BeneficiaryRequest")
public class BeneficiaryRequest extends AbstractParentEntity {
    private static final long serialVersionUID = 2706977564322639247L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_PARENT_ID")
    protected PersonGroupExt personGroupParent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_CHILD_ID")
    protected PersonGroupExt personGroupChild;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_FROM")
    protected Date dateFrom;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_TO")
    protected Date dateTo;

    @Column(name = "GET_ALIMONY")
    protected Boolean getAlimony;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RELATIONSHIP_TYPE_ID")
    protected DicRelationshipType relationshipType;

    @Column(name = "LAST_NAME", length = 2000)
    private String lastName;

    @Column(name = "FIRST_NAME", length = 2000)
    private String firstName;

    @Column(name = "MIDDLE_NAME", length = 2000)
    private String middleName;

    @Temporal(TemporalType.DATE)
    @Column(name = "BIRTH_DATE")
    private Date birthDate;

    @Column(name = "WORK_LOCATION", length = 2000)
    private String workLocation;

    @Column(name = "HOME_ADDRESS", length = 2000)
    private String homeAddress;

    @Column(name = "ADDITIONAL_CONTACT", length = 2000)
    private String additionalContact;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_ID")
    private PersonGroupExt personGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUEST_STATUS_ID")
    private DicRequestStatus requestStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BENEFICIARY_ID")
    private Beneficiary beneficiary;

    public Beneficiary getBeneficiary() {
        return beneficiary;
    }

    public void setBeneficiary(Beneficiary beneficiary) {
        this.beneficiary = beneficiary;
    }

    public DicRequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(DicRequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public String getAdditionalContact() {
        return additionalContact;
    }

    public void setAdditionalContact(String additionalContact) {
        this.additionalContact = additionalContact;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getWorkLocation() {
        return workLocation;
    }

    public void setWorkLocation(String workLocation) {
        this.workLocation = workLocation;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleNmae) {
        this.middleName = middleNmae;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public DicRelationshipType getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(DicRelationshipType relationshipType) {
        this.relationshipType = relationshipType;
    }


    public PersonGroupExt getPersonGroupChild() {
        return personGroupChild;
    }

    public void setPersonGroupChild(PersonGroupExt personGroupChild) {
        this.personGroupChild = personGroupChild;
    }


    public void setPersonGroupParent(PersonGroupExt personGroupParent) {
        this.personGroupParent = personGroupParent;
    }

    public PersonGroupExt getPersonGroupParent() {
        return personGroupParent;
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

    public void setGetAlimony(Boolean getAlimony) {
        this.getAlimony = getAlimony;
    }

    public Boolean getGetAlimony() {
        return getAlimony;
    }

}