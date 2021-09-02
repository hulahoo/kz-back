package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.base.entity.dictionary.DicCountry;
import kz.uco.tsadv.modules.personal.dictionary.*;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Listeners("tsadv_BeneficiaryListener")
@NamePattern("%s %s|personGroupChild,personGroupParent")
@Table(name = "TSADV_BENEFICIARY")
@Entity(name = "tsadv$Beneficiary")
public class Beneficiary extends AbstractParentEntity {
    private static final long serialVersionUID = 4701244820156979239L;

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
    protected String lastName;

    @Column(name = "FIRST_NAME", length = 2000)
    protected String firstName;

    @Column(name = "MIDDLE_NAME", length = 2000)
    protected String middleName;

    @Temporal(TemporalType.DATE)
    @Column(name = "BIRTH_DATE")
    protected Date birthDate;

    @Column(name = "WORK_LOCATION", length = 2000)
    protected String workLocation;

    @Column(name = "HOME_ADDRESS", length = 2000)
    protected String homeAddress;

    @Column(name = "ADDITIONAL_CONTACT", length = 2000)
    protected String additionalContact;

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE_HISTORY")
    protected Date startDateHistory;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE_HISTORY")
    protected Date endDateHistory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RELATED_PERSON_GROUP_ID")
    private PersonGroupExt relatedPersonGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RELATION_DEGREE_ID")
    private RelationshipTypeBeneficiary relationDegree;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SOC_STATUS_ID")
    protected DicSocStatus socStatus;

    @Column(name = "BENEFICIARY_ADDRESS", length = 2500)
    protected String beneficiaryAddress;

    @Column(name = "BENEFICIARY_PLACE_OF_WORK", length = 2500)
    protected String beneficiaryPlaceOfWork;

    @Column(name = "BENEFICIARY_JOB", length = 2500)
    protected String beneficiaryJob;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ADDRESS_TYPE_ID")
    private DicAddressType addressType;

    @Column(name = "POSTAL_CODE", length = 2500)
    private String postalCode;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COUNTRY_ID")
    private DicCountry country;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ADDRESS_KATO_CODE_ID")
    private DicKato addressKATOCode;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "STREET_TYPE_ID")
    private DicStreetType streetType;

    @Column(name = "STREET_NAME", length = 2500)
    private String streetName;

    @Column(name = "BUILDING", length = 2500)
    private String building;

    @Column(name = "BLOCK", length = 2500)
    private String block;

    @Column(name = "FLAT", length = 2500)
    private String flat;

    @Column(name = "ADDRESS_FOR_EXPATS", length = 2500)
    private String addressForExpats;

    public String getAddressForExpats() {
        return addressForExpats;
    }

    public void setAddressForExpats(String addressForExpats) {
        this.addressForExpats = addressForExpats;
    }

    public String getFlat() {
        return flat;
    }

    public void setFlat(String flat) {
        this.flat = flat;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public DicStreetType getStreetType() {
        return streetType;
    }

    public void setStreetType(DicStreetType streetType) {
        this.streetType = streetType;
    }

    public DicKato getAddressKATOCode() {
        return addressKATOCode;
    }

    public void setAddressKATOCode(DicKato addressKATOCode) {
        this.addressKATOCode = addressKATOCode;
    }

    public DicCountry getCountry() {
        return country;
    }

    public void setCountry(DicCountry country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public DicAddressType getAddressType() {
        return addressType;
    }

    public void setAddressType(DicAddressType addressType) {
        this.addressType = addressType;
    }

    public String getBeneficiaryJob() {
        return beneficiaryJob;
    }

    public void setBeneficiaryJob(String beneficiaryJob) {
        this.beneficiaryJob = beneficiaryJob;
    }

    public String getBeneficiaryPlaceOfWork() {
        return beneficiaryPlaceOfWork;
    }

    public void setBeneficiaryPlaceOfWork(String beneficiaryPlaceOfWork) {
        this.beneficiaryPlaceOfWork = beneficiaryPlaceOfWork;
    }

    public String getBeneficiaryAddress() {
        return beneficiaryAddress;
    }

    public void setBeneficiaryAddress(String beneficiaryAddress) {
        this.beneficiaryAddress = beneficiaryAddress;
    }

    public DicSocStatus getSocStatus() {
        return socStatus;
    }

    public void setSocStatus(DicSocStatus socStatus) {
        this.socStatus = socStatus;
    }

    public RelationshipTypeBeneficiary getRelationDegree() {
        return relationDegree;
    }

    public void setRelationDegree(RelationshipTypeBeneficiary relationDegree) {
        this.relationDegree = relationDegree;
    }

    public PersonGroupExt getRelatedPersonGroup() {
        return relatedPersonGroup;
    }

    public void setRelatedPersonGroup(PersonGroupExt relatedPersonGroup) {
        this.relatedPersonGroup = relatedPersonGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public Date getEndDateHistory() {
        return endDateHistory;
    }

    public void setEndDateHistory(Date endDateHistory) {
        this.endDateHistory = endDateHistory;
    }

    public Date getStartDateHistory() {
        return startDateHistory;
    }

    public void setStartDateHistory(Date startDateHistory) {
        this.startDateHistory = startDateHistory;
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