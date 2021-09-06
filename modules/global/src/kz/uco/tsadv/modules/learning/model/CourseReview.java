package kz.uco.tsadv.modules.learning.model;

import com.haulmont.chile.core.annotations.MetaProperty;
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

    @MetaProperty(related = "rate")
    public Double getRoundedRate() {
        if (rate != null) {
            int intRate = rate.intValue();
            double dif = rate - intRate;
            if (dif < 0.25) return intRate + 0.0;
            else if (0.25 <= dif && dif < 0.75) return intRate + 0.5;
            else return intRate + 1.0;
        }
        return null;
    }

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