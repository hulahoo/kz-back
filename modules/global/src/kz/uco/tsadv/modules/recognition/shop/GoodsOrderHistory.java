package kz.uco.tsadv.modules.recognition.shop;

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
import kz.uco.tsadv.modules.recognition.enums.GoodsOrderStatus;
import com.haulmont.cuba.core.entity.StandardEntity;

@Table(name = "TSADV_GOODS_ORDER_HISTORY")
@Entity(name = "tsadv$GoodsOrderHistory")
public class GoodsOrderHistory extends StandardEntity {
    private static final long serialVersionUID = 420352742963615534L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "GOODS_ORDER_ID")
    protected GoodsOrder goodsOrder;

    @NotNull
    @Column(name = "STATUS", nullable = false)
    protected String status;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    @Column(name = "DATE_TIME", nullable = false)
    protected Date dateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    public void setGoodsOrder(GoodsOrder goodsOrder) {
        this.goodsOrder = goodsOrder;
    }

    public GoodsOrder getGoodsOrder() {
        return goodsOrder;
    }

    public void setStatus(GoodsOrderStatus status) {
        this.status = status == null ? null : status.getId();
    }

    public GoodsOrderStatus getStatus() {
        return status == null ? null : GoodsOrderStatus.fromId(status);
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }


}