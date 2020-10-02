package kz.uco.tsadv.modules.learning.model;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.learning.dictionary.DicReasonForLearning;
import kz.uco.tsadv.modules.learning.enums.EnrollmentStatus;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import java.util.Date;

/**
 * Зачисление на курс
 */
@NamePattern("%s|course")
@Table(name = "TSADV_ENROLLMENT")
@Entity(name = "tsadv$Enrollment")
public class Enrollment extends AbstractParentEntity {
    private static final long serialVersionUID = 2520575376425304199L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COURSE_ID")
    protected Course course;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @Column(name = "STATUS", nullable = false)
    protected Integer status;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_", nullable = false)
    protected Date date;

    @Column(name = "REASON", length = 1000)
    protected String reason;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CERTIFICATION_ENROLLMENT_ID")
    protected CertificationEnrollment certificationEnrollment;

    @Column(name = "MONEY_IN_BUDGET")
    protected Boolean moneyInBudget;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REASON_FOR_LEARNING_ID")
    protected DicReasonForLearning reasonForLearning;

    public void setReasonForLearning(DicReasonForLearning reasonForLearning) {
        this.reasonForLearning = reasonForLearning;
    }

    public DicReasonForLearning getReasonForLearning() {
        return reasonForLearning;
    }


    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }



    public void setMoneyInBudget(Boolean moneyInBudget) {
        this.moneyInBudget = moneyInBudget;
    }

    public Boolean getMoneyInBudget() {
        return moneyInBudget;
    }


    public EnrollmentStatus getStatus() {
        return status == null ? null : EnrollmentStatus.fromId(status);
    }

    public void setStatus(EnrollmentStatus status) {
        this.status = status == null ? null : status.getId();
    }


    public void setCertificationEnrollment(CertificationEnrollment certificationEnrollment) {
        this.certificationEnrollment = certificationEnrollment;
    }

    public CertificationEnrollment getCertificationEnrollment() {
        return certificationEnrollment;
    }


    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }




    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }


}