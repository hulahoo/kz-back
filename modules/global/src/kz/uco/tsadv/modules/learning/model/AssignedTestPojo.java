package kz.uco.tsadv.modules.learning.model;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import kz.uco.tsadv.modules.learning.enums.EnrollmentStatus;

import java.util.UUID;

@MetaClass(name = "tsadv$AssignedTestPojo")
public class AssignedTestPojo extends BaseUuidEntity {
    private static final long serialVersionUID = -2560966909637514052L;

    @MetaProperty
    protected UUID personGroupId;

    @MetaProperty
    protected String personFullName;

    @MetaProperty
    protected String position;

    @MetaProperty
    protected String organization;

    @MetaProperty
    protected UUID testId;

    @MetaProperty
    protected String testName;

    @MetaProperty
    protected Boolean success = false;

    @MetaProperty
    protected Long attemptsCount = 0L;

    @MetaProperty
    protected Long score;

    @MetaProperty
    protected UUID enrollmentId;

    @MetaProperty
    protected UUID courseSectionId;

    @MetaProperty
    protected String courseSectionName;

    @MetaProperty
    protected UUID courseId;

    @MetaProperty
    protected Integer enrollmentStatus;

    @MetaProperty
    protected UUID organizationGroupId;

    @MetaProperty
    protected String createdByLogin;

    public String getCreatedByLogin() {
        return createdByLogin;
    }

    public void setCreatedByLogin(String createdByLogin) {
        this.createdByLogin = createdByLogin;
    }

    public String getCourseSectionName() {
        return courseSectionName;
    }

    public void setCourseSectionName(String courseSectionName) {
        this.courseSectionName = courseSectionName;
    }

    public UUID getCourseId() {
        return courseId;
    }

    public void setCourseId(UUID courseId) {
        this.courseId = courseId;
    }

    public UUID getCourseSectionId() {
        return courseSectionId;
    }

    public void setCourseSectionId(UUID courseSectionId) {
        this.courseSectionId = courseSectionId;
    }

    public UUID getPersonGroupId() {
        return personGroupId;
    }

    public void setPersonGroupId(UUID personGroupId) {
        this.personGroupId = personGroupId;
    }

    public String getPersonFullName() {
        return personFullName;
    }

    public void setPersonFullName(String personFullName) {
        this.personFullName = personFullName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public UUID getTestId() {
        return testId;
    }

    public void setTestId(UUID testId) {
        this.testId = testId;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Long getAttemptsCount() {
        return attemptsCount;
    }

    public void setAttemptsCount(Long attemptsCount) {
        this.attemptsCount = attemptsCount;
    }

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }

    public UUID getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(UUID enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public void setEnrollmentStatus(Integer enrollmentStatus) {
        this.enrollmentStatus = enrollmentStatus;
    }

    public void setEnrollmentStatus(EnrollmentStatus enrollmentStatus) {
        this.enrollmentStatus = enrollmentStatus == null ? null : enrollmentStatus.getId();
    }

    public EnrollmentStatus getEnrollmentStatus() {
        return enrollmentStatus == null ? null : EnrollmentStatus.fromId(enrollmentStatus);
    }


    public UUID getOrganizationGroupId() {
        return organizationGroupId;
    }

    public void setOrganizationGroupId(UUID organizationGroupId) {
        this.organizationGroupId = organizationGroupId;
    }
}