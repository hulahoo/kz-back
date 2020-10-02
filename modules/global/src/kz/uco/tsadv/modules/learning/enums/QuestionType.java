package kz.uco.tsadv.modules.learning.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum QuestionType implements EnumClass<Integer> {

    TEXT(1),
    ONE(2),
    MANY(3),
    NUM(4);

    private Integer id;

    QuestionType(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static QuestionType fromId(Integer id) {
        for (QuestionType at : QuestionType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}