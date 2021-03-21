package kz.uco.tsadv.modules.performance.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum CardStatusEnum implements EnumClass<String> {

    DRAFT("DRAFT", 0),
    COMPLETED("COMPLETED", 1),
    ASSESSMENT("ASSESSMENT", 2);

    private final String id;
    private final int order;

    CardStatusEnum(String value, int order) {
        this.id = value;
        this.order = order;
    }

    public String getId() {
        return id;
    }

    public int getOrder() {
        return order;
    }

    @Nullable
    public static CardStatusEnum fromId(String id) {
        for (CardStatusEnum at : CardStatusEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }

    @Nullable
    public static CardStatusEnum fromOrder(int order) {
        for (CardStatusEnum at : CardStatusEnum.values()) {
            if (at.getOrder() == order) {
                return at;
            }
        }
        return null;
    }

}