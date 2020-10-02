package kz.uco.tsadv.modules.personal.model;

import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.tsadv.modules.performance.model.Goal;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;

import javax.persistence.*;

@Table(name = "TSADV_POSITION_GROUP_GOAL_LINK")
@Entity(name = "tsadv$PositionGroupGoalLink")
public class PositionGroupGoalLink extends AbstractParentEntity {
    private static final long serialVersionUID = -7117415888685606564L;

    @Column(name = "WEIGHT")
    protected Integer weight;

    @Column(name = "TARGET_VALUE")
    protected Integer targetValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POSITION_GROUP_ID")
    protected PositionGroupExt positionGroup;

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


    public void setPositionGroup(PositionGroupExt positionGroup) {
        this.positionGroup = positionGroup;
    }

    public PositionGroupExt getPositionGroup() {
        return positionGroup;
    }


    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getWeight() {
        return weight;
    }


}