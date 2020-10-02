package kz.uco.tsadv.modules.recognition.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum PointOperationType implements EnumClass<String> {

    RECEIPT("RECEIPT"),
    ISSUE("ISSUE");

    private String id;

    PointOperationType(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static PointOperationType fromId(String id) {
        for (PointOperationType at : PointOperationType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}