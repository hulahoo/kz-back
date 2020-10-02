package kz.uco.tsadv.global.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;

/**
 * Тип сведений истории обучения лица
 */
public enum LearningHistoryType implements EnumClass<String> {

    IN_THE_SYSTEM("IN_THE_SYSTEM"),     // Обучение проводилось (регистрировалось) в системе, в модуле обучение. Источник данных - сущность Enrollment
    OUT_THE_SYSTEM("OUT_THE_SYSTEM");   // Обучение проводилось вне системы. Источник данных - сущность PersonLearningHistory

    private String id;

    LearningHistoryType(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static LearningHistoryType fromId(String id) {
        for (LearningHistoryType at : LearningHistoryType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}