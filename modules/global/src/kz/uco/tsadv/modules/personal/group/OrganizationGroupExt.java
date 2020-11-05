package kz.uco.tsadv.modules.personal.group;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Extends;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.core.global.PersistenceHelper;
import kz.uco.base.common.BaseCommonUtils;
import kz.uco.base.entity.shared.OrganizationGroup;
import kz.uco.tsadv.modules.performance.model.PerformancePlan;
import kz.uco.tsadv.modules.personal.model.*;
import kz.uco.tsadv.modules.timesheet.model.OrgAnalytics;
import org.eclipse.persistence.annotations.Customizer;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@NamePattern("%s|organizationName")
@Extends(OrganizationGroup.class)
@Entity(name = "base$OrganizationGroupExt")
@Customizer(OrganizationGroupExtDescriptorCustomizer.class)
public class OrganizationGroupExt extends OrganizationGroup {
    private static final long serialVersionUID = -5913796466452223229L;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "group")
    protected List<OrganizationExt> list;

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
    protected OrganizationExt relevantOrganization; // Текущее (для момента в машине времени) подразделение


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

    // Для отображения имени вместо id в lookup и таблицах #FelixKamalov
    @MetaProperty(related = "list")
    @Transient
    public String getOrganizationName() {
        OrganizationExt organizationWithName = getOrganization();
        if (organizationWithName == null) {
            if (PersistenceHelper.isLoaded(this, "list") && list != null && !list.isEmpty()) {
                for (OrganizationExt organizationExt : list) {
                    if (organizationWithName == null || organizationWithName.getStartDate().before(organizationExt.getStartDate())) {
                        organizationWithName = organizationExt;
                    }
                }
            }
        }
        return organizationWithName == null ? "" : organizationWithName.getOrganizationName();
    }

}