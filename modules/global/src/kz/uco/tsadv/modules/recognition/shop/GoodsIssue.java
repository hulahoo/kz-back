package kz.uco.tsadv.modules.recognition.shop;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import kz.uco.base.entity.shared.Person;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import com.haulmont.cuba.core.entity.annotation.Listeners;

@Listeners("tsadv_GoodsIssueListener")
@Table(name = "TSADV_GOODS_ISSUE")
@Entity(name = "tsadv$GoodsIssue")
public class GoodsIssue extends AbstractParentEntity {
    private static final long serialVersionUID = -3737803557874282501L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "GOODS_ID")
    protected Goods goods;

    @NotNull
    @Column(name = "QUANTITY", nullable = false)
    protected Long quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID")
    protected GoodsOrder order;

    public void setOrder(GoodsOrder order) {
        this.order = order;
    }

    public GoodsOrder getOrder() {
        return order;
    }


    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }


    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public Goods getGoods() {
        return goods;
    }





}