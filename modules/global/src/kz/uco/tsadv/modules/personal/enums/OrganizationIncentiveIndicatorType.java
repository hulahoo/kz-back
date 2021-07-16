package kz.uco.tsadv.modules.personal.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum OrganizationIncentiveIndicatorType implements EnumClass<String> {

    PLAN_FACT("PLAN_FACT"),
    RESULT("RESULT");

    private String id;

    OrganizationIncentiveIndicatorType(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static OrganizationIncentiveIndicatorType fromId(String id) {
        for (OrganizationIncentiveIndicatorType at : OrganizationIncentiveIndicatorType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}