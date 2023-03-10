package kz.uco.tsadv.modules.personal.group;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Extends;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.core.sys.AppContext;
import kz.uco.base.common.BaseCommonUtils;
import kz.uco.base.entity.abstraction.IEntityGroup;
import kz.uco.base.entity.dictionary.DicCompany;
import kz.uco.base.entity.dictionary.DicLocation;
import kz.uco.base.entity.dictionary.DicOrgType;
import kz.uco.base.entity.shared.OrganizationGroup;
import kz.uco.tsadv.modules.performance.model.PerformancePlan;
import kz.uco.tsadv.modules.personal.dictionary.DicCostCenter;
import kz.uco.tsadv.modules.personal.dictionary.DicPayroll;
import kz.uco.tsadv.modules.personal.model.*;
import kz.uco.tsadv.modules.timesheet.model.OrgAnalytics;
import org.eclipse.persistence.annotations.Customizer;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@NamePattern("%s|organization,list,organizationNameLang1,organizationNameLang2,organizationNameLang3,organizationNameLang4,organizationNameLang5")
@Extends(OrganizationGroup.class)
@Entity(name = "base$OrganizationGroupExt")
@Customizer(OrganizationGroupExtDescriptorCustomizer.class)
public class OrganizationGroupExt extends OrganizationGroup implements IEntityGroup<OrganizationExt> {
    private static final long serialVersionUID = -5913796466452223229L;

    @Transient
    @MetaProperty(related = {"organizationNameLang1", "organizationNameLang2", "organizationNameLang3", "organizationNameLang4", "organizationNameLang5"})
    protected String organizationName;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "group")
    protected List<OrganizationExt> list;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID")
    private DicCompany company;

    @JoinColumn(name = "COST_CENTER_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private DicCostCenter costCenter;

    @JoinColumn(name = "PAYROLL_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private DicPayroll payroll;

    @Column(name = "IS_INTERNAL")
    private Boolean is_internal;

    @Column(name = "ORGANIZATION_NAME_LANG1", length = 1000)
    private String organizationNameLang1;

    @Column(name = "ORGANIZATION_NAME_LANG2", length = 1000)
    private String organizationNameLang2;

    @Column(name = "ORGANIZATION_NAME_LANG3", length = 1000)
    private String organizationNameLang3;

    @Column(name = "ORGANIZATION_NAME_LANG4", length = 1000)
    private String organizationNameLang4;

    @Column(name = "ORGANIZATION_NAME_LANG5", length = 1000)
    private String organizationNameLang5;

    @JoinColumn(name = "LOCATION_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private DicLocation location;

    @JoinColumn(name = "ORGANIZATION_TYPE_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private DicOrgType organizationType;

    @OneToMany(mappedBy = "organizationGroupExt")
    protected List<PositionExt> position;

    @Transient
    @MetaProperty(related = "list")
    protected OrganizationExt organization;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "organizationGroup")
    protected List<CompetenceElement> competenceElements;

    @OrderBy("dateFrom")
    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "organizationGroup")
    protected List<OrganizationHrUser> hrUsers;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "organizationGroup")
    protected List<Case> cases;

    @JoinTable(name = "TSADV_PERFORMANCE_PLAN_ORGANIZATION_GROUP_LINK",
            joinColumns = @JoinColumn(name = "ORGANIZATION_GROUP_ID"),
            inverseJoinColumns = @JoinColumn(name = "PERFORMANCE_PLAN_ID"))
    @ManyToMany
    protected List<PerformancePlan> performancePlans;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "organizationGroup")
    protected List<OrganizationGroupGoalLink> goals;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ANALYTICS_ID")
    protected OrgAnalytics analytics;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "group")
    protected OrganizationExt relevantOrganization; // ?????????????? (?????? ?????????????? ?? ???????????? ??????????????) ??????????????????????????

    public void setCompany(DicCompany company) {
        this.company = company;
    }

    public DicCompany getCompany() {
        return company;
    }

    public void setPayroll(DicPayroll payroll) {
        this.payroll = payroll;
    }

    public DicPayroll getPayroll() {
        return payroll;
    }

    public void setCostCenter(DicCostCenter costCenter) {
        this.costCenter = costCenter;
    }

    public DicCostCenter getCostCenter() {
        return costCenter;
    }

    public void setOrganizationType(DicOrgType organizationType) {
        this.organizationType = organizationType;
    }

    public DicOrgType getOrganizationType() {
        return organizationType;
    }

    public void setLocation(DicLocation location) {
        this.location = location;
    }

    public DicLocation getLocation() {
        return location;
    }

    public String getOrganizationName() {
        UserSessionSource userSessionSource = AppBeans.get("cuba_UserSessionSource");
        String language = userSessionSource.getLocale().getLanguage();
        String organizationNameOrder = AppContext.getProperty("base.abstractDictionary.langOrder");
        if (organizationNameOrder != null) {
            List<String> langs = Arrays.asList(organizationNameOrder.split(";"));
            switch (langs.indexOf(language)) {
                case 0:
                    return organizationNameLang1;
                case 1:
                    return organizationNameLang2;
                case 2:
                    return organizationNameLang3;
                case 3:
                    return organizationNameLang4;
                case 4:
                    return organizationNameLang5;
                default:
                    return organizationNameLang1;
            }
        }
        return organizationNameLang1;
    }

    public Boolean getIs_internal() {
        return is_internal;
    }

    public void setIs_internal(Boolean is_internal) {
        this.is_internal = is_internal;
    }

    public String getOrganizationNameLang5() {
        return organizationNameLang5;
    }

    public void setOrganizationNameLang5(String organizationNameLang5) {
        this.organizationNameLang5 = organizationNameLang5;
    }

    public String getOrganizationNameLang4() {
        return organizationNameLang4;
    }

    public void setOrganizationNameLang4(String organizationNameLang4) {
        this.organizationNameLang4 = organizationNameLang4;
    }

    public String getOrganizationNameLang3() {
        return organizationNameLang3;
    }

    public void setOrganizationNameLang3(String organizationNameLang3) {
        this.organizationNameLang3 = organizationNameLang3;
    }

    public String getOrganizationNameLang2() {
        return organizationNameLang2;
    }

    public void setOrganizationNameLang2(String organizationNameLang2) {
        this.organizationNameLang2 = organizationNameLang2;
    }

    public String getOrganizationNameLang1() {
        return organizationNameLang1;
    }

    public void setOrganizationNameLang1(String organizationNameLang1) {
        this.organizationNameLang1 = organizationNameLang1;
    }


    public OrganizationExt getRelevantOrganization() {
        return relevantOrganization;
    }

    public void setRelevantOrganization(OrganizationExt relevantOrganization) {
        this.relevantOrganization = relevantOrganization;
    }

    public void setAnalytics(OrgAnalytics analytics) {
        this.analytics = analytics;
    }

    public OrgAnalytics getAnalytics() {
        return analytics;
    }

    public void setPosition(List<PositionExt> position) {
        this.position = position;
    }

    public List<PositionExt> getPosition() {
        return position;
    }

    public List<OrganizationGroupGoalLink> getGoals() {
        return goals;
    }

    public void setGoals(List<OrganizationGroupGoalLink> goals) {
        this.goals = goals;
    }

    public void setPerformancePlans(List<PerformancePlan> performancePlans) {
        this.performancePlans = performancePlans;
    }

    public List<PerformancePlan> getPerformancePlans() {
        return performancePlans;
    }

    public void setCases(List<Case> cases) {
        this.cases = cases;
    }

    public List<Case> getCases() {
        return cases;
    }

    public void setHrUsers(List<OrganizationHrUser> hrUsers) {
        this.hrUsers = hrUsers;
    }

    public List<OrganizationHrUser> getHrUsers() {
        return hrUsers;
    }

    public void setCompetenceElements(List<CompetenceElement> competenceElements) {
        this.competenceElements = competenceElements;
    }

    public List<CompetenceElement> getCompetenceElements() {
        return competenceElements;
    }

    public void setList(List<OrganizationExt> list) {
        this.list = list;
    }

    public List<OrganizationExt> getList() {
        return list;
    }

    public void setOrganization(OrganizationExt organization) {
        this.organization = organization;
    }

    public OrganizationExt getOrganization() {
        return organization != null ? organization : getOrganizationInDate(BaseCommonUtils.getSystemDate());
    }

    public OrganizationExt getOrganizationInDate(Date date) {
        if (PersistenceHelper.isLoaded(this, "list") && list != null && !list.isEmpty()) {
            list.forEach(organizationExt -> {
                if (organizationExt.getDeleteTs() == null
                        && (organization == null
                        || !organizationExt.getStartDate().after(date)
                        && !organizationExt.getEndDate().before(date))) {
                    organization = organizationExt;
                }
            });
        }
        return organization;
    }

    // ?????? ?????????????????????? ?????????? ???????????? id ?? lookup ?? ???????????????? #FelixKamalov
//    @MetaProperty(related = "list")
//    @Transient
//    public String getOrganizationName() {
//        OrganizationExt organizationWithName = getOrganization();
//        if (organizationWithName == null) {
//            if (PersistenceHelper.isLoaded(this, "list") && list != null && !list.isEmpty()) {
//                for (OrganizationExt organizationExt : list) {
//                    if (organizationWithName == null || organizationWithName.getStartDate().before(organizationExt.getStartDate())) {
//                        organizationWithName = organizationExt;
//                    }
//                }
//            }
//        }
//        return organizationWithName == null ? "" : organizationWithName.getOrganizationName();
//    }

}