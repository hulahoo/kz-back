package kz.uco.tsadv.modules.personprotection.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum PersonalProtectionEquipmentStatus implements EnumClass<String> {
    ISSUED_BY("ISSUED_BY"),
    RETURN("RETURN"),
    LOST("LOST"),
    NOT_AVAILABLE("NOT_AVAILABLE"),
    ARE_AVAILABLE("ARE_AVAILABLE");

    private String id;

    PersonalProtectionEquipmentStatus(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static PersonalProtectionEquipmentStatus fromId(String id) {
        for (PersonalProtectionEquipmentStatus at : PersonalProtectionEquipmentStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}