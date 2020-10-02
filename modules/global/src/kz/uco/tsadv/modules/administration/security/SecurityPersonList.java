package kz.uco.tsadv.modules.administration.security;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.security.entity.Group;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import com.haulmont.cuba.core.entity.StandardEntity;

@Table(name = "TSADV_SECURITY_PERSON_LIST")
@Entity(name = "tsadv$SecurityPersonList")
public class SecurityPersonList extends StandardEntity {
    private static final long serialVersionUID = 5229190547862778022L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SECURITY_GROUP_ID")
    protected Group securityGroup;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "TRANSACTION_DATE", nullable = false)
    protected Date transactionDate;

    public void setSecurityGroup(Group securityGroup) {
        this.securityGroup = securityGroup;
    }

    public Group getSecurityGroup() {
        return securityGroup;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }


}