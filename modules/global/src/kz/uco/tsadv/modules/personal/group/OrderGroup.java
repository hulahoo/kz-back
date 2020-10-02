package kz.uco.tsadv.modules.personal.group;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.core.global.PersistenceHelper;
import kz.uco.base.entity.abstraction.AbstractGroup;
import kz.uco.base.common.BaseCommonUtils;
import kz.uco.tsadv.modules.personal.model.Order;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

@NamePattern("%s|id")
@Table(name = "TSADV_ORDER_GROUP")
@Entity(name = "tsadv$OrderGroup")
public class OrderGroup extends AbstractGroup {
    private static final long serialVersionUID = -6296245116811060798L;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "group")
    protected List<Order> list;

    @Transient
    @MetaProperty(related = "list")
    protected Order order;

    public List<Order> getList() {
        return list;
    }

    public void setList(List<Order> list) {
        this.list = list;
    }

    public Order getOrder() {
        if (PersistenceHelper.isLoaded(this, "list") && list != null && !list.isEmpty()) {
            list.stream().filter(o -> o.getDeleteTs() == null
                    && !BaseCommonUtils.getSystemDate().before(o.getStartDate())
                    && !BaseCommonUtils.getSystemDate().after(o.getEndDate()))
                    .findFirst()
                    .ifPresent(o -> order = o);
        }
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }


}