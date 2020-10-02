package kz.uco.tsadv.modules.personal.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum ArticleAttribute implements EnumClass<String> {
    DISMISSAL("DISMISSAL"),
    PUNISHMENT("PUNISHMENT");

    private String id;

    ArticleAttribute(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static ArticleAttribute fromId(String id) {
        for (ArticleAttribute at : ArticleAttribute.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}