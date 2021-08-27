package kz.uco.tsadv.modules.personal.group;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.annotation.Extends;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.core.global.PersistenceHelper;
import kz.uco.base.common.BaseCommonUtils;
import kz.uco.base.entity.abstraction.IEntityGroup;
import kz.uco.base.entity.dictionary.DicCompany;
import kz.uco.base.entity.shared.PersonGroup;
import kz.uco.tsadv.modules.learning.model.Attestation;
import kz.uco.tsadv.modules.learning.model.Enrollment;
import kz.uco.tsadv.modules.learning.model.IndividualDevelopmentPlan;
import kz.uco.tsadv.modules.learning.model.Internship;
import kz.uco.tsadv.modules.performance.model.Assessment;
import kz.uco.tsadv.modules.personal.dictionary.DicDriverCategory;
import kz.uco.tsadv.modules.personal.dictionary.DicSocStatus;
import kz.uco.tsadv.modules.personal.model.*;
import kz.uco.tsadv.modules.recruitment.model.*;
import org.eclipse.persistence.annotations.Customizer;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Listeners("tsadv_PersonGroupListener")
@NamePattern("%s|person,list")
@Extends(PersonGroup.class)
@Entity(name = "base$PersonGroupExt")
@Customizer(PersonGroupExtDescriptorCustomizer.class)
public class PersonGroupExt extends PersonGroup implements IEntityGroup<PersonExt> {
    private static final long serialVersionUID = -6026637722093200432L;

    @OnDelete(DeletePolicy.CASCADE)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IMAGE_ID")
    private FileDescriptor image;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "group")
    protected List<PersonExt> list;

    @OneToMany(mappedBy = "personGroupExt")
    protected List<Retirement> retirement;

    @OneToMany(mappedBy = "personGroupExt")
    protected List<Disability> disability;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "personGroup")
    protected List<MilitaryForm> militaryRank;

    @Transient
    @MetaProperty(related = "list")
    protected PersonExt person;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "group")
    protected PersonExt relevantPerson; // Текущее (для момента в машине времени) лицо

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "personGroup")
    protected List<CompetenceElement> competenceElements;

    @Composition
    @OneToMany(mappedBy = "personGroup")
    protected List<AssignmentExt> assignments;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "personGroup")
    protected List<PersonContact> personContacts;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "personGroup")
    protected List<PersonDocument> personDocuments;

    @OrderBy("startDate desc")
    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "employeePersonGroup")
    protected List<Assessment> assessments;

    @OrderBy("startYear")
    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "personGroup")
    protected List<PersonEducation> personEducation;

    @OrderBy("startMonth")
    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "personGroup")
    protected List<PersonExperience> personExperience;

    @OrderBy("filename")
    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "personGroup")
    protected List<PersonAttachment> personAttachment;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "candidatePersonGroup")
    protected List<JobRequest> jobRequests;

    @OneToMany(mappedBy = "personGroup")
    protected List<SuccessionPlanning> successionPlanning;

    @OneToMany(mappedBy = "personGroup")
    protected List<BusinessTrip> businessTrip;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "personGroup")
    protected List<Agreement> agreements;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "personGroup")
    protected List<Dismissal> dismissals;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "personGroup")
    protected List<Case> cases;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "person")
    protected List<PersonReview> reviews;

    @Transient
    @MetaProperty
    protected Long likeCount;

    @Transient
    @MetaProperty
    protected Long disLikeCount;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "personGroup")
    protected List<Address> addresses;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "personGroup")
    protected List<StudentGrant> studentGrants;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "personGroup")
    protected List<ReLocation> relocation;

    @OrderBy("actualDate")
    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "personGroup")
    protected List<PersonExpectedSalary> expectedSalary;

    @Column(name = "LINKEDIN_ACCESS_TOKEN", length = 1000)
    protected String linkedinAccessToken;

    @Column(name = "LINKEDIN_PROFILE_LINK", length = 1000)
    protected String linkedinProfileLink;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LINKEDIN_TOKEN_EXPIRES_DATE")
    protected Date linkedinTokenExpiresInDate;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "personGroupChild")
    protected List<Beneficiary> beneficiary;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "personGroup")
    protected List<IndividualDevelopmentPlan> individualDevelopmentPlan;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "personGroup")
    protected List<Internship> internship;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "personGroup")
    protected List<Enrollment> enrollment;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "personGroupExt")
    protected List<Attestation> attestation;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "personGroup")
    protected List<CandidateRequirement> candidateRequirement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID")
    protected DicCompany company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SOC_STATUS_ID")
    protected DicSocStatus socStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DRIVER_CATEGORY_ID")
    protected DicDriverCategory driverCategory;

    @Column(name = "REQUEST_NUMBER")
    protected String requestNumber;

    public String getRequestNumber() {
        return requestNumber;
    }

    public void setRequestNumber(String requestNumber) {
        this.requestNumber = requestNumber;
    }

    public DicDriverCategory getDriverCategory() {
        return driverCategory;
    }

    public void setDriverCategory(DicDriverCategory driverCategory) {
        this.driverCategory = driverCategory;
    }

    public DicSocStatus getSocStatus() {
        return socStatus;
    }

    public void setSocStatus(DicSocStatus socStatus) {
        this.socStatus = socStatus;
    }

    public FileDescriptor getImage() {
        return image;
    }

    public void setImage(FileDescriptor image) {
        this.image = image;
    }

    public DicCompany getCompany() {
        return company;
    }

    public void setCompany(DicCompany company) {
        this.company = company;
    }

    public void setRelevantPerson(PersonExt relevantPerson) {
        this.relevantPerson = relevantPerson;
    }

    public PersonExt getRelevantPerson() {
        return relevantPerson;
    }

    public void setCandidateRequirement(List<CandidateRequirement> candidateRequirement) {
        this.candidateRequirement = candidateRequirement;
    }

    public List<CandidateRequirement> getCandidateRequirement() {
        return candidateRequirement;
    }


    public void setIndividualDevelopmentPlan(List<IndividualDevelopmentPlan> individualDevelopmentPlan) {
        this.individualDevelopmentPlan = individualDevelopmentPlan;
    }

    public List<IndividualDevelopmentPlan> getIndividualDevelopmentPlan() {
        return individualDevelopmentPlan;
    }

    public void setInternship(List<Internship> internship) {
        this.internship = internship;
    }

    public List<Internship> getInternship() {
        return internship;
    }

    public void setEnrollment(List<Enrollment> enrollment) {
        this.enrollment = enrollment;
    }

    public List<Enrollment> getEnrollment() {
        return enrollment;
    }

    public void setAttestation(List<Attestation> attestation) {
        this.attestation = attestation;
    }

    public List<Attestation> getAttestation() {
        return attestation;
    }

    public void setBeneficiary(List<Beneficiary> beneficiary) {
        this.beneficiary = beneficiary;
    }

    public List<Beneficiary> getBeneficiary() {
        return beneficiary;
    }

    public void setRetirement(List<Retirement> retirement) {
        this.retirement = retirement;
    }

    public List<Retirement> getRetirement() {
        return retirement;
    }

    public void setDisability(List<Disability> disability) {
        this.disability = disability;
    }

    public List<Disability> getDisability() {
        return disability;
    }

    public List<AssignmentExt> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<AssignmentExt> assignments) {
        this.assignments = assignments;
    }

    public void setMilitaryRank(List<MilitaryForm> militaryRank) {
        this.militaryRank = militaryRank;
    }

    public List<MilitaryForm> getMilitaryRank() {
        return militaryRank;
    }

    @MetaProperty(related = "assignments,personExperience")
    @Transient
    public Long getTotalExperience() {
        Long dateDiff = 0L; //TODO Сделать поле startDate в сущности Assignment обязательным или проверить на null
        Date curDate = new Date();
        if (assignments == null || !PersistenceHelper.isLoaded(this, "assignments")) return dateDiff;
        for (AssignmentExt assignment : getAssignments()) {
            if (assignment.getEndDate() == null || assignment.getEndDate().after(curDate)) {
                dateDiff += curDate.getTime() - assignment.getStartDate().getTime();
            } else {
                dateDiff += assignment.getEndDate().getTime() - assignment.getStartDate().getTime();
            }
        }
        if (getPersonExperience() != null) {
            for (PersonExperience personExperience : getPersonExperience()) {
                if (personExperience.getEndMonth() == null || personExperience.getEndMonth().after(curDate)) {
                    dateDiff += curDate.getTime() - personExperience.getStartMonth().getTime();
                } else {
                    dateDiff += personExperience.getEndMonth().getTime() - personExperience.getStartMonth().getTime();
                }
            }
        }
        Long years = Math.round(dateDiff / (1000.0 * 60 * 60 * 24 * 365));
        return years;
    }

    public void setExpectedSalary(List<PersonExpectedSalary> expectedSalary) {
        this.expectedSalary = expectedSalary;
    }

    public List<PersonExpectedSalary> getExpectedSalary() {
        return expectedSalary;
    }

    public void setRelocation(List<ReLocation> relocation) {
        this.relocation = relocation;
    }

    public List<ReLocation> getRelocation() {
        return relocation;
    }

    public void setStudentGrants(List<StudentGrant> studentGrants) {
        this.studentGrants = studentGrants;
    }

    public List<StudentGrant> getStudentGrants() {
        return studentGrants;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }

    public Long getLikeCount() {
        return likeCount;
    }

    public void setDisLikeCount(Long disLikeCount) {
        this.disLikeCount = disLikeCount;
    }

    public Long getDisLikeCount() {
        return disLikeCount;
    }

    public void setReviews(List<PersonReview> reviews) {
        this.reviews = reviews;
    }

    public List<PersonReview> getReviews() {
        return reviews;
    }

    public void setAgreements(List<Agreement> agreements) {
        this.agreements = agreements;
    }

    public List<Agreement> getAgreements() {
        return agreements;
    }

    public void setCases(List<Case> cases) {
        this.cases = cases;
    }

    public List<Case> getCases() {
        return cases;
    }

    public void setDismissals(List<Dismissal> dismissals) {
        this.dismissals = dismissals;
    }

    public List<Dismissal> getDismissals() {
        return dismissals;
    }

    public void setBusinessTrip(List<BusinessTrip> businessTrip) {
        this.businessTrip = businessTrip;
    }

    public List<BusinessTrip> getBusinessTrip() {
        return businessTrip;
    }

    public void setSuccessionPlanning(List<SuccessionPlanning> successionPlanning) {
        this.successionPlanning = successionPlanning;
    }

    public List<SuccessionPlanning> getSuccessionPlanning() {
        return successionPlanning;
    }

    public void setJobRequests(List<JobRequest> jobRequests) {
        this.jobRequests = jobRequests;
    }

    public List<JobRequest> getJobRequests() {
        return jobRequests;
    }

    public void setPersonEducation(List<PersonEducation> personEducation) {
        this.personEducation = personEducation;
    }

    public List<PersonEducation> getPersonEducation() {
        return personEducation;
    }

    public void setPersonExperience(List<PersonExperience> personExperience) {
        this.personExperience = personExperience;
    }

    public List<PersonExperience> getPersonExperience() {
        return personExperience;
    }

    public void setPersonAttachment(List<PersonAttachment> personAttachment) {
        this.personAttachment = personAttachment;
    }

    public List<PersonAttachment> getPersonAttachment() {
        return personAttachment;
    }

    public void setAssessments(List<Assessment> assessments) {
        this.assessments = assessments;
    }

    public List<Assessment> getAssessments() {
        return assessments;
    }

    public void setPersonDocuments(List<PersonDocument> personDocuments) {
        this.personDocuments = personDocuments;
    }

    public List<PersonDocument> getPersonDocuments() {
        return personDocuments;
    }

    public void setPersonContacts(List<PersonContact> personContacts) {
        this.personContacts = personContacts;
    }

    public List<PersonContact> getPersonContacts() {
        return personContacts;
    }

    public void setCompetenceElements(List<CompetenceElement> competenceElements) {
        this.competenceElements = competenceElements;
    }

    public List<CompetenceElement> getCompetenceElements() {
        return competenceElements;
    }

    public void setList(List<PersonExt> list) {
        this.list = list;
    }

    public List<PersonExt> getList() {
        return list;
    }

    public void setPerson(PersonExt person) {
        this.person = person;
    }

    public PersonExt getPerson() {
        return person != null ? person : getPerson(BaseCommonUtils.getSystemDate());
    }

    public PersonExt getPerson(Date date) {
        if (PersistenceHelper.isLoaded(this, "list") && list != null && !list.isEmpty() && person == null) {
            list.forEach(personExt -> {
                if (personExt.getDeleteTs() == null
                        && (person == null
                        || !personExt.getStartDate().after(date)
                        && !personExt.getEndDate().before(date))) {
                    person = personExt;
                }
            });
        }
        return person;
    }

    @MetaProperty(related = "list")
    @Transient
    public String getPersonFioWithEmployeeNumber() {
        return getPerson() != null ? getPerson().getFioWithEmployeeNumber() : null;
    }

    public String getPersonFioWithEmployeeNumber(Date date) {
        return getPerson(date) != null ? getPerson(date).getFioWithEmployeeNumber() : null;
    }

    @MetaProperty(related = "assessments")
    @Transient
    public Assessment getActiveAssessment() {
        if (getAssessments() != null) {
            if (PersistenceHelper.isLoaded(this, "assessments") && !getAssessments().isEmpty()) {
                return getAssessments().get(0);
            } else
                return null;
        } else
            return null;
    }

    @MetaProperty(related = "assignments")
    @Transient
    public AssignmentExt getCurrentAssignment() {
        if (PersistenceHelper.isLoaded(this, "assignments") && getAssignments() != null && !getAssignments().isEmpty()) {
            return getAssignments().stream()
                    .filter(a -> a.getDeleteTs() == null
                            && !BaseCommonUtils.getSystemDate().before(a.getStartDate())
                            && !BaseCommonUtils.getSystemDate().after(a.getEndDate())
                            && a.getPrimaryFlag()
                            && a.getAssignmentStatus().getCode().equals("ACTIVE")
                    )
                    .findFirst()
                    .orElse(null);
        } else return null;
    }

    @MetaProperty(related = "assignments")
    @Transient
    public AssignmentExt getCurrentAssignmentWithSuspendedAndTerminatedStatus() {
        if (PersistenceHelper.isLoaded(this, "assignments") && getAssignments() != null && !getAssignments().isEmpty()) {
            return getAssignments().stream()
                    .filter(a -> a.getDeleteTs() == null
                            && !BaseCommonUtils.getSystemDate().before(a.getStartDate())
                            && !BaseCommonUtils.getSystemDate().after(a.getEndDate())
                            && a.getPrimaryFlag()
                            && (a.getAssignmentStatus().getCode().equals("ACTIVE")
                            || a.getAssignmentStatus().getCode().equals("SUSPENDED")
                            || a.getAssignmentStatus().getCode().equals("TERMINATED"))
                    )
                    .findFirst()
                    .orElse(null);
        } else return null;
    }

    @MetaProperty(related = "assignments")
    @Transient
    public AssignmentExt getCurrentAssignmentWithSuspendedStatus() {
        if (PersistenceHelper.isLoaded(this, "assignments") && getAssignments() != null && !getAssignments().isEmpty()) {
            return getAssignments().stream()
                    .filter(a -> a.getDeleteTs() == null
                            && !BaseCommonUtils.getSystemDate().before(a.getStartDate())
                            && !BaseCommonUtils.getSystemDate().after(a.getEndDate())
                            && a.getPrimaryFlag()
                            && (a.getAssignmentStatus().getCode().equals("ACTIVE")
                            || a.getAssignmentStatus().getCode().equals("SUSPENDED"))
                    )
                    .findFirst()
                    .orElse(null);
        } else return null;
    }

    @MetaProperty(related = "assignments")
    @Transient
    public AssignmentExt getPrimaryAssignment() {
        if (PersistenceHelper.isLoaded(this, "assignments") && getAssignments() != null && !getAssignments().isEmpty()) {
            return getAssignments().stream()
                    .filter(a -> a.getDeleteTs() == null
                            && !BaseCommonUtils.getSystemDate().before(a.getStartDate())
                            && !BaseCommonUtils.getSystemDate().after(a.getEndDate())
                            && a.getPrimaryFlag())
                    .findFirst()
                    .orElse(null);
        } else return null;
    }

    public String getLinkedinAccessToken() {
        return linkedinAccessToken;
    }

    public void setLinkedinAccessToken(String linkedinAccessToken) {
        this.linkedinAccessToken = linkedinAccessToken;
    }

    public String getLinkedinProfileLink() {
        return linkedinProfileLink;
    }

    public void setLinkedinProfileLink(String linkedinProfileLink) {
        this.linkedinProfileLink = linkedinProfileLink;
    }

    public Date getLinkedinTokenExpiresInDate() {
        return linkedinTokenExpiresInDate;
    }

    public void setLinkedinTokenExpiresInDate(Date linkedinTokenExpiresInDate) {
        this.linkedinTokenExpiresInDate = linkedinTokenExpiresInDate;
    }

    @MetaProperty
    @Transient
    public String getFullName() {
        PersonExt person = getPerson();
        if (person != null) {
            return person.getFullName();
        }
        return "";
    }

    @MetaProperty
    @Transient
    public String getFirstLastName() {
        PersonExt person = getPerson();
        if (person != null) {
            return person.getFirstLastName();
        }
        return "";
    }

    @MetaProperty
    @Transient
    public String getFioWithEmployeeNumber() {
        PersonExt person = getPerson();
        if (person != null) {
            return person.getFioWithEmployeeNumber();
        }
        return "";
    }

    @MetaProperty(related = "list")
    @Transient
    public String getPersonLatinFioWithEmployeeNumber() {
        return getPerson() != null ? getPerson().getFullNameLatin() : null;
    }

    public String getPersonLatinFioWithEmployeeNumber(String lang) {
        return getPerson() != null ? getPerson().getFullNameLatin(lang) : null;
    }

    @MetaProperty(related = "list")
    @Transient
    public String getPersonFirstLastNameLatin() {
        return getPerson() != null ? getPerson().getFistLastNameLatin() : null;
    }

    public String getFullName(Locale locale) {
        return getPerson() != null ? getPerson().getFullNameLatin(locale.getLanguage()) : null;
    }
}