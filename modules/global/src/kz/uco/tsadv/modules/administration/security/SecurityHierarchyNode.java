package kz.uco.tsadv.modules.administration.security;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.security.entity.Group;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import com.haulmont.cuba.core.entity.StandardEntity;

@Table(name = "TSADV_SECURITY_HIERARCHY_NODE")
@Entity(name = "tsadv$SecurityHierarchyNode")
public class SecurityHierarchyNode extends StandardEntity {
    private static final long serialVersionUID = 4313319157806171563L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SECURITY_GROUP_ID")
    protected Group securityGroup;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ORGANIZATION_GROUP_ID")
    protected OrganizationGroupExt organizationGroup;

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


}