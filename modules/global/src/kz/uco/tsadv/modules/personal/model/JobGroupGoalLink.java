package kz.uco.tsadv.modules.personal.model;

import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.tsadv.modules.performance.model.Goal;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.group.JobGroup;

import javax.persistence.*;

@Table(name = "TSADV_JOB_GROUP_GOAL_LINK")
@Entity(name = "tsadv$JobGroupGoalLink")
public class JobGroupGoalLink extends AbstractParentEntity {
    private static final long serialVersionUID = 4121150543243376965L;

    @Column(name = "WEIGHT")
    protected Integer weight;

    @Column(name = "TARGET_VALUE")
    protected Integer targetValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JOB_GROUP_ID")
    protected JobGroup jobGroup;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GOAL_ID")
    protected Goal goal;

    public void setTargetValue(Integer targetValue) {
        this.targetValue = targetValue;
    }

    public Integer getTargetValue() {
        return targetValue;
    }


    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    public Goal getGoal() {
        return goal;
    }


    public void setJobGroup(JobGroup jobGroup) {
        this.jobGroup = jobGroup;
    }

    public JobGroup getJobGroup() {
        return jobGroup;
    }


    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getWeight() {
        return weight;
    }


}