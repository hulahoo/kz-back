package kz.uco.tsadv.global.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum SendingToOracleStatus implements EnumClass<String> {

    SENT_TO_ORACLE("SentToOracle"),
    SENDING_TO_ORACLE("SendingToOracle");

    private String id;

    SendingToOracleStatus(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static SendingToOracleStatus fromId(String id) {
        for (SendingToOracleStatus at : SendingToOracleStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}