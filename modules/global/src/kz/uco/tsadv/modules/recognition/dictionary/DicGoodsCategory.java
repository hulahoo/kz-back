package kz.uco.tsadv.modules.recognition.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;

@Table(name = "TSADV_DIC_GOODS_CATEGORY")
@Entity(name = "tsadv$DicGoodsCategory")
public class DicGoodsCategory extends AbstractDictionary {
    private static final long serialVersionUID = 6456969139870087915L;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ID")
    protected DicGoodsCategory parent;

    public void setParent(DicGoodsCategory parent) {
        this.parent = parent;
    }

    public DicGoodsCategory getParent() {
        return parent;
    }


}