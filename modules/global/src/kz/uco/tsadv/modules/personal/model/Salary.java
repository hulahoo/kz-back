package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.abstraction.AbstractTimeBasedEntity;
import kz.uco.base.entity.dictionary.DicCurrency;
import kz.uco.tsadv.modules.personal.dictionary.DicSalaryChangeReason;
import kz.uco.tsadv.modules.personal.enums.GrossNet;
import kz.uco.tsadv.modules.personal.enums.SalaryType;
import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;
import kz.uco.tsadv.modules.personal.group.OrderGroup;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Listeners("tsadv_SalaryListener")
@Table(name = "TSADV_SALARY")
@Entity(name = "tsadv$Salary")
public class Salary extends AbstractTimeBasedEntity {
    private static final long serialVersionUID = 1587747507710789401L;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ASSIGNMENT_GROUP_ID")
    protected AssignmentGroupExt assignmentGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SALARY_REQUEST_ID")
    protected SalaryRequest salaryRequest;

    @Column(name = "AMOUNT")
    protected Double amount;

    @Column(name = "NET_GROSS", nullable = false)
    protected String netGross;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REASON_ID")
    protected DicSalaryChangeReason reason;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CURRENCY_ID")
    protected DicCurrency currency;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORD_ASSIGNMENT_ID")
    protected OrdAssignment ordAssignment;

    @OnDeleteInverse(DeletePolicy.UNLINK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_GROUP_ID")
    protected OrderGroup orderGroup;

    @OnDeleteInverse(DeletePolicy.UNLINK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AGREEMENT_ID")
    protected Agreement agreement;

    @Transient
    @MetaProperty
    protected Boolean isUpdatedManually;

    @NotNull
    @Column(name = "TYPE_", nullable = false)
    protected String type;

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setSalaryRequest(SalaryRequest salaryRequest) {
        this.salaryRequest = salaryRequest;
    }

    public SalaryRequest getSalaryRequest() {
        return salaryRequest;
    }

    public void setType(SalaryType type) {
        this.type = type == null ? null : type.getId();
    }

    public SalaryType getType() {
        return type == null ? null : SalaryType.fromId(type);
    }

    public DicSalaryChangeReason getReason() {
        return reason;
    }

    public void setReason(DicSalaryChangeReason reason) {
        this.reason = reason;
    }

    public Agreement getAgreement() {
        return agreement;
    }

    public void setAgreement(Agreement agreement) {
        this.agreement = agreement;
    }

    public void setOrderGroup(OrderGroup orderGroup) {
        this.orderGroup = orderGroup;
    }

    public OrderGroup getOrderGroup() {
        return orderGroup;
    }

    public GrossNet getNetGross() {
        return netGross == null ? null : GrossNet.fromId(netGross);
    }

    public void setNetGross(GrossNet netGross) {
        this.netGross = netGross == null ? null : netGross.getId();
    }

    public void setOrdAssignment(OrdAssignment ordAssignment) {
        this.ordAssignment = ordAssignment;
    }

    public OrdAssignment getOrdAssignment() {
        return ordAssignment;
    }

    public void setAssignmentGroup(AssignmentGroupExt assignmentGroup) {
        this.assignmentGroup = assignmentGroup;
    }

    public AssignmentGroupExt getAssignmentGroup() {
        return assignmentGroup;
    }

    public void setCurrency(DicCurrency currency) {
        this.currency = currency;
    }

    public DicCurrency getCurrency() {
        return currency;
    }

    @Transient
    @MetaProperty
    public Boolean isUpdatedManually() {
        return isUpdatedManually;
    }

    @Transient
    @MetaProperty
    public void setUpdatedManually(Boolean updatedManually) {
        isUpdatedManually = updatedManually;
    }
}