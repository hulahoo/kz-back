package kz.uco.tsadv.entity.dbview;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DesignSupport;
import com.haulmont.cuba.core.global.UserSessionSource;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@DesignSupport("{'dbView':true,'generateDdl':false}")
@Table(name = "aa_organization_vw")
@Entity(name = "tsadv$OrganizationSsView")
public class OrganizationSsView extends StandardEntity {
    private static final long serialVersionUID = -5885079025802457789L;

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE")
    protected Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE")
    protected Date endDate;

    @Column(name = "MAX_START_DATE")
    protected String maxStartDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_GROUP_ID")
    protected OrganizationGroupExt organizationGroup;

    @Column(name = "ORGANIZATION_NAME_RU")
    protected String organizationNameRu;

    @Column(name = "ORGANIZATION_NAME_KZ")
    protected String organizationNameKz;

    @Column(name = "ORGANIZATION_NAME_EN")
    protected String organizationNameEn;

    @Transient
    @MetaProperty(related = {"organizationNameRu", "organizationNameKz", "organizationNameEn"})
    protected String organizationName;

    @Column(name = "COST_CENTER")
    protected String costCenter;

    public void setOrganizationGroup(OrganizationGroupExt organizationGroup) {
        this.organizationGroup = organizationGroup;
    }

    public OrganizationGroupExt getOrganizationGroup() {
        return organizationGroup;
    }


    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getOrganizationName() {
        UserSessionSource userSessionSource = AppBeans.get("cuba_UserSessionSource");
        String language = userSessionSource.getLocale().getLanguage();
        String positionOrder = com.haulmont.cuba.core.sys.AppContext.getProperty("base.abstractDictionary.langOrder");
        if (positionOrder != null) {
            List<String> langs = Arrays.asList(positionOrder.split(";"));
            switch (langs.indexOf(language)) {
                case 0:
                    return organizationNameRu;
                case 1:
                    return organizationNameKz;
                case 2:
                    return organizationNameEn;
            }
        }
        return organizationNameRu;
    }


    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setMaxStartDate(String maxStartDate) {
        this.maxStartDate = maxStartDate;
    }

    public String getMaxStartDate() {
        return maxStartDate;
    }

    public void setOrganizationNameRu(String organizationNameRu) {
        this.organizationNameRu = organizationNameRu;
    }

    public String getOrganizationNameRu() {
        return organizationNameRu;
    }

    public void setOrganizationNameKz(String organizationNameKz) {
        this.organizationNameKz = organizationNameKz;
    }

    public String getOrganizationNameKz() {
        return organizationNameKz;
    }

    public void setOrganizationNameEn(String organizationNameEn) {
        this.organizationNameEn = organizationNameEn;
    }

    public String getOrganizationNameEn() {
        return organizationNameEn;
    }

    public void setCostCenter(String costCenter) {
        this.costCenter = costCenter;
    }

    public String getCostCenter() {
        return costCenter;
    }


}