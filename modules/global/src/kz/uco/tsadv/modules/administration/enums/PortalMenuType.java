package kz.uco.tsadv.modules.administration.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum PortalMenuType implements EnumClass<String> {

    PORTAL("P"),
    MOBILE("M");

    private String id;

    PortalMenuType(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static PortalMenuType fromId(String id) {
        for (PortalMenuType at : PortalMenuType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}