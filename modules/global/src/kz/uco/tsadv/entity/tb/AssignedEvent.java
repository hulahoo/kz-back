package kz.uco.tsadv.entity.tb;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import kz.uco.tsadv.entity.tb.dictionary.EventStatus;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;

@Table(name = "TSADV_ASSIGNED_EVENT")
@Entity(name = "tsadv$AssignedEvent")
public class AssignedEvent extends AbstractParentEntity {
    private static final long serialVersionUID = -8953526615927572934L;

    @Column(name = "FACT")
    protected Long fact;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_EVENT_ID")
    protected AssignedEvent parentEvent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SAFETY_EVENT_ID")
    protected SafetyEvent safetyEvent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ASSIGNMENT_ID")
    protected PersonGroupExt assignment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ASSIGNED_ID")
    protected PersonGroupExt assigned;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "STATUS_ID")
    protected EventStatus status;

    @Temporal(TemporalType.DATE)
    @Column(name = "DEADLINE", nullable = false)
    protected Date deadline;

    @Column(name = "INVESTMENT_PLAN")
    protected Long investmentPlan;

    @Column(name = "BUDGET_PLAN")
    protected Long budgetPlan;

    @Column(name = "INVESTMENT_FACT")
    protected Long investmentFact;

    @Column(name = "BUDGET_FACT")
    protected Long budgetFact;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SAFETY_PLAN_EVENT_ID")
    protected SafetyPlanEvent safetyPlanEvent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_ID")
    protected OrganizationGroupExt organization;

    public void setOrganization(OrganizationGroupExt organization) {
        this.organization = organization;
    }

    public OrganizationGroupExt getOrganization() {
        return organization;
    }


    public AssignedEvent getParentEvent() {
        return parentEvent;
    }

    public void setParentEvent(AssignedEvent parentEvent) {
        this.parentEvent = parentEvent;
    }



    public PersonGroupExt getAssignment() {
        return assignment;
    }

    public void setAssignment(PersonGroupExt assignment) {
        this.assignment = assignment;
    }


    public PersonGroupExt getAssigned() {
        return assigned;
    }

    public void setAssigned(PersonGroupExt assigned) {
        this.assigned = assigned;
    }



    public void setSafetyEvent(SafetyEvent safetyEvent) {
        this.safetyEvent = safetyEvent;
    }

    public SafetyEvent getSafetyEvent() {
        return safetyEvent;
    }



    public void setSafetyPlanEvent(SafetyPlanEvent safetyPlanEvent) {
        this.safetyPlanEvent = safetyPlanEvent;
    }

    public SafetyPlanEvent getSafetyPlanEvent() {
        return safetyPlanEvent;
    }


    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }


    public void setFact(Long fact) {
        this.fact = fact;
    }

    public Long getFact() {
        return fact;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setInvestmentPlan(Long investmentPlan) {
        this.investmentPlan = investmentPlan;
    }

    public Long getInvestmentPlan() {
        return investmentPlan;
    }

    public void setBudgetPlan(Long budgetPlan) {
        this.budgetPlan = budgetPlan;
    }

    public Long getBudgetPlan() {
        return budgetPlan;
    }

    public void setInvestmentFact(Long investmentFact) {
        this.investmentFact = investmentFact;
    }

    public Long getInvestmentFact() {
        return investmentFact;
    }

    public void setBudgetFact(Long budgetFact) {
        this.budgetFact = budgetFact;
    }

    public Long getBudgetFact() {
        return budgetFact;
    }


}