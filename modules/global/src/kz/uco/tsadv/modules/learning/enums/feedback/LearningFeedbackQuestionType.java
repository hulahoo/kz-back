package kz.uco.tsadv.modules.learning.enums.feedback;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum LearningFeedbackQuestionType implements EnumClass<String> {

    TEXT("TEXT"),
    ONE("ONE"),
    MANY("MANY"),
    NUM("NUM");

    private String id;

    LearningFeedbackQuestionType(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static LearningFeedbackQuestionType fromId(String id) {
        for (LearningFeedbackQuestionType at : LearningFeedbackQuestionType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}