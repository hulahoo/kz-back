package kz.uco.tsadv.modules.recognition.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum RecognitionCoinType implements EnumClass<String> {

    COIN("COIN"),
    POINT("POINT");

    private String id;

    RecognitionCoinType(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static RecognitionCoinType fromId(String id) {
        for (RecognitionCoinType at : RecognitionCoinType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}