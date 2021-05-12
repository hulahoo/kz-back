package kz.uco.tsadv.api;

import com.haulmont.chile.core.datatypes.impl.EnumClass;
import kz.uco.tsadv.modules.performance.enums.AssignedGoalTypeEnum;

import javax.annotation.Nullable;

public enum OrgStructureRequestDisplayType implements EnumClass<String> {

    ALL("ALL"),
    CHANGES("CHANGES");

    private String id;

    OrgStructureRequestDisplayType(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static AssignedGoalTypeEnum fromId(String id) {
        for (AssignedGoalTypeEnum at : AssignedGoalTypeEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}
