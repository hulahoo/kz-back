package kz.uco.tsadv.modules.recognition.shop;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import com.haulmont.cuba.core.entity.StandardEntity;

@Table(name = "TSADV_GOODS_CART")
@Entity(name = "tsadv$GoodsCart")
public class GoodsCart extends StandardEntity {
    private static final long serialVersionUID = 3079895103517775056L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "GOODS_ID")
    protected Goods goods;

    @NotNull
    @Column(name = "ISSUED", nullable = false)
    protected Boolean issued = false;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @NotNull
    @Column(name = "QUANTITY", nullable = false)
    protected Long quantity;

    public void setIssued(Boolean issued) {
        this.issued = issued;
    }

    public Boolean getIssued() {
        return issued;
    }


    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public Goods getGoods() {
        return goods;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Long getQuantity() {
        return quantity;
    }


}