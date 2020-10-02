package kz.uco.tsadv.modules.recognition.shop;

import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.Listeners;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Listeners("tsadv_GoodsReceiptListener")
@Table(name = "TSADV_GOODS_RECEIPT")
@Entity(name = "tsadv$GoodsReceipt")
public class GoodsReceipt extends StandardEntity {
    private static final long serialVersionUID = -5682905718507440726L;

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