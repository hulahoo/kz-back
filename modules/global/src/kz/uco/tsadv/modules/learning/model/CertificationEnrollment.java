package kz.uco.tsadv.modules.learning.model;

import kz.uco.tsadv.modules.learning.enums.CertificationStatus;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import java.util.Date;

@Table(name = "TSADV_CERTIFICATION_ENROLLMENT")
@Entity(name = "tsadv$CertificationEnrollment")
public class CertificationEnrollment extends AbstractParentEntity {
    private static final long serialVersionUID = -3986736832964782021L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE", nullable = false)
    protected Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "NEXT_DATE")
    protected Date nextDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CERTIFICATION_ID")
    protected kz.uco.tsadv.modules.learning.model.Certification certification;

    @Column(name = "STATUS", nullable = false)
    protected Integer status;

    public void setStatus(CertificationStatus status) {
        this.status = status == null ? null : status.getId();
    }

    public CertificationStatus getStatus() {
        return status == null ? null : CertificationStatus.fromId(status);
    }


    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setNextDate(Date nextDate) {
        this.nextDate = nextDate;
    }

    public Date getNextDate() {
        return nextDate;
    }

    public void setCertification(kz.uco.tsadv.modules.learning.model.Certification certification) {
        this.certification = certification;
    }

    public Certification getCertification() {
        return certification;
    }


}