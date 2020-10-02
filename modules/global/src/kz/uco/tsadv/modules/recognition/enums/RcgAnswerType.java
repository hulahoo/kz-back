package kz.uco.tsadv.modules.recognition.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum RcgAnswerType implements EnumClass<String> {

    ICON("ICON"),
    RADIO("RADIO");

    private String id;

    RcgAnswerType(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static RcgAnswerType fromId(String id) {
        for (RcgAnswerType at : RcgAnswerType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}