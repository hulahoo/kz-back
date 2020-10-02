package kz.uco.tsadv.modules.learning.model;

import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.learning.enums.EnrollmentStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Table(name = "TSADV_COURSE_SESSION_ENROLLMENT")
@Entity(name = "tsadv$CourseSessionEnrollment")
public class CourseSessionEnrollment extends AbstractParentEntity {
    private static final long serialVersionUID = -2154579572049276863L;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ENROLLMENT_ID")
    protected Enrollment enrollment;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COURSE_SESSION_ID")
    protected CourseSectionSession courseSession;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "ENROLLMENT_DATE", nullable = false)
    protected Date enrollmentDate;

    @Column(name = "STATUS")
    protected Integer status;

    @Column(name = "COMMENT_", length = 1000)
    protected String comment;


    public void setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
    }

    public Enrollment getEnrollment() {
        return enrollment;
    }

    public void setCourseSession(CourseSectionSession courseSession) {
        this.courseSession = courseSession;
    }

    public CourseSectionSession getCourseSession() {
        return courseSession;
    }

    public void setEnrollmentDate(Date enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public Date getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setStatus(EnrollmentStatus status) {
        this.status = status == null ? null : status.getId();
    }

    public EnrollmentStatus getStatus() {
        return status == null ? null : EnrollmentStatus.fromId(status);
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

}