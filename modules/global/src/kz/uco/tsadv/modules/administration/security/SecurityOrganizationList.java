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
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import com.haulmont.cuba.core.entity.StandardEntity;

@Table(name = "TSADV_SECURITY_ORGANIZATION_LIST")
@Entity(name = "tsadv$SecurityOrganizationList")
public class SecurityOrganizationList extends StandardEntity {
    private static final long serialVersionUID = -1584367897224751901L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SECURITY_GROUP_ID")
    protected Group securityGroup;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ORGANIZATION_GROUP_ID")
    protected OrganizationGroupExt organizationGroup;

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

    public void setOrganizationGroup(OrganizationGroupExt organizationGroup) {
        this.organizationGroup = organizationGroup;
    }

    public OrganizationGroupExt getOrganizationGroup() {
        return organizationGroup;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }


}