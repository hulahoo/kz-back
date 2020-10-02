package kz.uco.tsadv.entity.dbview;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.global.DesignSupport;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import kz.uco.tsadv.modules.learning.enums.EnrollmentStatus;
import kz.uco.tsadv.modules.learning.model.Course;
import kz.uco.tsadv.modules.learning.model.CourseSection;
import kz.uco.tsadv.modules.learning.model.Enrollment;
import kz.uco.tsadv.modules.learning.model.Test;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s %s|fullName,testName")
@DesignSupport("{'dbView':true,'generateDdl':false}")
@Table(name = "TSADV_ASSIGNED_TEST_ENROLLMENT_V")
@Entity(name = "tsadv$AssignedTestEnrollment")
public class AssignedTestEnrollment extends BaseUuidEntity {
    private static final long serialVersionUID = -6313415288424298121L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ENROLLMENT_ID")
    protected Enrollment enrollment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PG_ID")
    protected PersonGroupExt personGroup;

    @Column(name = "FULL_NAME")
    protected String fullName;

    @Column(name = "ORGANIZATION_NAME_LANG1")
    protected String organizationNameLang1;

    @Column(name = "POSITION_NAME_LANG1")
    protected String positionNameLang1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEST_ID")
    protected Test test;

    @Column(name = "TEST_NAME")
    protected String testName;

    @Column(name = "ENROLLMENT_STATUS")
    protected Integer enrollmentStatus;

    @Column(name = "SUCCESS")
    protected Boolean success;

    @Column(name = "ATTEMPTS")
    protected Integer attempts;

    @Column(name = "TEST_RESULT")
    protected Integer testResult;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COURSE_SECTION_ID")
    protected CourseSection courseSection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COURSE_ID")
    protected Course course;

    @Column(name = "SECTION_NAME")
    protected String sectionName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORG_ID")
    protected OrganizationGroupExt organization;

    @Column(name = "CREATED_BY")
    protected String createdBy;

    public void setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
    }

    public Enrollment getEnrollment() {
        return enrollment;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setOrganizationNameLang1(String organizationNameLang1) {
        this.organizationNameLang1 = organizationNameLang1;
    }

    public String getOrganizationNameLang1() {
        return organizationNameLang1;
    }

    public void setPositionNameLang1(String positionNameLang1) {
        this.positionNameLang1 = positionNameLang1;
    }

    public String getPositionNameLang1() {
        return positionNameLang1;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public Test getTest() {
        return test;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getTestName() {
        return testName;
    }

    public void setEnrollmentStatus(EnrollmentStatus enrollmentStatus) {
        this.enrollmentStatus = enrollmentStatus == null ? null : enrollmentStatus.getId();
    }

    public EnrollmentStatus getEnrollmentStatus() {
        return enrollmentStatus == null ? null : EnrollmentStatus.fromId(enrollmentStatus);
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setAttempts(Integer attempts) {
        this.attempts = attempts;
    }

    public Integer getAttempts() {
        return attempts;
    }

    public void setTestResult(Integer testResult) {
        this.testResult = testResult;
    }

    public Integer getTestResult() {
        return testResult;
    }

    public void setCourseSection(CourseSection courseSection) {
        this.courseSection = courseSection;
    }

    public CourseSection getCourseSection() {
        return courseSection;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Course getCourse() {
        return course;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setOrganization(OrganizationGroupExt organization) {
        this.organization = organization;
    }

    public OrganizationGroupExt getOrganization() {
        return organization;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedBy() {
        return createdBy;
    }


}