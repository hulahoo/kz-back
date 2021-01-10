package kz.uco.tsadv.modules.personal.model;

import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;

@Table(name = "TSADV_PERSON_REASON_CHANGING_JOB")
@Entity(name = "tsadv_PersonReasonChangingJob")
public class PersonReasonChangingJob extends AbstractParentEntity {
    private static final long serialVersionUID = -3632026559425715999L;

    @Lob
    @Column(name = "REASON")
    private String reason;

    @Column(name = "TEL_FULL_NAME_HR", length = 2000)
    private String telFullNameHR;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_ID")
    private PersonGroupExt personGroup;

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public String getTelFullNameHR() {
        return telFullNameHR;
    }

    public void setTelFullNameHR(String telFullNameHR) {
        this.telFullNameHR = telFullNameHR;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}