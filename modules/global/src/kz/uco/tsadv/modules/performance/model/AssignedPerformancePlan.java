package kz.uco.tsadv.modules.performance.model;

import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;

@Table(name = "TSADV_ASSIGNED_PERFORMANCE_PLAN")
@Entity(name = "tsadv$AssignedPerformancePlan")
public class AssignedPerformancePlan extends StandardEntity {
    private static final long serialVersionUID = -5439003925414989525L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERFORMANCE_PLAN_ID")
    protected PerformancePlan performancePlan;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ASSIGNED_PERSON_ID")
    protected PersonGroupExt assignedPerson;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ASSIGNED_BY_ID")
    protected PersonGroupExt assigned_by;

    public void setPerformancePlan(PerformancePlan performancePlan) {
        this.performancePlan = performancePlan;
    }

    public PerformancePlan getPerformancePlan() {
        return performancePlan;
    }

    public void setAssignedPerson(PersonGroupExt assignedPerson) {
        this.assignedPerson = assignedPerson;
    }

    public PersonGroupExt getAssignedPerson() {
        return assignedPerson;
    }

    public void setAssigned_by(PersonGroupExt assigned_by) {
        this.assigned_by = assigned_by;
    }

    public PersonGroupExt getAssigned_by() {
        return assigned_by;
    }


}