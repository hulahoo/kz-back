package kz.uco.tsadv.modules.performance.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum CardStatusEnum implements EnumClass<String> {

    DRAFT("DRAFT"),
    COMPLETED("COMPLETED"),
    ASSESSMENT("ASSESSMENT");

    private String id;

    CardStatusEnum(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
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
}