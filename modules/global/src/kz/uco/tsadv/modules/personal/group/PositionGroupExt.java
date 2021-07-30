package kz.uco.tsadv.modules.personal.group;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Extends;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.core.global.PersistenceHelper;
import kz.uco.base.common.BaseCommonUtils;
import kz.uco.base.entity.abstraction.IEntityGroup;
import kz.uco.base.entity.dictionary.DicCompany;
import kz.uco.base.entity.shared.PositionGroup;
import kz.uco.tsadv.modules.hr.JobDescription;
import kz.uco.tsadv.modules.performance.model.PerformancePlan;
import kz.uco.tsadv.modules.personal.dictionary.DicHrRole;
import kz.uco.tsadv.modules.personal.model.*;
import kz.uco.tsadv.modules.timesheet.model.OrgAnalytics;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@NamePattern("%s|position")
@Extends(PositionGroup.class)
@Entity(name = "base$PositionGroupExt")
public class PositionGroupExt extends PositionGroup implements IEntityGroup<PositionExt> {
    private static final long serialVersionUID = -8784610299705796841L;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "group")
    protected List<PositionExt> list;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_GROUP_ID")
    protected OrganizationGroupExt organizationGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JOB_GROUP_ID")
    protected JobGroup jobGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GRADE_GROUP_ID")
    protected GradeGroup gradeGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID")
    protected DicCompany company;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "positionGroup")
    protected List<VacationConditions> vacationConditionsList;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ANALYTICS_ID")
    protected OrgAnalytics analytics;

    @Transient
    @MetaProperty(related = "list")
    protected PositionExt position;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "positionGroup")
    protected List<AssignmentExt> assignments;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "positionGroup")
    protected List<CompetenceElement> competenceElements;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "positionGroup")
    protected List<Case> cases;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "positionGroup")
    protected List<SurCharge> surCharge;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "positionGroupName")
    protected List<FlightTimeRate> flightTimeRate;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "positionGroupId")
    protected List<FlySurCharge> flySurCharge;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "positionGroup")
    protected List<PositionGroupGoalLink> goals;

    @JoinTable(name = "TSADV_PERFORMANCE_PLAN_POSITION_GROUP_LINK",
            joinColumns = @JoinColumn(name = "POSITION_GROUP_ID"),
            inverseJoinColumns = @JoinColumn(name = "PERFORMANCE_PLAN_ID"))
    @ManyToMany
    protected List<PerformancePlan> performancePlans;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ADMIN_APPROVE_ID")
    protected DicHrRole adminApprove;

    @OnDelete(DeletePolicy.CASCADE)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "positionGroup")
    private JobDescription jobDescription;

    public JobDescription getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(JobDescription jobDescription) {
        this.jobDescription = jobDescription;
    }

    public DicCompany getCompany() {
        return company;
    }

    public void setCompany(DicCompany company) {
        this.company = company;
    }

    public GradeGroup getGradeGroup() {
        return gradeGroup;
    }

    public void setGradeGroup(GradeGroup gradeGroup) {
        this.gradeGroup = gradeGroup;
    }

    public JobGroup getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(JobGroup jobGroup) {
        this.jobGroup = jobGroup;
    }

    public OrganizationGroupExt getOrganizationGroup() {
        return organizationGroup;
    }

    public void setOrganizationGroup(OrganizationGroupExt organizationGroup) {
        this.organizationGroup = organizationGroup;
    }

    public DicHrRole getAdminApprove() {
        return adminApprove;
    }

    public void setAdminApprove(DicHrRole adminApprove) {
        this.adminApprove = adminApprove;
    }

    public List<VacationConditions> getVacationConditionsList() {
        return vacationConditionsList;
    }

    public void setVacationConditionsList(List<VacationConditions> vacationConditionsList) {
        this.vacationConditionsList = vacationConditionsList;
    }

    public void setAnalytics(OrgAnalytics analytics) {
        this.analytics = analytics;
    }

    public OrgAnalytics getAnalytics() {
        return analytics;
    }

    public List<AssignmentExt> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<AssignmentExt> assignments) {
        this.assignments = assignments;
    }

    public void setFlightTimeRate(List<FlightTimeRate> flightTimeRate) {
        this.flightTimeRate = flightTimeRate;
    }

    public List<FlightTimeRate> getFlightTimeRate() {
        return flightTimeRate;
    }

    public void setFlySurCharge(List<FlySurCharge> flySurCharge) {
        this.flySurCharge = flySurCharge;
    }

    public List<FlySurCharge> getFlySurCharge() {
        return flySurCharge;
    }

    public List<PositionGroupGoalLink> getGoals() {
        return goals;
    }

    public void setGoals(List<PositionGroupGoalLink> goals) {
        this.goals = goals;
    }

    public void setPerformancePlans(List<PerformancePlan> performancePlans) {
        this.performancePlans = performancePlans;
    }

    public List<PerformancePlan> getPerformancePlans() {
        return performancePlans;
    }

    public void setSurCharge(List<SurCharge> surCharge) {
        this.surCharge = surCharge;
    }

    public List<SurCharge> getSurCharge() {
        return surCharge;
    }

    public void setCases(List<Case> cases) {
        this.cases = cases;
    }

    public List<Case> getCases() {
        return cases;
    }

    public void setCompetenceElements(List<CompetenceElement> competenceElements) {
        this.competenceElements = competenceElements;
    }

    public List<CompetenceElement> getCompetenceElements() {
        return competenceElements;
    }

    public void setList(List<PositionExt> list) {
        this.list = list;
    }

    public List<PositionExt> getList() {
        return list;
    }

    public void setPosition(PositionExt position) {
        this.position = position;
    }

    public PositionExt getPosition() {
        return position != null ? position : getPositionInDate(BaseCommonUtils.getSystemDate());
    }

    public PositionExt getPositionInDate(Date date) {
        if (PersistenceHelper.isLoaded(this, "list") && list != null && !list.isEmpty()) {
            list.stream()
                    .filter(p -> p.getDeleteTs() == null
                            && !date.before(p.getStartDate())
                            && !date.after(p.getEndDate()))
                    .findFirst()
                    .ifPresent(p -> position = p);
        }
        return position;
    }

    // Для отображения имени вместо id в lookup и таблицах #Timur Tashmatov
    @MetaProperty(related = "list")
    @Transient
    public String getPositionName() {
        PositionExt positionExtWithName = getPosition();
        if (positionExtWithName == null) {
            if (PersistenceHelper.isLoaded(this, "list") && list != null && !list.isEmpty()) {
                for (PositionExt positionExt : list) {
                    if (positionExtWithName == null || positionExt.getStartDate().after(positionExtWithName.getStartDate())) {
                        positionExtWithName = positionExt;
                    }
                }

            }
        }
        return positionExtWithName == null ? "" : positionExtWithName.getPositionName();
    }

    @MetaProperty(related = "list")
    @Transient
    public String getFullName() {
        PositionExt positionExtWithName = getPosition();
        if (positionExtWithName == null) {
            if (PersistenceHelper.isLoaded(this, "list") && list != null && !list.isEmpty()) {
                for (PositionExt positionExt : list) {
                    if (positionExtWithName == null || positionExt.getStartDate().after(positionExtWithName.getStartDate())) {
                        positionExtWithName = positionExt;
                    }
                }

            }
        }
        return positionExtWithName == null ? "" : positionExtWithName.getPositionFullName();
    }
}