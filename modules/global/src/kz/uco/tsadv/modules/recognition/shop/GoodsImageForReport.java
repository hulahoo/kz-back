package kz.uco.tsadv.modules.recognition.shop;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.FileDescriptor;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;

@Table(name = "TSADV_GOODS_IMAGE_FOR_REPORT")
@Entity(name = "tsadv$GoodsImageForReport")
public class GoodsImageForReport extends StandardEntity {
    private static final long serialVersionUID = -5099889121190553276L;

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

    public void setGood(Goods good) {
        this.good = good;
    }

    public Goods getGood() {
        return good;
    }

    public void setPrimary(Boolean primary) {
        this.primary = primary;
    }

    public Boolean getPrimary() {
        return primary;
    }

    public void setImage(FileDescriptor image) {
        this.image = image;
    }

    public FileDescriptor getImage() {
        return image;
    }


}