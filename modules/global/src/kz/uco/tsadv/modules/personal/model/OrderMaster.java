package kz.uco.tsadv.modules.personal.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import java.util.List;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.validation.constraints.NotNull;
import kz.uco.tsadv.modules.personal.dictionary.DicOrderType;
import kz.uco.tsadv.modules.personal.model.*;
import kz.uco.tsadv.modules.personal.model.OrderMasterEntity;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

@Table(name = "TSADV_ORDER_MASTER")
@Entity(name = "tsadv$OrderMaster")
public class OrderMaster extends AbstractParentEntity {
    private static final long serialVersionUID = 7573161023053004801L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ORDER_TYPE_ID")
    protected DicOrderType orderType;

    @OrderBy("order")
    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "orderMaster")
    protected List<kz.uco.tsadv.modules.personal.model.OrderMasterEntity> orderMasterEntities;

    public void setOrderType(DicOrderType orderType) {
        this.orderType = orderType;
    }

    public DicOrderType getOrderType() {
        return orderType;
    }

    public void setOrderMasterEntities(List<kz.uco.tsadv.modules.personal.model.OrderMasterEntity> orderMasterEntities) {
        this.orderMasterEntities = orderMasterEntities;
    }

    public List<OrderMasterEntity> getOrderMasterEntities() {
        return orderMasterEntities;
    }


}