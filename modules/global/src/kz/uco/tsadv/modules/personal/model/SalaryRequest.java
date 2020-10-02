package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.base.entity.dictionary.DicCurrency;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.modules.personal.dictionary.DicSalaryChangeReason;
import kz.uco.tsadv.modules.personal.enums.GrossNet;
import kz.uco.tsadv.modules.personal.enums.SalaryType;
import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;
import kz.uco.tsadv.modules.personal.group.OrderGroup;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Table(name = "TSADV_SALARY_REQUEST")
@Entity(name = "tsadv$SalaryRequest")
public class SalaryRequest extends AbstractParentEntity {
    private static final long serialVersionUID = 1587747507710789401L;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ASSIGNMENT_GROUP_ID")
    protected AssignmentGroupExt assignmentGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ATTACHMENT_ID")
    protected FileDescriptor attachment;

    @Column(name = "NOTE", length = 3000)
    protected String note;

    @Column(name = "REQUEST_NUMBER")
    protected Long requestNumber;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "STATUS_ID")
    protected DicRequestStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OLD_SALARY")
    protected Salary oldSalary;

    @Column(name = "AMOUNT", nullable = false)
    protected Double amount;

    @Column(name = "CHANGE_PERCENT")
    protected Double changePercent;

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

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE", nullable = false)
    protected Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE", nullable = false)
    protected Date endDate;

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

    public Long getRequestNumber() {
        return requestNumber;
    }

    public void setRequestNumber(Long requestNumber) {
        this.requestNumber = requestNumber;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setAttachment(FileDescriptor attachment) {
        this.attachment = attachment;
    }

    public FileDescriptor getAttachment() {
        return attachment;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getNote() {
        return note;
    }

    @MetaProperty
    public Double getDifference() {
        return (amount != null ? amount : 0) - (oldSalary != null && oldSalary.getAmount() != null ? oldSalary.getAmount() : 0);
    }

    public void setChangePercent(Double changePercent) {
        this.changePercent = changePercent;
    }

    public Double getChangePercent() {
        return changePercent;
    }

    public void setStatus(DicRequestStatus status) {
        this.status = status;
    }

    public DicRequestStatus getStatus() {
        return status;
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

    public Boolean getUpdatedManually() {
        return isUpdatedManually;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getEndDate() {
        return endDate;
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

    public Salary getOldSalary() {
        return oldSalary;
    }

    public void setOldSalary(Salary oldSalary) {
        this.oldSalary = oldSalary;
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