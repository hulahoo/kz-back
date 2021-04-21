package kz.uco.tsadv.modules.learning.model.feedback;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.entity.annotation.PublishEntityChangedEvents;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.learning.enums.feedback.FeedbackResponsibleRole;
import kz.uco.tsadv.modules.learning.model.Course;
import kz.uco.tsadv.modules.learning.model.CourseSectionSession;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@PublishEntityChangedEvents
@Table(name = "TSADV_COURSE_FEEDBACK_PERSON_ANSWER")
@Entity(name = "tsadv$CourseFeedbackPersonAnswer")
public class CourseFeedbackPersonAnswer extends AbstractParentEntity {
    private static final long serialVersionUID = 2337765196055600001L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FEEDBACK_TEMPLATE_ID")
    protected LearningFeedbackTemplate feedbackTemplate;

    @OrderBy("questionOrder")
    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "courseFeedbackPersonAnswer")
    protected List<CourseFeedbackPersonAnswerDetail> details;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COURSE_ID")
    protected Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COURSE_SECTION_SESSION_ID")
    protected CourseSectionSession courseSectionSession;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "COMPLETE_DATE", nullable = false)
    protected Date completeDate;

    @NotNull
    @Column(name = "RESPONSIBLE_ROLE", nullable = false)
    protected String responsibleRole;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @Column(name = "SUM_SCORE")
    protected Long sumScore;

    @Column(name = "AVG_SCORE")
    protected Double avgScore;

    public FeedbackResponsibleRole getResponsibleRole() {
        return responsibleRole == null ? null : FeedbackResponsibleRole.fromId(responsibleRole);
    }

    public void setResponsibleRole(FeedbackResponsibleRole responsibleRole) {
        this.responsibleRole = responsibleRole == null ? null : responsibleRole.getId();
    }


    public void setDetails(List<CourseFeedbackPersonAnswerDetail> details) {
        this.details = details;
    }

    public List<CourseFeedbackPersonAnswerDetail> getDetails() {
        return details;
    }


    public void setFeedbackTemplate(LearningFeedbackTemplate feedbackTemplate) {
        this.feedbackTemplate = feedbackTemplate;
    }

    public LearningFeedbackTemplate getFeedbackTemplate() {
        return feedbackTemplate;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourseSectionSession(CourseSectionSession courseSectionSession) {
        this.courseSectionSession = courseSectionSession;
    }

    public CourseSectionSession getCourseSectionSession() {
        return courseSectionSession;
    }

    public void setCompleteDate(Date completeDate) {
        this.completeDate = completeDate;
    }

    public Date getCompleteDate() {
        return completeDate;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setSumScore(Long sumScore) {
        this.sumScore = sumScore;
    }

    public Long getSumScore() {
        return sumScore;
    }

    public void setAvgScore(Double avgScore) {
        this.avgScore = avgScore;
    }

    public Double getAvgScore() {
        return avgScore;
    }


}