package kz.uco.tsadv.modules.performance.model;

import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.performance.dictionary.DicPerformanceStage;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;

import javax.persistence.*;

@Table(name = "TSADV_ASSIGNED_PERFORMANCE_PLAN_HISTORY")
@Entity(name = "tsadv_AssignedPerformancePlanHistory")
public class AssignedPerformancePlanHistory extends AbstractParentEntity {
    private static final long serialVersionUID = 4006441031565002935L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STAGE_ID")
    protected DicPerformanceStage stage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STATUS_ID")
    protected DicRequestStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ASSIGNED_PERFORMANCE_PLAN_ID")
    protected AssignedPerformancePlan assignedPerformancePlan;

    public AssignedPerformancePlan getAssignedPerformancePlan() {
        return assignedPerformancePlan;
    }

    public void setAssignedPerformancePlan(AssignedPerformancePlan assignedPerformancePlan) {
        this.assignedPerformancePlan = assignedPerformancePlan;
    }

    public DicRequestStatus getStatus() {
        return status;
    }

    public void setStatus(DicRequestStatus status) {
        this.status = status;
    }

    public DicPerformanceStage getStage() {
        return stage;
    }

    public void setStage(DicPerformanceStage stage) {
        this.stage = stage;
    }
}