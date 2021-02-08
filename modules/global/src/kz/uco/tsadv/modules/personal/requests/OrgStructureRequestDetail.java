package kz.uco.tsadv.modules.personal.requests;

import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.tsadv.modules.personal.dictionary.DicChangeType;
import kz.uco.tsadv.modules.personal.group.GradeGroup;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.Grade;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Table(name = "TSADV_ORG_STRUCTURE_REQUEST_DETAIL")
@Entity(name = "tsadv_OrgStructureRequestDetail")
public class OrgStructureRequestDetail extends StandardEntity {
    private static final long serialVersionUID = -6122499357953929703L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORG_STRUCTURE_REQUEST_ID")
    protected OrgStructureRequest orgStructureRequest;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CHANGE_TYPE_ID")
    protected DicChangeType changeType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CURRENT_ORGANIZATION_GROUP_ID")
    protected OrganizationGroupExt currentOrganizationGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CURRENT_POSITION_GROUP_ID")
    protected PositionGroupExt currentPositionGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CURRENT_GRADE_GROUP_ID")
    protected GradeGroup currentGradeGroup;

    @Column(name = "CURRENT_HEAD_COUNT")
    protected Integer currentHeadCount;

    @Column(name = "CURRENT_BASE_SALARY")
    protected BigDecimal currentBaseSalary;

    @Column(name = "CURRENT_MONTHLY_PAYROLL")
    protected BigDecimal currentMonthlyPayroll;

    @Column(name = "CURRENT_MONTHLY_TOTAL_PAYROLL")
    protected BigDecimal currentMonthlyTotalPayroll;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ORGANIZATION_GROUP_ID")
    protected OrganizationGroupExt parentOrganizationGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_POSITION_GROUP_ID")
    protected PositionGroupExt parentPositionGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NEW_ORGANIZATION_ID")
    protected OrganizationGroupExt newOrganization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NEW_POSITION_GROUP_ID")
    protected PositionGroupExt newPositionGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NEW_GRADE_ID")
    protected Grade newGrade;

    @Column(name = "NEW_HEAD_COUNT")
    protected BigDecimal newHeadCount;

    @Column(name = "NEW_BASE_SALARY")
    protected BigDecimal newBaseSalary;

    @Column(name = "NEW_MONTHLY_PAYROLL")
    protected BigDecimal newMonthlyPayroll;

    @Column(name = "NEW_MONTHLY_TOTAL_PAYROLL")
    protected BigDecimal newMonthlyTotalPayroll;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DIFFERENCE_ORGANIZATION_GROUP_ID")
    protected OrganizationGroupExt differenceOrganizationGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DIFFERENCE_POSITION_GROUP_ID")
    protected PositionGroupExt differencePositionGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DIFFERENCE_GRADE_GROUP_ID")
    protected GradeGroup differenceGradeGroup;

    @Column(name = "DIFFERENCE_HEAD_COUNT")
    protected Integer differenceHeadCount;

    @Column(name = "DIFFERENCE_BASE_SALARY")
    protected BigDecimal differenceBaseSalary;

    @Column(name = "DIFFERENCE_MONTHLY_PAYROLL")
    protected BigDecimal differenceMonthlyPayroll;

    @Column(name = "DIFFERENCE_MONTHLY_TOTAL_PAYROLL")
    protected BigDecimal differenceMonthlyTotalPayroll;

    public void setChangeType(DicChangeType changeType) {
        this.changeType = changeType;
    }

    public DicChangeType getChangeType() {
        return changeType;
    }

    public OrgStructureRequest getOrgStructureRequest() {
        return orgStructureRequest;
    }

    public void setOrgStructureRequest(OrgStructureRequest orgStructureRequest) {
        this.orgStructureRequest = orgStructureRequest;
    }

    public BigDecimal getDifferenceMonthlyTotalPayroll() {
        return differenceMonthlyTotalPayroll;
    }

    public void setDifferenceMonthlyTotalPayroll(BigDecimal differenceMonthlyTotalPayroll) {
        this.differenceMonthlyTotalPayroll = differenceMonthlyTotalPayroll;
    }

    public BigDecimal getDifferenceMonthlyPayroll() {
        return differenceMonthlyPayroll;
    }

    public void setDifferenceMonthlyPayroll(BigDecimal differenceMonthlyPayroll) {
        this.differenceMonthlyPayroll = differenceMonthlyPayroll;
    }

    public BigDecimal getDifferenceBaseSalary() {
        return differenceBaseSalary;
    }

    public void setDifferenceBaseSalary(BigDecimal differenceBaseSalary) {
        this.differenceBaseSalary = differenceBaseSalary;
    }

    public Integer getDifferenceHeadCount() {
        return differenceHeadCount;
    }

    public void setDifferenceHeadCount(Integer differenceHeadCount) {
        this.differenceHeadCount = differenceHeadCount;
    }

    public GradeGroup getDifferenceGradeGroup() {
        return differenceGradeGroup;
    }

    public void setDifferenceGradeGroup(GradeGroup differenceGradeGroup) {
        this.differenceGradeGroup = differenceGradeGroup;
    }

    public PositionGroupExt getDifferencePositionGroup() {
        return differencePositionGroup;
    }

    public void setDifferencePositionGroup(PositionGroupExt differencePositionGroup) {
        this.differencePositionGroup = differencePositionGroup;
    }

    public OrganizationGroupExt getDifferenceOrganizationGroup() {
        return differenceOrganizationGroup;
    }

    public void setDifferenceOrganizationGroup(OrganizationGroupExt differenceOrganizationGroup) {
        this.differenceOrganizationGroup = differenceOrganizationGroup;
    }

    public BigDecimal getNewMonthlyTotalPayroll() {
        return newMonthlyTotalPayroll;
    }

    public void setNewMonthlyTotalPayroll(BigDecimal newMonthlyTotalPayroll) {
        this.newMonthlyTotalPayroll = newMonthlyTotalPayroll;
    }

    public BigDecimal getNewMonthlyPayroll() {
        return newMonthlyPayroll;
    }

    public void setNewMonthlyPayroll(BigDecimal newMonthlyPayroll) {
        this.newMonthlyPayroll = newMonthlyPayroll;
    }

    public BigDecimal getCurrentMonthlyPayrollCopy() {
        return newMonthlyPayroll;
    }

    public void setCurrentMonthlyPayrollCopy(BigDecimal currentMonthlyPayrollCopy) {
        this.newMonthlyPayroll = currentMonthlyPayrollCopy;
    }

    public BigDecimal getNewBaseSalary() {
        return newBaseSalary;
    }

    public void setNewBaseSalary(BigDecimal newBaseSalary) {
        this.newBaseSalary = newBaseSalary;
    }

    public BigDecimal getNewHeadCount() {
        return newHeadCount;
    }

    public void setNewHeadCount(BigDecimal newHeadCount) {
        this.newHeadCount = newHeadCount;
    }

    public Grade getNewGrade() {
        return newGrade;
    }

    public void setNewGrade(Grade newGrade) {
        this.newGrade = newGrade;
    }

    public PositionGroupExt getNewPositionGroup() {
        return newPositionGroup;
    }

    public void setNewPositionGroup(PositionGroupExt newPositionGroup) {
        this.newPositionGroup = newPositionGroup;
    }

    public OrganizationGroupExt getNewOrganization() {
        return newOrganization;
    }

    public void setNewOrganization(OrganizationGroupExt newOrganization) {
        this.newOrganization = newOrganization;
    }

    public PositionGroupExt getParentPositionGroup() {
        return parentPositionGroup;
    }

    public void setParentPositionGroup(PositionGroupExt parentPositionGroup) {
        this.parentPositionGroup = parentPositionGroup;
    }

    public OrganizationGroupExt getParentOrganizationGroup() {
        return parentOrganizationGroup;
    }

    public void setParentOrganizationGroup(OrganizationGroupExt parentOrganizationGroup) {
        this.parentOrganizationGroup = parentOrganizationGroup;
    }

    public BigDecimal getCurrentMonthlyTotalPayroll() {
        return currentMonthlyTotalPayroll;
    }

    public void setCurrentMonthlyTotalPayroll(BigDecimal currentMonthlyTotalPayroll) {
        this.currentMonthlyTotalPayroll = currentMonthlyTotalPayroll;
    }

    public BigDecimal getCurrentMonthlyPayroll() {
        return currentMonthlyPayroll;
    }

    public void setCurrentMonthlyPayroll(BigDecimal currentMonthlyPayroll) {
        this.currentMonthlyPayroll = currentMonthlyPayroll;
    }

    public BigDecimal getCurrentBaseSalary() {
        return currentBaseSalary;
    }

    public void setCurrentBaseSalary(BigDecimal currentBaseSalary) {
        this.currentBaseSalary = currentBaseSalary;
    }

    public Integer getCurrentHeadCount() {
        return currentHeadCount;
    }

    public void setCurrentHeadCount(Integer currentHeadCount) {
        this.currentHeadCount = currentHeadCount;
    }

    public GradeGroup getCurrentGradeGroup() {
        return currentGradeGroup;
    }

    public void setCurrentGradeGroup(GradeGroup currentGradeGroup) {
        this.currentGradeGroup = currentGradeGroup;
    }

    public PositionGroupExt getCurrentPositionGroup() {
        return currentPositionGroup;
    }

    public void setCurrentPositionGroup(PositionGroupExt currentPositionGroup) {
        this.currentPositionGroup = currentPositionGroup;
    }

    public OrganizationGroupExt getCurrentOrganizationGroup() {
        return currentOrganizationGroup;
    }

    public void setCurrentOrganizationGroup(OrganizationGroupExt currentOrganizationGroup) {
        this.currentOrganizationGroup = currentOrganizationGroup;
    }

}