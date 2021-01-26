package kz.uco.tsadv.modules.learning.model;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.learning.dictionary.DicCategory;
import kz.uco.tsadv.modules.learning.dictionary.DicLearningType;
import kz.uco.tsadv.modules.learning.model.feedback.CourseFeedbackTemplate;
import kz.uco.tsadv.modules.performance.model.CourseTrainer;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@NamePattern("%s|name")
@Table(name = "TSADV_COURSE")
@Entity(name = "tsadv$Course")
public class Course extends AbstractParentEntity {
    private static final long serialVersionUID = 9028025620872520019L;

    @Column(name = "NAME", nullable = false)
    protected String name;

    @NotNull
    @Column(name = "IS_ISSUED_CERTIFICATE", nullable = false)
    protected Boolean isIssuedCertificate = false;

    @OneToMany(mappedBy = "course")
    protected List<Enrollment> enrollments;

    @OneToMany(mappedBy = "course")
    protected List<CourseTrainer> courseTrainers;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "course")
    protected List<CourseFeedbackTemplate> feedbackTemplates;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARTY_ID")
    protected PartyExt party;

    @Lob
    @Column(name = "DESCRIPTION")
    protected String description;

    @Column(name = "LOGO")
    protected byte[] logo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CATEGORY_ID")
    protected DicCategory category;

    @Column(name = "TARGET_AUDIENCE", length = 1000)
    protected String targetAudience;

    @Column(name = "ACTIVE_FLAG")
    protected Boolean activeFlag;

    @OrderBy("order ASC")
    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "course")
    protected List<CourseSection> sections;

    @Column(name = "SHORT_DESCRIPTION", length = 2000)
    protected String shortDescription;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "course")
    protected List<CourseCompetence> competences;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "course")
    protected List<CoursePreRequisition> preRequisition;

    @Transient
    @MetaProperty
    protected Double avgRate;


    @Column(name = "SELF_ENROLLMENT")
    protected Boolean selfEnrollment = false;

    @Transient
    @MetaProperty
    protected Boolean completed;

    @Lookup(type = LookupType.DROPDOWN, actions = {})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LEARNING_TYPE_ID")
    protected DicLearningType learningType;

    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "course")
    protected List<CourseReview> reviews;

    public List<CourseReview> getReviews() {
        return reviews;
    }

    public void setReviews(List<CourseReview> reviews) {
        this.reviews = reviews;
    }

    public Boolean getIsIssuedCertificate() {
        return isIssuedCertificate;
    }

    public void setIsIssuedCertificate(Boolean isIssuedCertificate) {
        this.isIssuedCertificate = isIssuedCertificate;
    }

    public List<Enrollment> getEnrollments() {
        return enrollments;
    }

    public void setEnrollments(List<Enrollment> enrollments) {
        this.enrollments = enrollments;
    }

    public List<CourseTrainer> getCourseTrainers() {
        return courseTrainers;
    }

    public void setCourseTrainers(List<CourseTrainer> courseTrainers) {
        this.courseTrainers = courseTrainers;
    }

    public void setFeedbackTemplates(List<CourseFeedbackTemplate> feedbackTemplates) {
        this.feedbackTemplates = feedbackTemplates;
    }

    public List<CourseFeedbackTemplate> getFeedbackTemplates() {
        return feedbackTemplates;
    }


    public List<CourseSection> getSections() {
        return sections;
    }

    public void setSections(List<CourseSection> sections) {
        this.sections = sections;
    }



    public PartyExt getParty() {
        return party;
    }

    public void setParty(PartyExt party) {
        this.party = party;
    }

    public void setLearningType(DicLearningType learningType) {
        this.learningType = learningType;
    }

    public DicLearningType getLearningType() {
        return learningType;
    }


    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Boolean getCompleted() {
        return completed;
    }


    public void setSelfEnrollment(Boolean selfEnrollment) {
        this.selfEnrollment = selfEnrollment;
    }

    public Boolean getSelfEnrollment() {
        return selfEnrollment;
    }


    public void setAvgRate(Double avgRate) {
        this.avgRate = avgRate;
    }

    public Double getAvgRate() {
        return avgRate;
    }


    public void setPreRequisition(List<CoursePreRequisition> preRequisition) {
        this.preRequisition = preRequisition;
    }

    public List<CoursePreRequisition> getPreRequisition() {
        return preRequisition;
    }


    public void setCompetences(List<CourseCompetence> competences) {
        this.competences = competences;
    }

    public List<CourseCompetence> getCompetences() {
        return competences;
    }


    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getShortDescription() {
        return shortDescription;
    }





    public DicCategory getCategory() {
        return category;
    }

    public void setCategory(DicCategory category) {
        this.category = category;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setLogo(byte[] logo) {
        this.logo = logo;
    }

    public byte[] getLogo() {
        return logo;
    }

    public void setTargetAudience(String targetAudience) {
        this.targetAudience = targetAudience;
    }

    public String getTargetAudience() {
        return targetAudience;
    }

    public void setActiveFlag(Boolean activeFlag) {
        this.activeFlag = activeFlag;
    }

    public Boolean getActiveFlag() {
        return activeFlag;
    }


}