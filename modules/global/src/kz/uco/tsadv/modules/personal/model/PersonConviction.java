package kz.uco.tsadv.modules.personal.model;

import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.enums.YesNoEnum;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;

@Table(name = "TSADV_PERSON_CONVICTION")
@Entity(name = "tsadv_PersonConviction")
public class PersonConviction extends AbstractParentEntity {
    private static final long serialVersionUID = 4790686361417674457L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_ID")
    private PersonGroupExt personGroup;

    @Column(name = "HAVE_CONVICTION")
    private String haveConviction;

    @Column(name = "REASON", length = 2000)
    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public YesNoEnum getHaveConviction() {
        return haveConviction == null ? null : YesNoEnum.fromId(haveConviction);
    }

    public void setHaveConviction(YesNoEnum haveConviction) {
        this.haveConviction = haveConviction == null ? null : haveConviction.getId();
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }
}