package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.Categorized;
import com.haulmont.cuba.core.entity.Category;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.annotation.*;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.core.sys.AppContext;
import kz.uco.base.entity.abstraction.IGroupedEntity;
import kz.uco.base.entity.shared.Person;
import kz.uco.tsadv.global.dictionary.DicNationality;
import kz.uco.tsadv.modules.personal.dictionary.DicCitizenship;
import kz.uco.tsadv.modules.personal.dictionary.DicMaritalStatus;
import kz.uco.tsadv.modules.personal.dictionary.DicNonresidentType;
import kz.uco.tsadv.modules.personal.dictionary.DicPersonType;
import kz.uco.tsadv.modules.personal.enums.YesNoEnum;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static kz.uco.base.common.Null.isNotEmpty;

@Listeners("tsadv_PersonExtListener")
@NamePattern("%s|personName,lastName,firstName,middleName,employeeNumber,endDate,startDate,firstNameLatin,lastNameLatin,middleNameLatin")
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

    @Column(name = "PREV_JOB_OBLIGATION")
    protected String prevJobObligation;

    @OrderBy("name")
    @JoinTable(name = "TSADV_PERSON_EXT_FILE_DESCRIPTOR_LINK",
            joinColumns = @JoinColumn(name = "PERSON_EXT_ID"),
            inverseJoinColumns = @JoinColumn(name = "FILE_DESCRIPTOR_ID"))
    @ManyToMany
    private List<FileDescriptor> attachments;

    @Column(name = "COMMITMENTS_FROM_PREV_JOB")
    protected String commitmentsFromPrevJob;

    @OrderBy("name")
    @JoinTable(name = "TSADV_PERSON_EXT_FILE_DESCRIPTOR_LINK",
            joinColumns = @JoinColumn(name = "PERSON_EXT_ID"),
            inverseJoinColumns = @JoinColumn(name = "FILE_DESCRIPTOR_ID"))
    @ManyToMany
    protected List<FileDescriptor> commitmentsAttachments;

    @Column(name = "COMMITMENTS_LOAN")
    protected Boolean commitmentsLoan;

    @Column(name = "COMMITMENTS_CREDIT")
    protected Boolean commitmentsCredit;

    @Column(name = "COMMITMENTS_NOT_SUR_MAT_VALUES")
    protected Boolean commitmentsNotSurMatValues;

    @Column(name = "HAVE_NDA")
    protected String haveNDA;

    @OrderBy("name")
    @JoinTable(name = "TSADV_PERSON_EXT_FILE_DESCRIPTOR_LINK",
            joinColumns = @JoinColumn(name = "PERSON_EXT_ID"),
            inverseJoinColumns = @JoinColumn(name = "FILE_DESCRIPTOR_ID"))
    @ManyToMany
    protected List<FileDescriptor> ndaAttachments;

    @Column(name = "REASON_FOR_DISMISSAL")
    protected String reasonForDismissal;

    @Column(name = "PREV_JOB_HR")
    protected String prevJobHr;

    @Column(name = "HAVE_CONVICTION")
    protected String haveConviction;

    @OrderBy("name")
    @JoinTable(name = "TSADV_PERSON_EXT_FILE_DESCRIPTOR_LINK",
            joinColumns = @JoinColumn(name = "PERSON_EXT_ID"),
            inverseJoinColumns = @JoinColumn(name = "FILE_DESCRIPTOR_ID"))
    @ManyToMany
    protected List<FileDescriptor> convictionAttachments;

    @Column(name = "REGISTERED_DISPENSARY")
    protected String registeredDispensary;

    @OrderBy("name")
    @JoinTable(name = "TSADV_PERSON_EXT_FILE_DESCRIPTOR_LINK",
            joinColumns = @JoinColumn(name = "PERSON_EXT_ID"),
            inverseJoinColumns = @JoinColumn(name = "FILE_DESCRIPTOR_ID"))
    @ManyToMany
    protected List<FileDescriptor> dispensaryAttachments;

    @Column(name = "DISPENSARY_PERIOD")
    protected String dispensaryPeriod;

    @Column(name = "DISABILITY")
    protected String disability;

    @OrderBy("name")
    @JoinTable(name = "TSADV_PERSON_EXT_FILE_DESCRIPTOR_LINK",
            joinColumns = @JoinColumn(name = "PERSON_EXT_ID"),
            inverseJoinColumns = @JoinColumn(name = "FILE_DESCRIPTOR_ID"))
    @ManyToMany
    protected List<FileDescriptor> disabilityAttachments;

    @Column(name = "DISABILITY_GROUP")
    protected String disabilityGroup;

    @Column(name = "CONTRAINDICATIONS_HEALTH")
    protected String contraindicationsHealth;

    @OrderBy("name")
    @JoinTable(name = "TSADV_PERSON_EXT_FILE_DESCRIPTOR_LINK",
            joinColumns = @JoinColumn(name = "PERSON_EXT_ID"),
            inverseJoinColumns = @JoinColumn(name = "FILE_DESCRIPTOR_ID"))
    @ManyToMany
    protected List<FileDescriptor> contraindicationsHealthAttachments;

    @Column(name = "CONTRAINDICATIONS_HEALTH_TEXT")
    protected String contraindicationsHealthText;

    @Column(name = "CHILD_UNDER18_WITHOUT_FATHER_OR_MOTHER")
    protected String childUnder18WithoutFatherOrMother;

    @OrderBy("name")
    @JoinTable(name = "TSADV_PERSON_EXT_FILE_DESCRIPTOR_LINK",
            joinColumns = @JoinColumn(name = "PERSON_EXT_ID"),
            inverseJoinColumns = @JoinColumn(name = "FILE_DESCRIPTOR_ID"))
    @ManyToMany
    protected List<FileDescriptor> childUnder18WithoutFatherOrMotherAttachments;

    @Column(name = "CHILD_UNDER14_WITHOUT_FATHER_OR_MOTHER")
    protected String childUnder14WithoutFatherOrMother;

    @OrderBy("name")
    @JoinTable(name = "TSADV_PERSON_EXT_FILE_DESCRIPTOR_LINK",
            joinColumns = @JoinColumn(name = "PERSON_EXT_ID"),
            inverseJoinColumns = @JoinColumn(name = "FILE_DESCRIPTOR_ID"))
    @ManyToMany
    protected List<FileDescriptor> childUnder14WithoutFatherOrMotherAttachments;

    @Column(name = "CRIMINAL_ADMINISTRATIVE_LIABILITY")
    protected String criminalAdministrativeLiability;

    @OrderBy("name")
    @JoinTable(name = "TSADV_PERSON_EXT_FILE_DESCRIPTOR_LINK",
            joinColumns = @JoinColumn(name = "PERSON_EXT_ID"),
            inverseJoinColumns = @JoinColumn(name = "FILE_DESCRIPTOR_ID"))
    @ManyToMany
    protected List<FileDescriptor> criminalAdministrativeLiabilityAttachments;

    @Column(name = "CRIMINAL_ADMINISTRATIVE_LIABILITY_PERIOID_REASON")
    protected String criminalAdministrativeLiabilityPerioidReason;

    @Transient
    @MetaProperty(related = {"lastName", "firstName", "middleName", "firstNameLatin", "lastNameLatin", "middleNameLatin"})
    protected String personName;

//    @Transient
//    @MetaProperty(related = {"lastName", "firstName", "middleName", "firstNameLatin", "lastNameLatin"})
//    protected String fullName;


    public String getCriminalAdministrativeLiabilityPerioidReason() {
        return criminalAdministrativeLiabilityPerioidReason;
    }

    public void setCriminalAdministrativeLiabilityPerioidReason(String criminalAdministrativeLiabilityPerioidReason) {
        this.criminalAdministrativeLiabilityPerioidReason = criminalAdministrativeLiabilityPerioidReason;
    }

    public void setCommitmentsFromPrevJob(YesNoEnum commitmentsFromPrevJob) {
        this.commitmentsFromPrevJob = commitmentsFromPrevJob == null ? null : commitmentsFromPrevJob.getId();
    }

    public YesNoEnum getCommitmentsFromPrevJob() {
        return commitmentsFromPrevJob == null ? null : YesNoEnum.fromId(commitmentsFromPrevJob);
    }

    public List<FileDescriptor> getCriminalAdministrativeLiabilityAttachments() {
        return criminalAdministrativeLiabilityAttachments;
    }

    public void setCriminalAdministrativeLiabilityAttachments(List<FileDescriptor> criminalAdministrativeLiabilityAttachments) {
        this.criminalAdministrativeLiabilityAttachments = criminalAdministrativeLiabilityAttachments;
    }

    public YesNoEnum getCriminalAdministrativeLiability() {
        return criminalAdministrativeLiability == null ? null : YesNoEnum.fromId(criminalAdministrativeLiability);
    }

    public void setCriminalAdministrativeLiability(YesNoEnum criminalAdministrativeLiability) {
        this.criminalAdministrativeLiability = criminalAdministrativeLiability == null ? null : criminalAdministrativeLiability.getId();
    }

    public List<FileDescriptor> getChildUnder14WithoutFatherOrMotherAttachments() {
        return childUnder14WithoutFatherOrMotherAttachments;
    }

    public void setChildUnder14WithoutFatherOrMotherAttachments(List<FileDescriptor> childUnder14WithoutFatherOrMotherAttachments) {
        this.childUnder14WithoutFatherOrMotherAttachments = childUnder14WithoutFatherOrMotherAttachments;
    }

    public YesNoEnum getChildUnder14WithoutFatherOrMother() {
        return childUnder14WithoutFatherOrMother == null ? null : YesNoEnum.fromId(childUnder14WithoutFatherOrMother);
    }

    public void setChildUnder14WithoutFatherOrMother(YesNoEnum childUnder14WithoutFatherOrMother) {
        this.childUnder14WithoutFatherOrMother = childUnder14WithoutFatherOrMother == null ? null : childUnder14WithoutFatherOrMother.getId();
    }

    public List<FileDescriptor> getChildUnder18WithoutFatherOrMotherAttachments() {
        return childUnder18WithoutFatherOrMotherAttachments;
    }

    public void setChildUnder18WithoutFatherOrMotherAttachments(List<FileDescriptor> childUnder18WithoutFatherOrMotherAttachments) {
        this.childUnder18WithoutFatherOrMotherAttachments = childUnder18WithoutFatherOrMotherAttachments;
    }

    public YesNoEnum getChildUnder18WithoutFatherOrMother() {
        return childUnder18WithoutFatherOrMother == null ? null : YesNoEnum.fromId(childUnder18WithoutFatherOrMother);
    }

    public void setChildUnder18WithoutFatherOrMother(YesNoEnum childUnder18WithoutFatherOrMother) {
        this.childUnder18WithoutFatherOrMother = childUnder18WithoutFatherOrMother == null ? null : childUnder18WithoutFatherOrMother.getId();
    }

    public String getContraindicationsHealthText() {
        return contraindicationsHealthText;
    }

    public void setContraindicationsHealthText(String contraindicationsHealthText) {
        this.contraindicationsHealthText = contraindicationsHealthText;
    }

    public List<FileDescriptor> getContraindicationsHealthAttachments() {
        return contraindicationsHealthAttachments;
    }

    public void setContraindicationsHealthAttachments(List<FileDescriptor> contraindicationsHealthAttachments) {
        this.contraindicationsHealthAttachments = contraindicationsHealthAttachments;
    }

    public YesNoEnum getContraindicationsHealth() {
        return contraindicationsHealth == null ? null : YesNoEnum.fromId(contraindicationsHealth);
    }

    public void setContraindicationsHealth(YesNoEnum contraindicationsHealth) {
        this.contraindicationsHealth = contraindicationsHealth == null ? null : contraindicationsHealth.getId();
    }

    public String getDisabilityGroup() {
        return disabilityGroup;
    }

    public void setDisabilityGroup(String disabilityGroup) {
        this.disabilityGroup = disabilityGroup;
    }

    public List<FileDescriptor> getDisabilityAttachments() {
        return disabilityAttachments;
    }

    public void setDisabilityAttachments(List<FileDescriptor> disabilityAttachments) {
        this.disabilityAttachments = disabilityAttachments;
    }

    public YesNoEnum getDisability() {
        return disability == null ? null : YesNoEnum.fromId(disability);
    }

    public void setDisability(YesNoEnum disability) {
        this.disability = disability == null ? null : disability.getId();
    }

    public String getDispensaryPeriod() {
        return dispensaryPeriod;
    }

    public void setDispensaryPeriod(String dispensaryPeriod) {
        this.dispensaryPeriod = dispensaryPeriod;
    }

    public List<FileDescriptor> getDispensaryAttachments() {
        return dispensaryAttachments;
    }

    public void setDispensaryAttachments(List<FileDescriptor> dispensaryAttachments) {
        this.dispensaryAttachments = dispensaryAttachments;
    }

    public YesNoEnum getRegisteredDispensary() {
        return registeredDispensary == null ? null : YesNoEnum.fromId(registeredDispensary);
    }

    public void setRegisteredDispensary(YesNoEnum registeredDispensary) {
        this.registeredDispensary = registeredDispensary == null ? null : registeredDispensary.getId();
    }

    public List<FileDescriptor> getConvictionAttachments() {
        return convictionAttachments;
    }

    public void setConvictionAttachments(List<FileDescriptor> convictionAttachments) {
        this.convictionAttachments = convictionAttachments;
    }

    public YesNoEnum getHaveConviction() {
        return haveConviction == null ? null : YesNoEnum.fromId(haveConviction);
    }

    public void setHaveConviction(YesNoEnum haveConviction) {
        this.haveConviction = haveConviction == null ? null : haveConviction.getId();
    }

    public String getPrevJobHr() {
        return prevJobHr;
    }

    public void setPrevJobHr(String prevJobHr) {
        this.prevJobHr = prevJobHr;
    }

    public String getReasonForDismissal() {
        return reasonForDismissal;
    }

    public void setReasonForDismissal(String reasonForDismissal) {
        this.reasonForDismissal = reasonForDismissal;
    }

    public List<FileDescriptor> getNdaAttachments() {
        return ndaAttachments;
    }

    public void setNdaAttachments(List<FileDescriptor> ndaAttachments) {
        this.ndaAttachments = ndaAttachments;
    }

    public YesNoEnum getHaveNDA() {
        return haveNDA == null ? null : YesNoEnum.fromId(haveNDA);
    }

    public void setHaveNDA(YesNoEnum haveNDA) {
        this.haveNDA = haveNDA == null ? null : haveNDA.getId();
    }

    public Boolean getCommitmentsNotSurMatValues() {
        return commitmentsNotSurMatValues;
    }

    public void setCommitmentsNotSurMatValues(Boolean commitmentsNotSurMatValues) {
        this.commitmentsNotSurMatValues = commitmentsNotSurMatValues;
    }

    public Boolean getCommitmentsCredit() {
        return commitmentsCredit;
    }

    public void setCommitmentsCredit(Boolean commitmentsCredit) {
        this.commitmentsCredit = commitmentsCredit;
    }

    public Boolean getCommitmentsLoan() {
        return commitmentsLoan;
    }

    public void setCommitmentsLoan(Boolean commitmentsLoan) {
        this.commitmentsLoan = commitmentsLoan;
    }

    public List<FileDescriptor> getCommitmentsAttachments() {
        return commitmentsAttachments;
    }

    public void setCommitmentsAttachments(List<FileDescriptor> commitmentsAttachments) {
        this.commitmentsAttachments = commitmentsAttachments;
    }

    public void setPrevJobObligation(YesNoEnum prevJobObligation) {
        this.prevJobObligation = prevJobObligation == null ? null : prevJobObligation.getId();
    }

    public YesNoEnum getPrevJobObligation() {
        return prevJobObligation == null ? null : YesNoEnum.fromId(prevJobObligation);
    }

    public List<FileDescriptor> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<FileDescriptor> attachments) {
        this.attachments = attachments;
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

    public String getPersonName() {
        UserSessionSource userSessionSource = AppBeans.get("cuba_UserSessionSource");
        String language = userSessionSource.getLocale().getLanguage();
        String langOrder = AppContext.getProperty("base.abstractDictionary.langOrder");
        String[] initials;
        if (langOrder != null) {
            List<String> langs = Arrays.asList(langOrder.split(";"));
            if (langs.indexOf(language) == 2) {
                initials = new String[]{lastNameLatin, firstNameLatin, middleNameLatin};
            } else {
                initials = new String[]{lastName, firstName, middleName};
            }
        } else {
            initials = new String[]{lastName, firstName, middleName};
        }

        return Arrays.stream(initials).filter(Objects::nonNull).collect(Collectors.joining(" "));
    }

//    public String getFullName() {
//        StringBuilder builder = new StringBuilder("");
//        UserSessionSource userSessionSource = AppBeans.get("cuba_UserSessionSource");
//        String langOrder = com.haulmont.cuba.core.sys.AppContext.getProperty("base.abstractDictionary.langOrder");
//        String language = userSessionSource.getLocale().getLanguage();
//
//        builder.append(lastName).append(" ");
//        builder.append(firstName).append(" ");
//        if (middleName != null) builder.append(middleName).append(" ");
//
//        if (langOrder != null) {
//            List<String> langs = Arrays.asList(langOrder.split(";")); //ru, kz, en
//            if (langs.indexOf(language) == 2 && StringUtils.isNotBlank(firstNameLatin) && StringUtils.isNotBlank(lastNameLatin)) {
//                builder = new StringBuilder("");
//                builder.append(firstNameLatin).append(" ");
//                builder.append(lastNameLatin).append(" ");
//            }
//        }
//        return builder.toString();
//    }

    @Override
    public String getShortName() {
        StringBuilder builder = new StringBuilder("");
        builder.append(lastName).append(" ");
        builder.append(firstName != null && firstName.length() > 0 ? firstName.substring(0, 1) : "").append(". ");
        if (middleName != null && middleName.length() > 0) builder.append(middleName.substring(0, 1)).append(". ");
        return builder.toString();
    }
}