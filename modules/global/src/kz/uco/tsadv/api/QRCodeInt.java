package kz.uco.tsadv.api;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

@MetaClass(name = "tsadv$QRCodeInt")
public class QRCodeInt extends BaseUuidEntity {
    private static final long serialVersionUID = -2897126350447591553L;

    @MetaProperty
    protected String fullName;

    @MetaProperty
    protected String qrCode;

    @MetaProperty
    protected String quantity;

    @MetaProperty
    protected String image;

    @MetaProperty
    protected String voucherUsed;

    @MetaProperty
    protected String goodsName;

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public String getVoucherUsed() {
        return voucherUsed;
    }

    public void setVoucherUsed(String voucherUsed) {
        this.voucherUsed = voucherUsed;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }
}