package kz.uco.tsadv.modules.learning.model;

import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.learning.dictionary.DicLearningCenter;
import kz.uco.tsadv.modules.performance.model.Trainer;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * Группа обучения
 */
@Table(name = "TSADV_COURSE_SECTION_SESSION")
@Entity(name = "tsadv$CourseSectionSession")
public class CourseSectionSession extends AbstractParentEntity {
    private static final long serialVersionUID = 7462149964522727090L;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "START_DATE", nullable = false)
    protected Date startDate;

    @OneToMany(mappedBy = "courseSession")
    protected List<CourseSessionEnrollment> courseSessionEnrollmentList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRAINER_ID")
    protected Trainer trainer;

    @Column(name = "NAME")
    protected String name;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "END_DATE", nullable = false)
    protected Date endDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "LEARNING_CENTER_ID")
    protected DicLearningCenter learningCenter;

    @Column(name = "MAX_PERSON", nullable = false)
    protected Integer maxPerson;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COURSE_SECTION_ID")
    protected CourseSection courseSection;  // Раздел курса

    public void setCourseSessionEnrollmentList(List<CourseSessionEnrollment> courseSessionEnrollmentList) {
        this.courseSessionEnrollmentList = courseSessionEnrollmentList;
    }

    public List<CourseSessionEnrollment> getCourseSessionEnrollmentList() {
        return courseSessionEnrollmentList;
    }


    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    public Trainer getTrainer() {
        return trainer;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public CourseSection getCourseSection() {
        return courseSection;
    }

    public void setCourseSection(CourseSection courseSection) {
        this.courseSection = courseSection;
    }


    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setLearningCenter(DicLearningCenter learningCenter) {
        this.learningCenter = learningCenter;
    }

    public DicLearningCenter getLearningCenter() {
        return learningCenter;
    }

    public void setMaxPerson(Integer maxPerson) {
        this.maxPerson = maxPerson;
    }

    public Integer getMaxPerson() {
        return maxPerson;
    }


}