package kz.uco.tsadv.modules.learning.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum LmsSliderPosition implements EnumClass<String> {

    HOME("HOME");

    private String id;

    LmsSliderPosition(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static LmsSliderPosition fromId(String id) {
        for (LmsSliderPosition at : LmsSliderPosition.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}