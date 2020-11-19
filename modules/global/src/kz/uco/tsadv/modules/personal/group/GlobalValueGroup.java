package kz.uco.tsadv.modules.personal.group;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.abstraction.AbstractGroup;
import kz.uco.base.entity.abstraction.IEntityGroup;
import kz.uco.tsadv.modules.personal.model.GlobalValue;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

@Table(name = "TSADV_GLOBAL_VALUE_GROUP")
@Entity(name = "tsadv$GlobalValueGroup")
public class GlobalValueGroup extends AbstractGroup implements IEntityGroup<GlobalValue> {
    private static final long serialVersionUID = -6263914923344683153L;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "group")
    protected List<GlobalValue> list;

    @Transient
    @MetaProperty
    protected GlobalValue globalValue;

    public List<GlobalValue> getList() {
        return list;
    }

    public void setList(List<GlobalValue> list) {
        this.list = list;
    }


    public GlobalValue getGlobalValue() {
        if (list != null && !list.isEmpty()) globalValue = list.get(0);
        return globalValue;
    }

    public void setGlobalValue(GlobalValue globalValue) {
        this.globalValue = globalValue;
    }


}