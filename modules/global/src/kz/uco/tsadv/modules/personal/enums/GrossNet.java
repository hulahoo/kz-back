package kz.uco.tsadv.modules.personal.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum GrossNet implements EnumClass<String> {

    GROSS("GROSS"),
    NET("NET");

    private String id;

    GrossNet(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static GrossNet fromId(String id) {
        for (GrossNet at : GrossNet.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}