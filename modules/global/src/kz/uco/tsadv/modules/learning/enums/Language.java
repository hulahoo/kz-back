package kz.uco.tsadv.modules.learning.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum Language implements EnumClass<String> {

    ru("ru"),
    kz("kz"),
    en("en");

    private String id;

    Language(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static Language fromId(String id) {
        for (Language at : Language.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}