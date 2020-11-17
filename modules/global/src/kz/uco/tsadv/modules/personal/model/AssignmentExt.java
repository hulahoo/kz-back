package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.*;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.abstraction.IGroupedEntity;
import kz.uco.base.entity.shared.Assignment;
import kz.uco.tsadv.modules.personal.dictionary.DicAssignmentCategory;
import kz.uco.tsadv.modules.personal.dictionary.DicAssignmentStatus;
import kz.uco.tsadv.modules.personal.dictionary.DicCostCenter;
import kz.uco.tsadv.modules.personal.group.*;
import kz.uco.tsadv.modules.recruitment.enums.HS_Periods;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Listeners("tsadv_AssignmentExtListener")
@NamePattern("%s %s %s %s|id,organizationGroup,jobGroup,group")
@Extends(Assignment.class)
@Entity(name = "base$AssignmentExt")
public class AssignmentExt extends Assignment implements IGroupedEntity<AssignmentGroupExt> {
    private static final long serialVersionUID = -6247874875200340151L;

    @OnDeleteInverse(DeletePolicy.DENY)
    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "open", "clear"})
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUBSTITUTE_EMPLOYEE_ID")
    protected PersonGroupExt substituteEmployee;

    @Column(name = "ORDER_NUMBER")
    protected String orderNumber;

    @Temporal(TemporalType.DATE)
    @Column(name = "ORDER_DATE")
    protected Date orderDate;

    @Column(name = "DURATION_PROBATION_PERIOD")
    protected Integer durationProbationPeriod;

    @Column(name = "UNIT")
    protected Integer unit;

    @Temporal(TemporalType.DATE)
    @Column(name = "PROBATION_END_DATE")
    protected Date probationEndDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ASSIGNMENT_STATUS_ID")
    protected DicAssignmentStatus assignmentStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ASSIGNMENT_CATEGORY_ID")
    protected DicAssignmentCategory assignmentCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COST_CENTER_ID")
    protected DicCostCenter costCenter;

    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "open", "clear"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JOB_GROUP_ID")
    protected JobGroup jobGroup;

    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "open", "clear"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GRADE_GROUP_ID")
    protected GradeGroup gradeGroup;

    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "open", "clear"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_GROUP_ID")
    protected OrganizationGroupExt organizationGroup;

    @Column(name = "PRIMARY_FLAG")
    protected Boolean primaryFlag = false;

    @Column(name = "FTE")
    protected Double fte;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID")
    protected AssignmentGroupExt group;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POSITION_GROUP_ID")
    protected PositionGroupExt positionGroup;

    @Transient
    @MetaProperty
    protected List<AssignmentExt> children;

    @Temporal(TemporalType.DATE)
    @Column(name = "TEMPORARY_END_DATE")
    protected Date temporaryEndDate;

    public void setTemporaryEndDate(Date temporaryEndDate) {
        this.temporaryEndDate = temporaryEndDate;
    }

    public Date getTemporaryEndDate() {
        return temporaryEndDate;
    }

    public DicAssignmentCategory getAssignmentCategory() {
        return assignmentCategory;
    }

    public void setAssignmentCategory(DicAssignmentCategory assignmentCategory) {
        this.assignmentCategory = assignmentCategory;
    }

    public Double getFte() {
        return fte;
    }

    public void setFte(Double fte) {
        this.fte = fte;
    }

    public void setCostCenter(DicCostCenter costCenter) {
        this.costCenter = costCenter;
    }

    public DicCostCenter getCostCenter() {
        return costCenter;
    }

    public void setSubstituteEmployee(PersonGroupExt substituteEmployee) {
        this.substituteEmployee = substituteEmployee;
    }

    public PersonGroupExt getSubstituteEmployee() {
        return substituteEmployee;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setDurationProbationPeriod(Integer durationProbationPeriod) {
        this.durationProbationPeriod = durationProbationPeriod;
    }

    public Integer getDurationProbationPeriod() {
        return durationProbationPeriod;
    }

    public void setUnit(HS_Periods unit) {
        this.unit = unit == null ? null : unit.getId();
    }

    public HS_Periods getUnit() {
        return unit == null ? null : HS_Periods.fromId(unit);
    }

    public void setProbationEndDate(Date probationEndDate) {
        this.probationEndDate = probationEndDate;
    }

    public Date getProbationEndDate() {
        return probationEndDate;
    }

    public void setAssignmentStatus(DicAssignmentStatus assignmentStatus) {
        this.assignmentStatus = assignmentStatus;
    }

    public DicAssignmentStatus getAssignmentStatus() {
        return assignmentStatus;
    }

    public List<AssignmentExt> getChildren() {
        return children;
    }

    public void setChildren(List<AssignmentExt> children) {
        this.children = children;
    }

    public void setPositionGroup(PositionGroupExt positionGroup) {
        this.positionGroup = positionGroup;
    }

    public PositionGroupExt getPositionGroup() {
        return positionGroup;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setJobGroup(JobGroup jobGroup) {
        this.jobGroup = jobGroup;
    }

    public JobGroup getJobGroup() {
        return jobGroup;
    }

    public void setGradeGroup(GradeGroup gradeGroup) {
        this.gradeGroup = gradeGroup;
    }

    public GradeGroup getGradeGroup() {
        return gradeGroup;
    }

    public void setOrganizationGroup(OrganizationGroupExt organizationGroup) {
        this.organizationGroup = organizationGroup;
    }

    public OrganizationGroupExt getOrganizationGroup() {
        return organizationGroup;
    }

    public void setGroup(AssignmentGroupExt group) {
        this.group = group;
    }

    public AssignmentGroupExt getGroup() {
        return group;
    }

    public void setPrimaryFlag(Boolean primaryFlag) {
        this.primaryFlag = primaryFlag;
    }

    public Boolean getPrimaryFlag() {
        return primaryFlag;
    }

}