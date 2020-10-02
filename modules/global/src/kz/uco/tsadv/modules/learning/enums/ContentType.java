package kz.uco.tsadv.modules.learning.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum ContentType implements EnumClass<String> {

    URL("URL"),
    VIDEO("VIDEO"),
    PDF("PDF"),
    FILE("FILE"),
    HTML("HTML"),
    TEXT("TEXT"),
    SCORM_ZIP("SCORM_ZIP");

    private String id;

    ContentType(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static ContentType fromId(String id) {
        for (ContentType at : ContentType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}