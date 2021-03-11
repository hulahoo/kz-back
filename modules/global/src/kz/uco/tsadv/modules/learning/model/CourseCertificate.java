package kz.uco.tsadv.modules.learning.model;

import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.reports.entity.Report;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Table(name = "TSADV_COURSE_CERTIFICATE")
@Entity(name = "tsadv_CourseCertificate")
public class CourseCertificate extends StandardEntity {
    private static final long serialVersionUID = -5303313609830983143L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CERTIFICATE_ID")
    protected Report certificate;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "START_DATE", nullable = false)
    protected Date startDate;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "END_DATE", nullable = false)
    protected Date endDate;

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Report getCertificate() {
        return certificate;
    }

    public void setCertificate(Report certificate) {
        this.certificate = certificate;
    }
}