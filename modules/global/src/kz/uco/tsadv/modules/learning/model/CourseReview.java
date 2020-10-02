package kz.uco.tsadv.modules.learning.model;

import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;

@Table(name = "TSADV_COURSE_REVIEW")
@Entity(name = "tsadv$CourseReview")
public class CourseReview extends StandardEntity {
    private static final long serialVersionUID = 3445274219830523927L;

    @OnDelete(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COURSE_ID")
    protected kz.uco.tsadv.modules.learning.model.Course course;

    @OnDelete(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @Column(name = "RATE")
    protected Double rate;

    @Column(name = "TEXT", nullable = false, length = 500)
    protected String text;

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