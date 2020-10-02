package kz.uco.tsadv.modules.personal.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import javax.persistence.ManyToOne;
import kz.uco.base.entity.dictionary.DicCurrency;

@NamePattern("%s|organization")
@Table(name = "TSADV_ORGANIZATION_PROPERTIES")
@Entity(name = "tsadv$OrganizationProperties")
public class OrganizationProperties extends StandardEntity {
    private static final long serialVersionUID = -2436022283798656662L;

    @Lookup(type = LookupType.SCREEN, actions = {"lookup"})
    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ORGANIZATION_ID")
    protected OrganizationGroupExt organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FUNCTIONAL_CURRENCY_ID")
    protected DicCurrency functionalCurrency;

    public void setFunctionalCurrency(DicCurrency functionalCurrency) {
        this.functionalCurrency = functionalCurrency;
    }

    public DicCurrency getFunctionalCurrency() {
        return functionalCurrency;
    }


    public void setOrganization(OrganizationGroupExt organization) {
        this.organization = organization;
    }

    public OrganizationGroupExt getOrganization() {
        return organization;
    }


}