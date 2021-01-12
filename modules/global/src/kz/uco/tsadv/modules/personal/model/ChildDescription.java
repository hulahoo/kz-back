package kz.uco.tsadv.modules.personal.model;

import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.enums.YesNoEnum;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;

@Table(name = "TSADV_CHILD_DESCRIPTION")
@Entity(name = "tsadv_ChildDescription")
public class ChildDescription extends AbstractParentEntity {
    private static final long serialVersionUID = -7324926591820665746L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_ID")
    private PersonGroupExt personGroup;

    @Column(name = "HAVE_DISABLED_CHILD")
    private String haveDisabledChild;

    @Column(name = "HAVE_LITTLE_CHILD")
    private String haveLittleChild;

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public YesNoEnum getHaveLittleChild() {
        return haveLittleChild == null ? null : YesNoEnum.fromId(haveLittleChild);
    }

    public void setHaveLittleChild(YesNoEnum haveLittleChild) {
        this.haveLittleChild = haveLittleChild == null ? null : haveLittleChild.getId();
    }

    public YesNoEnum getHaveDisabledChild() {
        return haveDisabledChild == null ? null : YesNoEnum.fromId(haveDisabledChild);
    }

    public void setHaveDisabledChild(YesNoEnum haveDisabledChild) {
        this.haveDisabledChild = haveDisabledChild == null ? null : haveDisabledChild.getId();
    }
}