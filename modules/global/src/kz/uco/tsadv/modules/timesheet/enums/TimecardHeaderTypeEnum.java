package kz.uco.tsadv.modules.timesheet.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum TimecardHeaderTypeEnum implements EnumClass<String> {

    FACT("FACT"),
    PLAN("PLAN"),
    BASE("BASE");

    private String id;

    TimecardHeaderTypeEnum(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static TimecardHeaderTypeEnum fromId(String id) {
        for (TimecardHeaderTypeEnum at : TimecardHeaderTypeEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}