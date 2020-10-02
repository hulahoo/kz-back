package kz.uco.tsadv.modules.recognition.shop;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.recognition.enums.GoodsOrderStatus;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import java.util.List;
import javax.persistence.OneToMany;
import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import kz.uco.tsadv.modules.recognition.dictionary.DicDeliveryAddress;

@Listeners("tsadv_GoodsOrderListener")
@NamePattern("%s|orderNumber")
@Table(name = "TSADV_GOODS_ORDER")
@Entity(name = "tsadv$GoodsOrder")
public class GoodsOrder extends StandardEntity {
    private static final long serialVersionUID = 6885662167934190478L;

    @NotNull
    @Column(name = "STATUS", nullable = false)
    protected String status;

    @Lookup(type = LookupType.SCREEN)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DELIVERY_ADDRESS_ID")
    protected DicDeliveryAddress deliveryAddress;

    @Column(name = "DISCOUNT")
    protected Integer discount;

    @OneToMany(mappedBy = "goodsOrder")
    protected List<GoodsOrderHistory> histories;

    @NotNull
    @Column(name = "TOTAL_SUM", nullable = false)
    protected Long totalSum;

    @NotNull
    @Column(name = "ORDER_NUMBER", nullable = false)
    protected String orderNumber;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    @Column(name = "ORDER_DATE", nullable = false)
    protected Date orderDate;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "goodsOrder")
    protected List<GoodsOrderDetail> details;

    public void setDeliveryAddress(DicDeliveryAddress deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public DicDeliveryAddress getDeliveryAddress() {
        return deliveryAddress;
    }


    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public Integer getDiscount() {
        return discount;
    }


    public void setHistories(List<GoodsOrderHistory> histories) {
        this.histories = histories;
    }

    public List<GoodsOrderHistory> getHistories() {
        return histories;
    }


    public void setTotalSum(Long totalSum) {
        this.totalSum = totalSum;
    }

    public Long getTotalSum() {
        return totalSum;
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


    public void setDetails(List<GoodsOrderDetail> details) {
        this.details = details;
    }

    public List<GoodsOrderDetail> getDetails() {
        return details;
    }


    public void setStatus(GoodsOrderStatus status) {
        this.status = status == null ? null : status.getId();
    }

    public GoodsOrderStatus getStatus() {
        return status == null ? null : GoodsOrderStatus.fromId(status);
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }


}