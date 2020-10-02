package kz.uco.tsadv.modules.timesheet.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum DeviationChangingTypeEnum implements EnumClass<String> {

    FROM_BEGIN("FROM_BEGIN"),
    FROM_END("FROM_END");

    private String id;

    DeviationChangingTypeEnum(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static DeviationChangingTypeEnum fromId(String id) {
        for (DeviationChangingTypeEnum at : DeviationChangingTypeEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}