package kz.uco.tsadv.global.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum BooleanEnum implements EnumClass<String> {

    TRUE("TRUE"),
    FALSE("FALSE");

    private String id;

    BooleanEnum(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static BooleanEnum fromId(String id) {
        for (BooleanEnum at : BooleanEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}