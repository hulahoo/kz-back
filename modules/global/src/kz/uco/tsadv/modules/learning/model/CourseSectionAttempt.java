package kz.uco.tsadv.modules.learning.model;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.*;
import java.util.Date;

/**
 * Попытка прохождения раздела курса
 */
@Listeners("tsadv_CourseSectionAttemptListener")
@NamePattern("%s|courseSection")
@Table(name = "TSADV_COURSE_SECTION_ATTEMPT")
@Entity(name = "tsadv$CourseSectionAttempt")
public class CourseSectionAttempt extends AbstractParentEntity {
    private static final long serialVersionUID = -5706108774749788592L;

    @Temporal(TemporalType.DATE)
    @Column(name = "ATTEMPT_DATE")
    protected Date attemptDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEST_ID")
    protected Test test;

    @Column(name = "TEST_RESULT")
    protected Integer testResult;

    @Column(name = "TEST_RESULT_PERCENT")
    protected Integer testResultPercent;

    @Column(name = "TIME_SPENT")
    protected Long timeSpent;

    @Column(name = "ACTIVE_ATTEMPT")
    protected Boolean activeAttempt;

    @Column(name = "SUCCESS", nullable = false)
    protected Boolean success = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ENROLLMENT_ID")
    protected Enrollment enrollment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COURSE_SECTION_ID")
    protected CourseSection courseSection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COURSE_SECTION_SESSION_ID")
    protected CourseSectionSession courseSectionSession;



    public void setTestResultPercent(Integer testResultPercent) {
        this.testResultPercent = testResultPercent;
    }

    public Integer getTestResultPercent() {
        return testResultPercent;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public Test getTest() {
        return test;
    }

    public void setTestResult(Integer testResult) {
        this.testResult = testResult;
    }

    public Integer getTestResult() {
        return testResult;
    }

    public void setTimeSpent(Long timeSpent) {
        this.timeSpent = timeSpent;
    }

    public Long getTimeSpent() {
        return timeSpent;
    }


    public CourseSectionSession getCourseSectionSession() {
        return courseSectionSession;
    }

    public void setCourseSectionSession(CourseSectionSession courseSectionSession) {
        this.courseSectionSession = courseSectionSession;
    }


    public void setActiveAttempt(Boolean activeAttempt) {
        this.activeAttempt = activeAttempt;
    }

    public Boolean getActiveAttempt() {
        return activeAttempt;
    }


    public void setCourseSection(CourseSection courseSection) {
        this.courseSection = courseSection;
    }

    public CourseSection getCourseSection() {
        return courseSection;
    }


    @MetaProperty(related = "courseSection")
    public String getCourseSectionFormat() {
        return courseSection.getFormat().getLangValue();
    }

    public void setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
    }

    public Enrollment getEnrollment() {
        return enrollment;
    }


    public void setAttemptDate(Date attemptDate) {
        this.attemptDate = attemptDate;
    }

    public Date getAttemptDate() {
        return attemptDate;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Boolean getSuccess() {
        return success;
    }

//    public String getSectionName() {
//        if (courseSection != null) {
//            return courseSection.getSectionName();
//        }
//        return null;
//    }

}