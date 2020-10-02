package kz.uco.tsadv.modules.recruitment.config;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum OrganizationGenerationType implements EnumClass<String> {

    FULL("FULL"),
    MANAGER("MANAGER");

    private String id;

    OrganizationGenerationType(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static OrganizationGenerationType fromId(String id) {
        for (OrganizationGenerationType at : OrganizationGenerationType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}