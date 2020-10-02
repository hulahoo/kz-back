package kz.uco.tsadv.modules.personal.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum SalaryType implements EnumClass<String> {

    monthlyRate("MONTHLYRATE"),
    hourlyRate("HOURLYRATE");

    private String id;

    SalaryType(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static SalaryType fromId(String id) {
        for (SalaryType at : SalaryType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}