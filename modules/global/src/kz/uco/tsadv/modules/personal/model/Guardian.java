package kz.uco.tsadv.modules.personal.model;

import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.dictionary.DicFieldOfActivity;
import kz.uco.tsadv.modules.personal.dictionary.DicGuardianType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Table(name = "TSADV_GUARDIAN")
@Entity(name = "tsadv_Guardian")
public class Guardian extends AbstractParentEntity {
    private static final long serialVersionUID = -8364965533181551901L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "GUARDIAN_TYPE_ID")
    protected DicGuardianType guardianType;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FIELD_OF_ACTIVITY_ID")
    protected DicFieldOfActivity fieldOfActivity;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public DicFieldOfActivity getFieldOfActivity() {
        return fieldOfActivity;
    }

    public void setFieldOfActivity(DicFieldOfActivity fieldOfActivity) {
        this.fieldOfActivity = fieldOfActivity;
    }

    public DicGuardianType getGuardianType() {
        return guardianType;
    }

    public void setGuardianType(DicGuardianType guardianType) {
        this.guardianType = guardianType;
    }
}