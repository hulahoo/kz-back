package kz.uco.tsadv.modules.performance.model;

import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.*;

@Table(name = "TSADV_INSTRUCTIONS_KPI")
@Entity(name = "tsadv_InstructionsKpi")
public class InstructionsKpi extends AbstractParentEntity {
    private static final long serialVersionUID = -7954699942312098634L;

    @Lob
    @Column(name = "INSTRUCTION")
    protected String instruction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERFORMANCE_PLAN_ID")
    protected PerformancePlan performancePlan;

    public PerformancePlan getPerformancePlan() {
        return performancePlan;
    }

    public void setPerformancePlan(PerformancePlan performancePlan) {
        this.performancePlan = performancePlan;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }
}