package kz.uco.tsadv.modules.performance.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum AssignedGoalTypeEnum implements EnumClass<String> {

    LIBRARY("LIBRARY"),
    CASCADE("CASCADE"),
    INDIVIDUAL("INDIVIDUAL");

    private String id;

    AssignedGoalTypeEnum(String value) {
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