package kz.uco.tsadv.modules.personal.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.haulmont.cuba.core.entity.annotation.Listeners;
import kz.uco.tsadv.modules.personal.dictionary.DicAwardType;
import kz.uco.tsadv.modules.personal.dictionary.DicPromotionType;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

@Listeners("tsadv_AwardsListener")
@Table(name = "TSADV_AWARDS")
@Entity(name = "tsadv$Awards")
public class Awards extends AbstractParentEntity {
    private static final long serialVersionUID = -1942689863888906200L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROMOTION_TYPE_ID")
    protected DicPromotionType promotionType;

    @Column(name = "CALCULATED")
    protected String calculated;

    @Column(name = "SUR_CHARGE_TYPE")
    protected Integer surChargeType;

    @Column(name = "VALUE_")
    protected Integer value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AWARD_TYPE_ID")
    protected DicAwardType awardType;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_")
    protected Date date;

    @Column(name = "ORDER_NUM")
    protected String orderNum;

    @Temporal(TemporalType.DATE)
    @Column(name = "ORDER_DATE")
    protected Date orderDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ASSIGNMENT_GROUP_ID")
    protected AssignmentGroupExt assignmentGroup;

    @Column(name = "REASON")
    protected String reason;


    public void setCalculated(String calculated) {
        this.calculated = calculated;
    }

    public String getCalculated() {
        return calculated;
    }


    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }


    public void setSurChargeType(SurChargeType surChargeType) {
        this.surChargeType = surChargeType == null ? null : surChargeType.getId();
    }

    public SurChargeType getSurChargeType() {
        return surChargeType == null ? null : SurChargeType.fromId(surChargeType);
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }


    public void setAssignmentGroup(AssignmentGroupExt assignmentGroup) {
        this.assignmentGroup = assignmentGroup;
    }

    public AssignmentGroupExt getAssignmentGroup() {
        return assignmentGroup;
    }


    public void setPromotionType(DicPromotionType promotionType) {
        this.promotionType = promotionType;
    }

    public DicPromotionType getPromotionType() {
        return promotionType;
    }

    public void setAwardType(DicAwardType awardType) {
        this.awardType = awardType;
    }

    public DicAwardType getAwardType() {
        return awardType;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Date getOrderDate() {
        return orderDate;
    }




}