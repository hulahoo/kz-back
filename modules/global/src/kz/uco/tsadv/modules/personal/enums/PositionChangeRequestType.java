package kz.uco.tsadv.modules.personal.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum PositionChangeRequestType implements EnumClass<String> {

    NEW("NEW"),
    CHANGE("CHANGE"),
    CLOSE("CLOSE");

    private String id;

    PositionChangeRequestType(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static PositionChangeRequestType fromId(String id) {
        for (PositionChangeRequestType at : PositionChangeRequestType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}