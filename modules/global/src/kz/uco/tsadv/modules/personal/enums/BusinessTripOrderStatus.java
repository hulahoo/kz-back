package kz.uco.tsadv.modules.personal.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum BusinessTripOrderStatus implements EnumClass<String> {

    CHANGED("CHANGED"),
    CANCELED("CANCELED"),
    PLANNED("PLANNED"),
    APPROVED("APPROVED");

    private String id;

    BusinessTripOrderStatus(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static BusinessTripOrderStatus fromId(String id) {
        for (BusinessTripOrderStatus at : BusinessTripOrderStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}