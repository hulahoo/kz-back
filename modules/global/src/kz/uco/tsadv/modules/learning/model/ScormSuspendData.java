package kz.uco.tsadv.modules.learning.model;

import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Table(name = "TSADV_SCORM_SUSPEND_DATA")
@Entity(name = "tsadv_ScormSuspendData")
public class ScormSuspendData extends StandardEntity {
    private static final long serialVersionUID = 3399276928615614203L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ENROLLMENT_ID")
    protected Enrollment enrollment;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COURSE_SECTION_ID")
    protected CourseSection courseSection;

    @Lob
    @Column(name = "SUSPEND_DATA")
    protected String suspendData;

    public String getSuspendData() {
        return suspendData;
    }

    public void setSuspendData(String suspendData) {
        this.suspendData = suspendData;
    }

    public CourseSection getCourseSection() {
        return courseSection;
    }

    public void setCourseSection(CourseSection courseSection) {
        this.courseSection = courseSection;
    }

    public Enrollment getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
    }
}