package kz.uco.tsadv.modules.performance.model;

import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.tsadv.modules.performance.enums.CardStatusEnum;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;

@Table(name = "TSADV_ASSIGNED_PERFORMANCE_PLAN")
@Entity(name = "tsadv$AssignedPerformancePlan")
public class AssignedPerformancePlan extends StandardEntity {
    private static final long serialVersionUID = -5439003925414989525L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERFORMANCE_PLAN_ID")
    protected PerformancePlan performancePlan;

    @Column(name = "RESULT")
    protected Integer result;

    @Column(name = "GZP")
    protected Integer gzp;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ASSIGNED_PERSON_ID")
    protected PersonGroupExt assignedPerson;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ASSIGNED_BY_ID")
    protected PersonGroupExt assigned_by;

    @Column(name = "STATUS")
    protected String status;

    public CardStatusEnum getStatus() {
        return status == null ? null : CardStatusEnum.fromId(status);
    }

    public void setStatus(CardStatusEnum status) {
        this.status = status == null ? null : status.getId();
    }

    public Integer getGzp() {
        return gzp;
    }

    public void setGzp(Integer gzp) {
        this.gzp = gzp;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

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