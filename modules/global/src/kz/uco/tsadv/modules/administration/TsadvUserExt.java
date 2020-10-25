package kz.uco.tsadv.modules.administration;

import com.haulmont.cuba.core.entity.annotation.Extends;
import kz.uco.base.entity.extend.UserExt;
import kz.uco.base.entity.shared.PersonGroup;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name = "tsadv$UserExt")
@Extends(UserExt.class)
public class TsadvUserExt extends UserExt {
    private static final long serialVersionUID = 6933569044418446062L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_ID")
    private PersonGroup personGroup;

    public PersonGroup getPersonGroup() {
        return personGroup;
    }

    public void setPersonGroup(PersonGroup personGroup) {
        this.personGroup = personGroup;
    }


}