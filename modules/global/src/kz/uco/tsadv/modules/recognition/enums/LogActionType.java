package kz.uco.tsadv.modules.recognition.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum LogActionType implements EnumClass<String> {

    RECEIVE_RECOGNITION("RECEIVE_RECOGNITION"),
    SEND_RECOGNITION("SEND_RECOGNITION"),
    ADDED_PHOTO("ADDED_PHOTO"),
    RECEIVE_HEART_COIN("RECEIVE_HEART_COIN"),
    SEND_HEART_COIN("SEND_HEART_COIN"),
    ADDED_PREFERENCE("ADDED_PREFERENCE"),
    RECEIVE_SYSTEM_POINT("RECEIVE_SYSTEM_POINT");

    private String id;

    LogActionType(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static LogActionType fromId(String id) {
        for (LogActionType at : LogActionType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}