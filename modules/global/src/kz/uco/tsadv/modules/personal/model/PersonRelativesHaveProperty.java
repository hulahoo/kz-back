package kz.uco.tsadv.modules.personal.model;

import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.enums.YesNoEnum;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;

@Table(name = "TSADV_PERSON_RELATIVES_HAVE_PROPERTY")
@Entity(name = "tsadv_PersonRelativesHaveProperty")
public class PersonRelativesHaveProperty extends AbstractParentEntity {
    private static final long serialVersionUID = 1346012739202922025L;

    @Column(name = "HAVE_OR_NOT")
    private String haveOrNot;

    @Column(name = "PROPERTY", length = 2000)
    private String property;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_ID")
    private PersonGroupExt personGroup;

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public YesNoEnum getHaveOrNot() {
        return haveOrNot == null ? null : YesNoEnum.fromId(haveOrNot);
    }

    public void setHaveOrNot(YesNoEnum haveOrNot) {
        this.haveOrNot = haveOrNot == null ? null : haveOrNot.getId();
    }
}