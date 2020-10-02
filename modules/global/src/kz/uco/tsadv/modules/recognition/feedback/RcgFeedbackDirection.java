package kz.uco.tsadv.modules.recognition.feedback;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum RcgFeedbackDirection implements EnumClass<String> {

    SEND("SEND"),
    REQUEST("REQUEST"),
    ANSWERED("ANSWERED");

    private String id;

    RcgFeedbackDirection(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static RcgFeedbackDirection fromId(String id) {
        for (RcgFeedbackDirection at : RcgFeedbackDirection.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}