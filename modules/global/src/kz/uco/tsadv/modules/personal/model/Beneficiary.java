package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.dictionary.DicRelationshipType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
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

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE_HISTORY")
    private Date startDateHistory;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE_HISTORY")
    private Date endDateHistory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_ID")
    private PersonGroupExt personGroup;

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