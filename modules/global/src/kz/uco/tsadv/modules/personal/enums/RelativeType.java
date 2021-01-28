package kz.uco.tsadv.modules.personal.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum RelativeType implements EnumClass<String> {

    EMPLOYEE("Employee"),
    MEMBER("Member");

    private String id;

    RelativeType(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static RelativeType fromId(String id) {
        for (RelativeType at : RelativeType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}