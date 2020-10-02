package kz.uco.tsadv.modules.personal.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum BusinessTripOrderType implements EnumClass<String> {

    RECALL("RECALL"),
    TRANSFER("TRANSFER"),
    EXTENDED("EXTENDED"),
    ADDITIONALCHANGE("ADDITIONALCHANGE");

    private String id;

    BusinessTripOrderType(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static BusinessTripOrderType fromId(String id) {
        for (BusinessTripOrderType at : BusinessTripOrderType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}