package kz.uco.tsadv.modules.performance.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import kz.uco.tsadv.modules.learning.model.Course;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

@Table(name = "TSADV_COURSE_TRAINER_ASSESSMENT")
@Entity(name = "tsadv$CourseTrainerAssessment")
public class CourseTrainerAssessment extends AbstractParentEntity {
    private static final long serialVersionUID = 8594827972835335818L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TRAINER_ID")
    protected Trainer trainer;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COURSE_ID")
    protected Course course;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "ASSESSMENT_DATE", nullable = false)
    protected Date assessmentDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ASSESSOR_ID")
    protected PersonGroupExt assessor;

    @NotNull
    @Column(name = "SCORE", nullable = false)
    protected Double score;

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    public Trainer getTrainer() {
        return trainer;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Course getCourse() {
        return course;
    }

    public void setAssessmentDate(Date assessmentDate) {
        this.assessmentDate = assessmentDate;
    }

    public Date getAssessmentDate() {
        return assessmentDate;
    }

    public void setAssessor(PersonGroupExt assessor) {
        this.assessor = assessor;
    }

    public PersonGroupExt getAssessor() {
        return assessor;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Double getScore() {
        return score;
    }


}