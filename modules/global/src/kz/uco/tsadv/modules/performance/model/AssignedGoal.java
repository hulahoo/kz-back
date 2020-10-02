package kz.uco.tsadv.modules.performance.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import kz.uco.tsadv.modules.performance.dictionary.DicPriority;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.group.JobGroup;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@NamePattern("%s|goal")
@Table(name = "TSADV_ASSIGNED_GOAL", uniqueConstraints = {
    @UniqueConstraint(name = "IDX_TSADV_ASSIGNED_GOAL_UNQ", columnNames = {"PERSON_GROUP_ID", "GOAL_ID", "PERFORMANCE_PLAN_ID"})
})
@Entity(name = "tsadv$AssignedGoal")
public class AssignedGoal extends AbstractParentEntity {
    private static final long serialVersionUID = -516917084182343541L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "GOAL_ID")
    protected Goal goal;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_GROUP_ID")
    protected OrganizationGroupExt organizationGroup;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POSITION_GROUP_ID")
    protected PositionGroupExt positionGroup;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JOB_GROUP_ID")
    protected JobGroup jobGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_GOAL_ID")
    protected Goal parentGoal;

    @NotNull
    @Column(name = "TARGET_VALUE", nullable = false)
    protected Integer targetValue;

    @Column(name = "ACTUAL_VALUE", nullable = false)
    protected Integer actualValue;

    @Column(name = "SUCCESS_CRITETIA", length = 2000)
    protected String successCritetia;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ASSIGNED_BY_PERSON_GROUP_ID")
    protected PersonGroupExt assignedByPersonGroup;

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE", nullable = false)
    protected Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE", nullable = false)
    protected Date endDate;

    @Column(name = "WEIGHT")
    protected Integer weight;

    @Lookup(type = LookupType.DROPDOWN)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRIORITY_ID")
    protected DicPriority priority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERFORMANCE_PLAN_ID")
    protected PerformancePlan performancePlan;

    public Goal getParentGoal() {
        return parentGoal;
    }

    public void setParentGoal(Goal parentGoal) {
        this.parentGoal = parentGoal;
    }


    public void setPerformancePlan(PerformancePlan performancePlan) {
        this.performancePlan = performancePlan;
    }

    public PerformancePlan getPerformancePlan() {
        return performancePlan;
    }


    public void setPriority(DicPriority priority) {
        this.priority = priority;
    }

    public DicPriority getPriority() {
        return priority;
    }


    public void setAssignedByPersonGroup(PersonGroupExt assignedByPersonGroup) {
        this.assignedByPersonGroup = assignedByPersonGroup;
    }

    public PersonGroupExt getAssignedByPersonGroup() {
        return assignedByPersonGroup;
    }


    public void setJobGroup(JobGroup jobGroup) {
        this.jobGroup = jobGroup;
    }

    public JobGroup getJobGroup() {
        return jobGroup;
    }


    public void setPositionGroup(PositionGroupExt positionGroup) {
        this.positionGroup = positionGroup;
    }

    public PositionGroupExt getPositionGroup() {
        return positionGroup;
    }


    public void setOrganizationGroup(OrganizationGroupExt organizationGroup) {
        this.organizationGroup = organizationGroup;
    }

    public OrganizationGroupExt getOrganizationGroup() {
        return organizationGroup;
    }


    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }


    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getWeight() {
        return weight;
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


    public void setTargetValue(Integer targetValue) {
        this.targetValue = targetValue;
    }

    public Integer getTargetValue() {
        return targetValue;
    }

    public void setActualValue(Integer actualValue) {
        this.actualValue = actualValue;
    }

    public Integer getActualValue() {
        return actualValue;
    }

    public void setSuccessCritetia(String successCritetia) {
        this.successCritetia = successCritetia;
    }

    public String getSuccessCritetia() {
        return successCritetia;
    }


    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    public Goal getGoal() {
        return goal;
    }


}