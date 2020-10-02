package kz.uco.tsadv.modules.personal.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum ElementTypeForGoals implements EnumClass<Integer> {

    ORGANIZATION(1),
    POSITION(2),
    JOB(3);

    private Integer id;

    ElementTypeForGoals(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static ElementTypeForGoals fromId(Integer id) {
        for (ElementTypeForGoals at : ElementTypeForGoals.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}