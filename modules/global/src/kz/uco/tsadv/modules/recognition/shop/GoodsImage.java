package kz.uco.tsadv.modules.recognition.shop;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.FileDescriptor;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import kz.uco.tsadv.modules.recognition.shop.Goods;
import com.haulmont.cuba.core.entity.StandardEntity;
import javax.validation.constraints.NotNull;
import javax.persistence.Column;

@Table(name = "TSADV_GOODS_IMAGE")
@Entity(name = "tsadv$GoodsImage")
public class GoodsImage extends StandardEntity {
    private static final long serialVersionUID = -3575419527257608035L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "GOOD_ID")
    protected Goods good;

    @NotNull
    @Column(name = "PRIMARY_", nullable = false)
    protected Boolean primary = false;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IMAGE_ID")
    protected FileDescriptor image;

    public void setPrimary(Boolean primary) {
        this.primary = primary;
    }

    public Boolean getPrimary() {
        return primary;
    }


    public void setGood(Goods good) {
        this.good = good;
    }

    public Goods getGood() {
        return good;
    }

    public void setImage(FileDescriptor image) {
        this.image = image;
    }

    public FileDescriptor getImage() {
        return image;
    }


}