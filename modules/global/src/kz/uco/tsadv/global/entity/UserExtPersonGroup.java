package kz.uco.tsadv.global.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.haulmont.chile.core.annotations.MetaProperty;
import kz.uco.base.entity.extend.UserExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import org.apache.commons.lang3.StringUtils;

@Table(name = "TSADV_USER_EXT_PERSON_GROUP")
@Entity(name = "tsadv$UserExtPersonGroup")
public class UserExtPersonGroup extends AbstractParentEntity {
    private static final long serialVersionUID = 1444706101678996108L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_EXT_ID")
    protected UserExt userExt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    public void setUserExt(UserExt userExt) {
        this.userExt = userExt;
    }

    public UserExt getUserExt() {
        return userExt;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    @MetaProperty
    public String getFullName() {
        String fullName = personGroup != null ? personGroup.getFioWithEmployeeNumber() : null;
        if (StringUtils.isBlank(fullName)) {
            fullName = userExt.getFullName();
        }
        return fullName != null ? fullName : "";
    }

    public String getFullName(String lang) {
        String fullName = personGroup != null ? personGroup.getPerson() != null ?
                personGroup.getPerson().getFullNameLatin(lang) : null : null;
        if (StringUtils.isBlank(fullName)) {
            fullName = userExt.getFullName();
        }
        if (fullName.contains("null")){
            return getFullName();
        }
        return fullName;
    }
}