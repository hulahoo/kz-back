package kz.uco.tsadv.modules.learning.model.dto;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import kz.uco.tsadv.modules.learning.model.BudgetRequestItem;
import kz.uco.tsadv.modules.personal.dictionary.DicCostType;

import java.util.List;

@MetaClass(name = "tsadv$BudgetDto")
public class BudgetRequestItemsRowDto extends BaseUuidEntity {

    @MetaProperty
    private String name;

    @MetaProperty
    private List<BudgetRequestItem> list;

    @MetaProperty
    DicCostType dicCostType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<BudgetRequestItem> getList() {
        return list;
    }

    public void setList(List<BudgetRequestItem> list) {
        this.list = list;
    }

    public DicCostType getDicCostType() {
        return dicCostType;
    }

    public void setDicCostType(DicCostType dicCostType) {
        this.dicCostType = dicCostType;
    }

}
