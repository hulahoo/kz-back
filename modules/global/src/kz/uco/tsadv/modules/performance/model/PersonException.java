package kz.uco.tsadv.modules.performance.model;

import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Table(name = "TSADV_PERSON_EXCEPTION")
@Entity(name = "tsadv_PersonException")
public class PersonException extends AbstractParentEntity {
    private static final long serialVersionUID = 2411738185705024406L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_GROUP_ID", unique = true)
    protected PersonGroupExt personGroup;

    @NotNull
    @Column(name = "MAX_BONUS", nullable = false)
    protected Double maxBonus;

    public Double getMaxBonus() {
        return maxBonus;
    }

    public void setMaxBonus(Double maxBonus) {
        this.maxBonus = maxBonus;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }
}