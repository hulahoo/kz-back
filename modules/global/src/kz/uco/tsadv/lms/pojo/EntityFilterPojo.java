package kz.uco.tsadv.lms.pojo;

import java.io.Serializable;
import java.util.List;

public class EntityFilterPojo implements Serializable {
    List<ConditionPojo> conditions;

    public List<ConditionPojo> getConditions() {
        return conditions;
    }

    public void setConditions(List<ConditionPojo> conditions) {
        this.conditions = conditions;
    }
}
