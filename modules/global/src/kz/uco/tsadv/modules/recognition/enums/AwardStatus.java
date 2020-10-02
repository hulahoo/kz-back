package kz.uco.tsadv.modules.recognition.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum AwardStatus implements EnumClass<String> {

    NOMINATED("NOMINATED"),
    SHORTLIST("SHORTLIST"),
    AWARDED("AWARDED"),
    DRAFT("DRAFT");

    private String id;

    AwardStatus(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static AwardStatus fromId(String id) {
        for (AwardStatus at : AwardStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}