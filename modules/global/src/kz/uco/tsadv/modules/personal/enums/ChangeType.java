package kz.uco.tsadv.modules.personal.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum ChangeType implements EnumClass<String> {

    NEW("NEW"),
    EDIT("EDIT"),
    CLOSE("CLOSE");

    private String id;

    ChangeType(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static ChangeType fromId(String id) {
        for (ChangeType at : ChangeType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}