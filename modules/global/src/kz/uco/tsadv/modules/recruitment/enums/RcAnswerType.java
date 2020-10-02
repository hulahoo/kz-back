package kz.uco.tsadv.modules.recruitment.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum RcAnswerType implements EnumClass<String> {

    SINGLE("SINGLE"),
    MULTI("MULTI"),
    TEXT("TEXT"),
    DATE("DATE"),
    NUMBER("NUM");

    private String id;

    RcAnswerType(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static RcAnswerType fromId(String id) {
        for (RcAnswerType at : RcAnswerType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}