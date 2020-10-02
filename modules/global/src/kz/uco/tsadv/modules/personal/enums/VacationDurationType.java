package kz.uco.tsadv.modules.personal.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum VacationDurationType implements EnumClass<String> {

    CALENDAR("CALENDAR"),
    WORK("WORK");

    private String id;

    VacationDurationType(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static VacationDurationType fromId(String id) {
        for (VacationDurationType at : VacationDurationType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}