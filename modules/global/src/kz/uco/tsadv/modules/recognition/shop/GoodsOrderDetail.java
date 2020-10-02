package kz.uco.tsadv.modules.recognition.shop;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import com.haulmont.cuba.core.entity.FileDescriptor;
import javax.persistence.OneToOne;

@Listeners("tsadv_GoodsOrderDetailListener")
@Table(name = "TSADV_GOODS_ORDER_DETAIL")
@Entity(name = "tsadv$GoodsOrderDetail")
public class GoodsOrderDetail extends StandardEntity {
    private static final long serialVersionUID = 8131388658407488696L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "GOODS_ID")
    protected Goods goods;

    @Column(name = "COMMENT_", length = 2000)
    protected String comment;

    @NotNull
    @Column(name = "EXCLUDED", nullable = false)
    protected Boolean excluded = false;

    @NotNull
    @Column(name = "QUANTITY", nullable = false)
    protected Long quantity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "GOODS_ORDER_ID")
    protected GoodsOrder goodsOrder;

    @NotNull
    @Column(name = "VOUCHER_USED", nullable = false)
    protected Boolean voucherUsed = false;

    @Column(name = "QR_CODE")
    protected String qrCode;

    @Lookup(type = LookupType.DROPDOWN)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QR_CODE_IMG_ID")
    protected FileDescriptor qrCodeImg;

    public void setQrCodeImg(FileDescriptor qrCodeImg) {
        this.qrCodeImg = qrCodeImg;
    }

    public FileDescriptor getQrCodeImg() {
        return qrCodeImg;
    }


    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getQrCode() {
        return qrCode;
    }



    public void setVoucherUsed(Boolean voucherUsed) {
        this.voucherUsed = voucherUsed;
    }

    public Boolean getVoucherUsed() {
        return voucherUsed;
    }


    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setExcluded(Boolean excluded) {
        this.excluded = excluded;
    }

    public Boolean getExcluded() {
        return excluded;
    }


    public void setGoodsOrder(GoodsOrder goodsOrder) {
        this.goodsOrder = goodsOrder;
    }

    public GoodsOrder getGoodsOrder() {
        return goodsOrder;
    }


    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public Goods getGoods() {
        return goods;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Long getQuantity() {
        return quantity;
    }


}