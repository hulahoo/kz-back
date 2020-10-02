package kz.uco.tsadv.modules.administration.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum HiringStepType implements EnumClass<String> {

    test("TEST"),
    interview("INTERVIEW");

    private String id;

    HiringStepType(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static HiringStepType fromId(String id) {
        for (HiringStepType at : HiringStepType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}