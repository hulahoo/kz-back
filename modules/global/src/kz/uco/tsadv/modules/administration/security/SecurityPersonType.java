package kz.uco.tsadv.modules.administration.security;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.security.entity.Group;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import kz.uco.tsadv.modules.personal.dictionary.DicPersonType;
import com.haulmont.cuba.core.entity.StandardEntity;

@Table(name = "TSADV_SECURITY_PERSON_TYPE")
@Entity(name = "tsadv$SecurityPersonType")
public class SecurityPersonType extends StandardEntity {
    private static final long serialVersionUID = 1057068464069507895L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SECURITY_GROUP_ID")
    protected Group securityGroup;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_TYPE_ID")
    protected DicPersonType personType;

    public void setSecurityGroup(Group securityGroup) {
        this.securityGroup = securityGroup;
    }

    public Group getSecurityGroup() {
        return securityGroup;
    }

    public void setPersonType(DicPersonType personType) {
        this.personType = personType;
    }

    public DicPersonType getPersonType() {
        return personType;
    }


}