package kz.uco.tsadv.modules.recognition.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum GoodsOrderStatus implements EnumClass<String> {

    ON_APPROVAL("ON_APPROVAL"),
    REJECTED("REJECTED"),
    WAIT_DELIVERY("WAIT_DELIVERY"),
    DELIVERED("DELIVERED");

    private String id;

    GoodsOrderStatus(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static GoodsOrderStatus fromId(String id) {
        for (GoodsOrderStatus at : GoodsOrderStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}