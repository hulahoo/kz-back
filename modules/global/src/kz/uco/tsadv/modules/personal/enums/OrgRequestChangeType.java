package kz.uco.tsadv.modules.personal.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum OrgRequestChangeType implements EnumClass<String> {

    NEW("NEW"),
    EDIT("EDIT"),
    CLOSE("CLOSE");

    private String id;

    OrgRequestChangeType(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static OrgRequestChangeType fromId(String id) {
        for (OrgRequestChangeType at : OrgRequestChangeType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}