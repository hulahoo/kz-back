package kz.uco.tsadv.modules.recognition;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import kz.uco.base.entity.shared.Hierarchy;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import com.haulmont.cuba.core.entity.StandardEntity;

@Table(name = "TSADV_COIN_DISTRIBUTION_ORGANIZATION")
@Entity(name = "tsadv$CoinDistributionOrganization")
public class CoinDistributionOrganization extends StandardEntity {
    private static final long serialVersionUID = -6945472857734570066L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ORGANIZATION_GROUP_ID")
    protected OrganizationGroupExt parentOrganizationGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HIERARCHY_ID")
    protected Hierarchy hierarchy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_GROUP_ID")
    protected OrganizationGroupExt organizationGroup;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COIN_DISTRIBUTION_RULE_ID")
    protected CoinDistributionRule coinDistributionRule;

    public void setCoinDistributionRule(CoinDistributionRule coinDistributionRule) {
        this.coinDistributionRule = coinDistributionRule;
    }

    public CoinDistributionRule getCoinDistributionRule() {
        return coinDistributionRule;
    }


    public void setParentOrganizationGroup(OrganizationGroupExt parentOrganizationGroup) {
        this.parentOrganizationGroup = parentOrganizationGroup;
    }

    public OrganizationGroupExt getParentOrganizationGroup() {
        return parentOrganizationGroup;
    }

    public void setHierarchy(Hierarchy hierarchy) {
        this.hierarchy = hierarchy;
    }

    public Hierarchy getHierarchy() {
        return hierarchy;
    }

    public void setOrganizationGroup(OrganizationGroupExt organizationGroup) {
        this.organizationGroup = organizationGroup;
    }

    public OrganizationGroupExt getOrganizationGroup() {
        return organizationGroup;
    }


}