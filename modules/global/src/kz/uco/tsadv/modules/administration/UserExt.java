package kz.uco.tsadv.modules.administration;

import com.haulmont.cuba.core.entity.annotation.Extends;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name = "tsadv$UserExt")
@Extends(kz.uco.base.entity.extend.UserExt.class)
public class UserExt extends kz.uco.base.entity.extend.UserExt {
    private static final long serialVersionUID = 6933569044418446062L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_ID")
    private PersonGroupExt personGroup;

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }


}