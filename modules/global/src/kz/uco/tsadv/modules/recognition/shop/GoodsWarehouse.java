package kz.uco.tsadv.modules.recognition.shop;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import kz.uco.tsadv.modules.recognition.shop.Goods;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

@Table(name = "TSADV_GOODS_WAREHOUSE")
@Entity(name = "tsadv$GoodsWarehouse")
public class GoodsWarehouse extends AbstractParentEntity {
    private static final long serialVersionUID = 7157902047575441384L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "GOODS_ID")
    protected Goods goods;

    @NotNull
    @Column(name = "QUANTITY", nullable = false)
    protected Long quantity;

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public Goods getGoods() {
        return goods;
    }


    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }



}