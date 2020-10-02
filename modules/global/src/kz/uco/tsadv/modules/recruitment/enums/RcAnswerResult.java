package kz.uco.tsadv.modules.recruitment.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum RcAnswerResult implements EnumClass<String> {

    PASS("PASS"),
    NOT_PASS("NOT_PASS"),
    VERIFY("VERIFY");

    private String id;

    RcAnswerResult(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static RcAnswerResult fromId(String id) {
        for (RcAnswerResult at : RcAnswerResult.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}