package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.chile.core.datatypes.Datatypes;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.core.global.UserSessionSource;
import kz.uco.tsadv.modules.personal.dictionary.DicOrderReason;
import kz.uco.tsadv.modules.personal.dictionary.DicOrderStatus;
import kz.uco.tsadv.modules.personal.dictionary.DicOrderType;
import kz.uco.tsadv.modules.personal.group.OrderGroup;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.base.entity.abstraction.AbstractTimeBasedEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@NamePattern("#getCaption|orderType,orderNumber,orderDate")
@Table(name = "TSADV_ORDER")
@Entity(name = "tsadv$Order")
public class Order extends AbstractTimeBasedEntity {
    private static final long serialVersionUID = -4619528381745503842L;

    @Column(name = "ORDER_NUMBER", nullable = false)
    protected String orderNumber;

    @Temporal(TemporalType.DATE)
    @Column(name = "ORDER_DATE", nullable = false)
    protected Date orderDate;


    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "open", "clear"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ID")
    protected OrderGroup parent;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID")
    protected OrderGroup group;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ORDER_TYPE_ID")
    protected DicOrderType orderType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_STATUS_ID")
    protected DicOrderStatus orderStatus;


    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "order")
    protected List<OrdAssignment> ordAssignment;

    @Column(name = "CANCEL_ORDER_NUMBER")
    protected Integer cancelOrderNumber;

    @Temporal(TemporalType.DATE)
    @Column(name = "CANCEL_ORDER_DATE")
    protected Date cancelOrderDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CANCEL_ORDER_REASON_ID")
    protected DicOrderReason cancelOrderReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APPROVER_PERSON_GROUP_ID")
    protected PersonGroupExt approverPersonGroup;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_REASON_ID")
    protected DicOrderReason orderReason;

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }


    public void setOrderReason(DicOrderReason orderReason) {
        this.orderReason = orderReason;
    }

    public DicOrderReason getOrderReason() {
        return orderReason;
    }


    public void setApproverPersonGroup(PersonGroupExt approverPersonGroup) {
        this.approverPersonGroup = approverPersonGroup;
    }

    public PersonGroupExt getApproverPersonGroup() {
        return approverPersonGroup;
    }


    public void setCancelOrderReason(DicOrderReason cancelOrderReason) {
        this.cancelOrderReason = cancelOrderReason;
    }

    public DicOrderReason getCancelOrderReason() {
        return cancelOrderReason;
    }


    public void setCancelOrderNumber(Integer cancelOrderNumber) {
        this.cancelOrderNumber = cancelOrderNumber;
    }

    public Integer getCancelOrderNumber() {
        return cancelOrderNumber;
    }

    public void setCancelOrderDate(Date cancelOrderDate) {
        this.cancelOrderDate = cancelOrderDate;
    }

    public Date getCancelOrderDate() {
        return cancelOrderDate;
    }


    public List<OrdAssignment> getOrdAssignment() {
        return ordAssignment;
    }

    public void setOrdAssignment(List<OrdAssignment> ordAssignment) {
        this.ordAssignment = ordAssignment;
    }


    public OrderGroup getParent() {
        return parent;
    }

    public void setParent(OrderGroup parent) {
        this.parent = parent;
    }


    public void setGroup(OrderGroup group) {
        this.group = group;
    }

    public OrderGroup getGroup() {
        return group;
    }


    public DicOrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(DicOrderType orderType) {
        this.orderType = orderType;
    }


    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Date getOrderDate() {
        return orderDate;
    }


    public void setOrderStatus(DicOrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public DicOrderStatus getOrderStatus() {
        return orderStatus;
    }

    @MetaProperty
    public String getCaption() {
        UserSessionSource userSessionSource = AppBeans.get(UserSessionSource.NAME);

        StringBuilder sb = new StringBuilder();
        if (PersistenceHelper.isLoaded(this, "orderType") && this.getOrderType() != null)
            sb.append(this.getOrderType().getLangValue());
        if (PersistenceHelper.isLoaded(this, "orderNumber") && this.getOrderNumber() != null)
            sb.append(" ").append(this.getOrderNumber());
        if (PersistenceHelper.isLoaded(this, "orderDate") && this.getOrderDate() != null)
            sb.append(" ").append(Datatypes.get("date").format(this.getOrderDate(), userSessionSource.getLocale()));

        return sb.toString().trim();
    }
    
}