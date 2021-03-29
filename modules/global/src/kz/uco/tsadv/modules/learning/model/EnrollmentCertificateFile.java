package kz.uco.tsadv.modules.learning.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.FileDescriptor;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Table(name = "TSADV_ENROLLMENT_CERTIFICATE_FILE")
@Entity(name = "tsadv$EnrollmentCertificateFile")
public class EnrollmentCertificateFile extends StandardEntity {
    private static final long serialVersionUID = -2223348495285717215L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ENROLLMENT_ID")
    protected Enrollment enrollment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CERTIFICATE_FILE_ID")
    protected FileDescriptor certificateFile;

    public void setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
    }

    public Enrollment getEnrollment() {
        return enrollment;
    }

    public void setCertificateFile(FileDescriptor certificateFile) {
        this.certificateFile = certificateFile;
    }

    public FileDescriptor getCertificateFile() {
        return certificateFile;
    }

}