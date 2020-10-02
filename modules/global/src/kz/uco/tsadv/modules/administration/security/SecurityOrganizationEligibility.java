package kz.uco.tsadv.modules.administration.security;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.security.entity.Group;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import com.haulmont.cuba.core.entity.StandardEntity;

@Table(name = "TSADV_SECURITY_ORGANIZATION_ELIGIBILITY")
@Entity(name = "tsadv$SecurityOrganizationEligibility")
public class SecurityOrganizationEligibility extends StandardEntity {
    private static final long serialVersionUID = -7863368554321301756L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SECURITY_GROUP_ID")
    protected Group securityGroup;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ORGANIZATION_GROUP_ID")
    protected OrganizationGroupExt organizationGroup;

    @NotNull
    @Column(name = "INCLUDE_", nullable = false, columnDefinition = "Boolean default true")
    protected Boolean include = true;

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

    public void setInclude(Boolean include) {
        this.include = include;
    }

    public Boolean getInclude() {
        return include;
    }


}