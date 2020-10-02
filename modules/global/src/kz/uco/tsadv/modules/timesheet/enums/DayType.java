package kz.uco.tsadv.modules.timesheet.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum DayType implements EnumClass<String> {
    HOLIDAY("HOLIDAY"),
    WEEKEND("WEEKEND"),
    TRANSFER("TRANSFER"),
    OFFICIAL_WEEKEND("OFFICIAL_WEEKEND");

    private String id;

    DayType(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static DayType fromId(String id) {
        for (DayType at : DayType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}