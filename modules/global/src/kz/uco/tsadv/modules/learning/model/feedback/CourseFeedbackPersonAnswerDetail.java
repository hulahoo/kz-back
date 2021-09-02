package kz.uco.tsadv.modules.learning.model.feedback;

import com.haulmont.cuba.core.entity.annotation.PublishEntityChangedEvents;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.learning.model.Course;
import kz.uco.tsadv.modules.learning.model.CourseSectionSession;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@PublishEntityChangedEvents
@Table(name = "TSADV_COURSE_FEEDBACK_PERSON_ANSWER_DETAIL")
@Entity(name = "tsadv$CourseFeedbackPersonAnswerDetail")
public class CourseFeedbackPersonAnswerDetail extends AbstractParentEntity {
    private static final long serialVersionUID = -6014422480411111415L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FEEDBACK_TEMPLATE_ID")
    protected LearningFeedbackTemplate feedbackTemplate;

    @Column(name = "QUESTION_ORDER")
    protected Integer questionOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COURSE_ID")
    protected Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COURSE_SECTION_SESSION_ID")
    protected CourseSectionSession courseSectionSession;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "QUESTION_ID")
    protected LearningFeedbackQuestion question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ANSWER_ID")
    protected LearningFeedbackAnswer answer;

    @Column(name = "TEXT_ANSWER", length = 2000)
    protected String textAnswer;

    @NotNull
    @Column(name = "SCORE", nullable = false)
    protected Integer score;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COURSE_FEEDBACK_PERSON_ANSWER_ID")
    protected CourseFeedbackPersonAnswer courseFeedbackPersonAnswer;


    public void setQuestionOrder(Integer questionOrder) {
        this.questionOrder = questionOrder;
    }

    public Integer getQuestionOrder() {
        return questionOrder;
    }


    public void setCourseFeedbackPersonAnswer(CourseFeedbackPersonAnswer courseFeedbackPersonAnswer) {
        this.courseFeedbackPersonAnswer = courseFeedbackPersonAnswer;
    }

    public CourseFeedbackPersonAnswer getCourseFeedbackPersonAnswer() {
        return courseFeedbackPersonAnswer;
    }


    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
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

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setQuestion(LearningFeedbackQuestion question) {
        this.question = question;
    }

    public LearningFeedbackQuestion getQuestion() {
        return question;
    }

    public void setAnswer(LearningFeedbackAnswer answer) {
        this.answer = answer;
    }

    public LearningFeedbackAnswer getAnswer() {
        return answer;
    }

    public void setTextAnswer(String textAnswer) {
        this.textAnswer = textAnswer;
    }

    public String getTextAnswer() {
        return textAnswer;
    }


}