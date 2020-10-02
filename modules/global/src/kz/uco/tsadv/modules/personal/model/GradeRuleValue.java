package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.tsadv.modules.personal.model.*;
import kz.uco.tsadv.modules.personal.model.GradeRule;
import kz.uco.base.entity.abstraction.AbstractTimeBasedEntity;
import kz.uco.tsadv.modules.personal.group.GradeGroup;
import kz.uco.tsadv.modules.personal.group.GradeRuleValueGroup;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@NamePattern(" %s %s %s %s|max,mid,min,value")
@Table(name = "TSADV_GRADE_RULE_VALUE")
@Entity(name = "tsadv$GradeRuleValue")
public class GradeRuleValue extends AbstractTimeBasedEntity {
    private static final long serialVersionUID = -9024440342051976704L;

    @Column(name = "VALUE_")
    protected Double value;

    @Column(name = "MIN_")
    protected Double min;

    @Column(name = "MID")
    protected Double mid;

    @Column(name = "MAX_")
    protected Double max;


    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "GRADE_GROUP_ID")
    protected GradeGroup gradeGroup;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "GROUP_ID")
    protected GradeRuleValueGroup group;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "GRADE_RULE_ID")
    protected GradeRule gradeRule;

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }


    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }


    public Double getMid() {
        return mid;
    }

    public void setMid(Double mid) {
        this.mid = mid;
    }


    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    public GradeRule getGradeRule() {
        return gradeRule;
    }

    public void setGradeRule(GradeRule gradeRule) {
        this.gradeRule = gradeRule;
    }






    public void setGradeGroup(GradeGroup gradeGroup) {
        this.gradeGroup = gradeGroup;
    }

    public GradeGroup getGradeGroup() {
        return gradeGroup;
    }


    public void setGroup(GradeRuleValueGroup group) {
        this.group = group;
    }

    public GradeRuleValueGroup getGroup() {
        return group;
    }










}