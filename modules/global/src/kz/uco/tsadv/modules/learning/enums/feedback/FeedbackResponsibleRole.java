package kz.uco.tsadv.modules.learning.enums.feedback;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum FeedbackResponsibleRole implements EnumClass<String> {

    LEARNER("LEARNER"),
    MANAGER("MANAGER"),
    TRAINER("TRAINER");

    private String id;

    FeedbackResponsibleRole(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static FeedbackResponsibleRole fromId(String id) {
        for (FeedbackResponsibleRole at : FeedbackResponsibleRole.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}