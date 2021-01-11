package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.Categorized;
import com.haulmont.cuba.core.entity.Category;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.annotation.*;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.abstraction.IGroupedEntity;
import kz.uco.base.entity.shared.Person;
import kz.uco.tsadv.global.dictionary.DicNationality;
import kz.uco.tsadv.modules.personal.dictionary.*;
import kz.uco.tsadv.modules.personal.enums.YesNoEnum;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

import static kz.uco.base.common.Null.isNotEmpty;

@Listeners("tsadv_PersonExtListener")
@NamePattern("%s %s %s |lastName,firstName,middleName,employeeNumber")
@Extends(Person.class)
@Entity(name = "base$PersonExt")
public class PersonExt extends Person implements Categorized, IGroupedEntity<PersonGroupExt> {
    protected static final long serialVersionUID = -6341957289240331481L;

    @Transient
    @MetaProperty
    protected List<PersonGroupExt> children;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_ID")
    protected Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NONRESIDENT_TYPE_ID")
    protected DicNonresidentType nonresidentType;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_OF_DEATH")
    protected Date dateOfDeath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID")
    protected PersonGroupExt group;

    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "clear"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MARITAL_STATUS_ID")
    protected DicMaritalStatus maritalStatus;

    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "clear"})
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TYPE_ID")
    protected DicPersonType type;

    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "clear"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NATIONALITY_ID")
    protected DicNationality nationality;

    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "clear"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CITIZENSHIP_ID")
    protected DicCitizenship citizenship;

    @Column(name = "FULL_NAME_CYRILLIC")
    protected String fullNameCyrillic;

    @Column(name = "FULL_NAME_NUMBER_CYRILLIC")
    protected String fullNameNumberCyrillic;

    @Column(name = "BIRTH_PLACE", length = 2000)
    protected String birthPlace;

    @Column(name = "ACADEMIC_DEGREE", length = 2000)
    protected String academicDegree;

    @Column(name = "SCIENTIFIC_WORKS_IVENTIONS", length = 2000)
    protected String scientificWorksIventions;

    @Column(name = "STATE_AWARDS", length = 2000)
    protected String stateAwards;

    @Column(name = "HAVE_CRIPPLE_CHILD")
    protected String haveCrippleChild;

    @Column(name = "HAVE_CHILD_WITHOUT_PARENT")
    protected String haveChildWithoutParent;

    @Column(name = "PREV_JOB_NDA")
    protected String prevJobNDA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PREV_JOB_OBLIGATION_ID")
    protected DicPrevJobObligation prevJobObligation;

    @OrderBy("name")
    @JoinTable(name = "TSADV_PERSON_EXT_FILE_DESCRIPTOR_LINK",
            joinColumns = @JoinColumn(name = "PERSON_EXT_ID"),
            inverseJoinColumns = @JoinColumn(name = "FILE_DESCRIPTOR_ID"))
    @ManyToMany
    private List<FileDescriptor> attachments;

    public List<FileDescriptor> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<FileDescriptor> attachments) {
        this.attachments = attachments;
    }

    public DicPrevJobObligation getPrevJobObligation() {
        return prevJobObligation;
    }

    public void setPrevJobObligation(DicPrevJobObligation prevJobObligation) {
        this.prevJobObligation = prevJobObligation;
    }

    public YesNoEnum getPrevJobNDA() {
        return prevJobNDA == null ? null : YesNoEnum.fromId(prevJobNDA);
    }

    public void setPrevJobNDA(YesNoEnum prevJobNDA) {
        this.prevJobNDA = prevJobNDA == null ? null : prevJobNDA.getId();
    }

    public YesNoEnum getHaveChildWithoutParent() {
        return haveChildWithoutParent == null ? null : YesNoEnum.fromId(haveChildWithoutParent);
    }

    public void setHaveChildWithoutParent(YesNoEnum haveChildWithoutParent) {
        this.haveChildWithoutParent = haveChildWithoutParent == null ? null : haveChildWithoutParent.getId();
    }

    public YesNoEnum getHaveCrippleChild() {
        return haveCrippleChild == null ? null : YesNoEnum.fromId(haveCrippleChild);
    }

    public void setHaveCrippleChild(YesNoEnum haveCrippleChild) {
        this.haveCrippleChild = haveCrippleChild == null ? null : haveCrippleChild.getId();
    }

    public String getStateAwards() {
        return stateAwards;
    }

    public void setStateAwards(String stateAwards) {
        this.stateAwards = stateAwards;
    }

    public String getScientificWorksIventions() {
        return scientificWorksIventions;
    }

    public void setScientificWorksIventions(String scientificWorksIventions) {
        this.scientificWorksIventions = scientificWorksIventions;
    }

    public String getAcademicDegree() {
        return academicDegree;
    }

    public void setAcademicDegree(String academicDegree) {
        this.academicDegree = academicDegree;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public void setFullNameNumberCyrillic(String fullNameNumberCyrillic) {
        this.fullNameNumberCyrillic = fullNameNumberCyrillic;
    }

    public String getFullNameNumberCyrillic() {
        return fullNameNumberCyrillic;
    }

    public void setFullNameCyrillic(String fullNameCyrillic) {
        this.fullNameCyrillic = fullNameCyrillic;
    }

    public String getFullNameCyrillic() {
        return fullNameCyrillic;
    }

    @Override
    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public Category getCategory() {
        return category;
    }

    public void setNonresidentType(DicNonresidentType nonresidentType) {
        this.nonresidentType = nonresidentType;
    }

    public DicNonresidentType getNonresidentType() {
        return nonresidentType;
    }

    public void setDateOfDeath(Date dateOfDeath) {
        this.dateOfDeath = dateOfDeath;
    }

    public Date getDateOfDeath() {
        return dateOfDeath;
    }

    public void setImage(FileDescriptor image) {
        this.image = image;
    }

    public FileDescriptor getImage() {
        return image;
    }


    public void setLegacyId(String legacyId) {
        this.legacyId = legacyId;
    }

    public String getLegacyId() {
        return legacyId;
    }

    public List<PersonGroupExt> getChildren() {
        return children;
    }

    public void setChildren(List<PersonGroupExt> children) {
        this.children = children;
    }

    public void setGroup(PersonGroupExt group) {
        this.group = group;
    }

    public PersonGroupExt getGroup() {
        return group;
    }

    public DicPersonType getType() {
        return type;
    }

    public void setType(DicPersonType type) {
        this.type = type;
    }

    public DicNationality getNationality() {
        return nationality;
    }

    public void setNationality(DicNationality nationality) {
        this.nationality = nationality;
    }

    public DicMaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(DicMaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public DicCitizenship getCitizenship() {
        return citizenship;
    }

    public void setCitizenship(DicCitizenship citizenship) {
        this.citizenship = citizenship;
    }

    @MetaProperty(related = {"lastName", "firstName", "middleName", "firstNameLatin", "lastNameLatin", "employeeNumber"})
    @Transient
    public String getFioWithEmployeeNumberWithSortSupported() {
        StringBuilder builder = new StringBuilder(getFullName());
        if (employeeNumber != null) builder.append("(").append(employeeNumber).append(")");
        return builder.toString();
    }

    public String calculateFullNameCyrillic() {     // todo перенести в base
        StringBuilder fullNameCyrillic = new StringBuilder(lastName);
        fullNameCyrillic.append(" ")
                .append(firstName);
        if (isNotEmpty(middleName)) {
            fullNameCyrillic.append(" ")
                    .append(middleName);
        }
        return fullNameCyrillic.toString();
    }

    public String calculateFullNameNumberCyrillic() {      // todo перенести в base
        StringBuilder result = new StringBuilder(calculateFullNameCyrillic());
        if (isNotEmpty(employeeNumber)) {
            result.append(" (")
                    .append(employeeNumber)
                    .append(")");
        }
        return result.toString();
    }


}