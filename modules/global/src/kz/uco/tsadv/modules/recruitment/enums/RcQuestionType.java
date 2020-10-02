package kz.uco.tsadv.modules.recruitment.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum RcQuestionType implements EnumClass<String> {

    SELECTION("SELECT"),
    ASSESSMENT("ASSESS"),
    COMPETENCE("COMPETE"),
    TEST_RESULT("RESULT"),
    PRE_SCREEN("PRE_SCREEN");

    private String id;

    RcQuestionType(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static RcQuestionType fromId(String id) {
        for (RcQuestionType at : RcQuestionType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}