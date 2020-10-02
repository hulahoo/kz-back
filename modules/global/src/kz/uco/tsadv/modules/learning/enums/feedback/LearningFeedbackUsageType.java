package kz.uco.tsadv.modules.learning.enums.feedback;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum LearningFeedbackUsageType implements EnumClass<String> {

    COURSE("COURSE"),
    TRAINER("TRAINER");

    private String id;

    LearningFeedbackUsageType(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static LearningFeedbackUsageType fromId(String id) {
        for (LearningFeedbackUsageType at : LearningFeedbackUsageType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}