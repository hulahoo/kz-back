package kz.uco.tsadv.modules.learning.model;

import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.entity.annotation.PublishEntityChangedEvents;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.tsadv.modules.learning.model.feedback.CourseFeedbackPersonAnswer;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;

@PublishEntityChangedEvents
@Table(name = "TSADV_COURSE_REVIEW")
@Entity(name = "tsadv$CourseReview")
public class CourseReview extends StandardEntity {
    private static final long serialVersionUID = 3445274219830523927L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COURSE_ID")
    protected kz.uco.tsadv.modules.learning.model.Course course;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @Column(name = "RATE")
    protected Double rate;

    @Column(name = "TEXT", nullable = false, length = 2000)
    protected String text;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FROM_COURSE_FEEDBACK_PERSON_ANSWER_ID")
    protected CourseFeedbackPersonAnswer fromCourseFeedbackPersonAnswer;

    public CourseFeedbackPersonAnswer getFromCourseFeedbackPersonAnswer() {
        return fromCourseFeedbackPersonAnswer;
    }

    public void setFromCourseFeedbackPersonAnswer(CourseFeedbackPersonAnswer fromCourseFeedbackPersonAnswer) {
        this.fromCourseFeedbackPersonAnswer = fromCourseFeedbackPersonAnswer;
    }

    public void setCourse(kz.uco.tsadv.modules.learning.model.Course course) {
        this.course = course;
    }

    public Course getCourse() {
        return course;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Double getRate() {
        return rate;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }


}