package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Extends;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import com.haulmont.cuba.core.entity.annotation.PublishEntityChangedEvents;
import kz.uco.base.entity.abstraction.IGroupedEntity;
import kz.uco.base.entity.shared.Organization;
import kz.uco.tsadv.modules.personal.dictionary.DicCostCenter;
import kz.uco.tsadv.modules.personal.dictionary.DicPayroll;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;

import javax.persistence.*;

@PublishEntityChangedEvents
@Listeners("tsadv_OrganizationListener")
@NamePattern("%s|organizationName,organizationNameLang2,organizationNameLang3,organizationNameLang4,organizationNameLang5,startDate,endDate,organizationNameLang1")
@Extends(Organization.class)
@Entity(name = "base$OrganizationExt")
public class OrganizationExt extends Organization implements IGroupedEntity<OrganizationGroupExt> {
    private static final long serialVersionUID = 5012598590706050601L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID")
    protected OrganizationGroupExt group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COST_CENTER_ID")
    protected DicCostCenter costCenter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PAYROLL_ID")
    protected DicPayroll payroll;

    @Column(name = "INTERNAL")
    protected Boolean internal;

    @SuppressWarnings("all")
    @MetaProperty(related = "organizationNameLang1")
    @Transient
    public String getOrganizationNameLang1Reducted() {
        if (organizationNameLang1 != null) {
            if (organizationNameLang1.length() > 25) {
                return organizationNameLang1.substring(0, 25) + " ...";
            } else {
                return organizationNameLang1;
            }
        } else {
            return "";
        }
    }

    @SuppressWarnings("all")
    @MetaProperty(related = "organizationNameLang2")
    @Transient
    public String getOrganizationNameLang2Reducted() {
        if (organizationNameLang2 != null) {
            if (organizationNameLang2.length() > 25) {
                return organizationNameLang2.substring(0, 25) + " ...";
            } else {
                return organizationNameLang2;
            }
        } else {
            return "";
        }
    }

    @SuppressWarnings("all")
    @MetaProperty(related = "organizationNameLang3")
    @Transient
    public String getOrganizationNameLang3Reducted() {
        if (organizationNameLang3 != null) {
            if (organizationNameLang3.length() > 25) {
                return organizationNameLang3.substring(0, 25) + " ...";
            } else {
                return organizationNameLang3;
            }
        } else {
            return "";
        }
    }

    @SuppressWarnings("all")
    @MetaProperty(related = "organizationNameLang4")
    @Transient
    public String getOrganizationNameLang4Reducted() {
        if (organizationNameLang4 != null) {
            if (organizationNameLang4.length() > 25) {
                return organizationNameLang4.substring(0, 25) + " ...";
            } else {
                return organizationNameLang4;
            }
        } else {
            return "";
        }
    }

    @SuppressWarnings("all")
    @MetaProperty(related = "organizationNameLang5")
    @Transient
    public String getOrganizationNameLang5Reducted() {
        if (organizationNameLang5 != null) {
            if (organizationNameLang5.length() > 25) {
                return organizationNameLang5.substring(0, 25) + " ...";
            } else {
                return organizationNameLang5;
            }
        } else {
            return "";
        }
    }

    public void setInternal(Boolean internal) {
        this.internal = internal;
    }

    public Boolean getInternal() {
        return internal;
    }

    public OrganizationGroupExt getGroup() {
        return group;
    }

    public void setGroup(OrganizationGroupExt group) {
        this.group = group;
    }

    public DicCostCenter getCostCenter() {
        return costCenter;
    }

    public void setCostCenter(DicCostCenter costCenter) {
        this.costCenter = costCenter;
    }

    public DicPayroll getPayroll() {
        return payroll;
    }

    public void setPayroll(DicPayroll payroll) {
        this.payroll = payroll;
    }

}