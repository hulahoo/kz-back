package kz.uco.tsadv.modules.administration.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum PortalAvailability implements EnumClass<String> {

    FOR_ALL("FOR_ALL"),
    FOR_MANAGER("FOR_MANAGER"),
    FOR_ASSISTANT("FOR_ASSISTANT");

    private String id;

    PortalAvailability(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static PortalAvailability fromId(String id) {
        for (PortalAvailability at : PortalAvailability.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}