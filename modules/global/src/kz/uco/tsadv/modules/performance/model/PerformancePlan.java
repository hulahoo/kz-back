package kz.uco.tsadv.modules.performance.model;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.group.JobGroup;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@NamePattern("%s|performancePlanName")
@Table(name = "TSADV_PERFORMANCE_PLAN")
@Entity(name = "tsadv$PerformancePlan")
public class PerformancePlan extends AbstractParentEntity {
    private static final long serialVersionUID = 2220871965130779891L;

    @Column(name = "PERFORMANCE_PLAN_NAME", nullable = false, length = 240)
    protected String performancePlanName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PREVIOUS_PLAN_ID")
    protected kz.uco.tsadv.modules.performance.model.PerformancePlan previousPlan;

    @Column(name = "DESCRIPTION", length = 2000)
    protected String description;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ADMINISTRATOR_PERSON_GROUP_ID")
    protected PersonGroupExt administratorPersonGroup;

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE", nullable = false)
    protected Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE", nullable = false)
    protected Date endDate;


    @JoinTable(name = "TSADV_PERFORMANCE_PLAN_ORGANIZATION_GROUP_LINK",
            joinColumns = @JoinColumn(name = "PERFORMANCE_PLAN_ID"),
            inverseJoinColumns = @JoinColumn(name = "ORGANIZATION_GROUP_ID"))
    @ManyToMany
    protected List<OrganizationGroupExt> organizations;

    @JoinTable(name = "TSADV_PERFORMANCE_PLAN_POSITION_GROUP_LINK",
            joinColumns = @JoinColumn(name = "PERFORMANCE_PLAN_ID"),
            inverseJoinColumns = @JoinColumn(name = "POSITION_GROUP_ID"))
    @ManyToMany
    protected List<PositionGroupExt> positions;

    @JoinTable(name = "TSADV_PERFORMANCE_PLAN_JOB_GROUP_LINK",
            joinColumns = @JoinColumn(name = "PERFORMANCE_PLAN_ID"),
            inverseJoinColumns = @JoinColumn(name = "JOB_GROUP_ID"))
    @ManyToMany
    protected List<JobGroup> jobs;

    @Temporal(TemporalType.DATE)
    @Column(name = "ACCESSIBILITY_START_DATE")
    protected Date accessibilityStartDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "ACCESSIBILITY_END_DATE")
    protected Date accessibilityEndDate;

    @Column(name = "PERFORMANCE_PLAN_NAME_KZ")
    protected String performancePlanNameKz;

    @Column(name = "PERFORMANCE_PLAN_NAME_EN")
    protected String performancePlanNameEn;

    public String getPerformancePlanNameEn() {
        return performancePlanNameEn;
    }

    public void setPerformancePlanNameEn(String performancePlanNameEn) {
        this.performancePlanNameEn = performancePlanNameEn;
    }

    public String getPerformancePlanNameKz() {
        return performancePlanNameKz;
    }

    public void setPerformancePlanNameKz(String performancePlanNameKz) {
        this.performancePlanNameKz = performancePlanNameKz;
    }

    public void setAccessibilityStartDate(Date accessibilityStartDate) {
        this.accessibilityStartDate = accessibilityStartDate;
    }

    public Date getAccessibilityStartDate() {
        return accessibilityStartDate;
    }

    public void setAccessibilityEndDate(Date accessibilityEndDate) {
        this.accessibilityEndDate = accessibilityEndDate;
    }

    public Date getAccessibilityEndDate() {
        return accessibilityEndDate;
    }


    public void setJobs(List<JobGroup> jobs) {
        this.jobs = jobs;
    }

    public List<JobGroup> getJobs() {
        return jobs;
    }


    public void setPositions(List<PositionGroupExt> positions) {
        this.positions = positions;
    }

    public List<PositionGroupExt> getPositions() {
        return positions;
    }


    public void setOrganizations(List<OrganizationGroupExt> organizations) {
        this.organizations = organizations;
    }

    public List<OrganizationGroupExt> getOrganizations() {
        return organizations;
    }


    public void setAdministratorPersonGroup(PersonGroupExt administratorPersonGroup) {
        this.administratorPersonGroup = administratorPersonGroup;
    }

    public PersonGroupExt getAdministratorPersonGroup() {
        return administratorPersonGroup;
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


    public void setPreviousPlan(kz.uco.tsadv.modules.performance.model.PerformancePlan previousPlan) {
        this.previousPlan = previousPlan;
    }

    public kz.uco.tsadv.modules.performance.model.PerformancePlan getPreviousPlan() {
        return previousPlan;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }


    public void setPerformancePlanName(String performancePlanName) {
        this.performancePlanName = performancePlanName;
    }

    public String getPerformancePlanName() {
        return performancePlanName;
    }


}