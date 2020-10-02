package kz.uco.tsadv.modules.personprotection;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personprotection.enums.PersonalProtectionEquipmentStatus;
import kz.uco.tsadv.modules.personprotection.dictionary.DicProtectionEquipment;
import kz.uco.tsadv.modules.personprotection.dictionary.DicProtectionEquipmentCondition;

@Table(name = "TSADV_PERSONAL_PROTECTION")
@Entity(name = "tsadv$PersonalProtection")
public class PersonalProtection extends AbstractParentEntity {
    private static final long serialVersionUID = -4888756318628470437L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "EMPLOYEE_ID")
    protected PersonGroupExt employee;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PROTECTION_EQUIPMENT_ID")
    protected DicProtectionEquipment protectionEquipment;

    @NotNull
    @Column(name = "QUANTITY", nullable = false)
    protected Integer quantity;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "DATE_OF_ISSUE", nullable = false)
    protected Date dateOfIssue;

    @Temporal(TemporalType.DATE)
    @Column(name = "PLAN_CHANGE_DATE")
    protected Date planChangeDate;

    @NotNull
    @Column(name = "STATUS", nullable = false)
    protected String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONDITION_ID")
    protected DicProtectionEquipmentCondition condition;

    @NotNull
    @Column(name = "IS_ACCEPTED_BY_PERSON", nullable = false)
    protected Boolean isAcceptedByPerson = false;

    @Column(name = "WRITTEN_OF_REASON")
    protected String writtenOfReason;

    @Temporal(TemporalType.DATE)
    @Column(name = "WRITTEN_OF_DATE")
    protected Date writtenOfDate;
    @Temporal(TemporalType.DATE)
    @Column(name = "REPLACEMENT_DATE")
    protected Date replacementDate;

    public void setReplacementDate(Date replacementDate) {
        this.replacementDate = replacementDate;
    }

    public Date getReplacementDate() {
        return replacementDate;
    }


    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setPlanChangeDate(Date planChangeDate) {
        this.planChangeDate = planChangeDate;
    }

    public Date getPlanChangeDate() {
        return planChangeDate;
    }

    public void setCondition(DicProtectionEquipmentCondition condition) {
        this.condition = condition;
    }

    public DicProtectionEquipmentCondition getCondition() {
        return condition;
    }

    public void setIsAcceptedByPerson(Boolean isAcceptedByPerson) {
        this.isAcceptedByPerson = isAcceptedByPerson;
    }

    public Boolean getIsAcceptedByPerson() {
        return isAcceptedByPerson;
    }


    public DicProtectionEquipment getProtectionEquipment() {
        return protectionEquipment;
    }

    public void setProtectionEquipment(DicProtectionEquipment protectionEquipment) {
        this.protectionEquipment = protectionEquipment;
    }



    public void setStatus(PersonalProtectionEquipmentStatus status) {
        this.status = status == null ? null : status.getId();
    }

    public PersonalProtectionEquipmentStatus getStatus() {
        return status == null ? null : PersonalProtectionEquipmentStatus.fromId(status);
    }


    public void setEmployee(PersonGroupExt employee) {
        this.employee = employee;
    }

    public PersonGroupExt getEmployee() {
        return employee;
    }



    public void setDateOfIssue(Date dateOfIssue) {
        this.dateOfIssue = dateOfIssue;
    }

    public Date getDateOfIssue() {
        return dateOfIssue;
    }

    public void setWrittenOfReason(String writtenOfReason) {
        this.writtenOfReason = writtenOfReason;
    }

    public String getWrittenOfReason() {
        return writtenOfReason;
    }

    public void setWrittenOfDate(Date writtenOfDate) {
        this.writtenOfDate = writtenOfDate;
    }

    public Date getWrittenOfDate() {
        return writtenOfDate;
    }


}