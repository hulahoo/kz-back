package kz.uco.tsadv.modules.learning.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum IdpStatus implements EnumClass<String> {

    DRAFT("DRAFT"),
    APPROVED("APPROVED"),
    CLOSED("CLOSED");

    private String id;

    IdpStatus(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static IdpStatus fromId(String id) {
        for (IdpStatus at : IdpStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}