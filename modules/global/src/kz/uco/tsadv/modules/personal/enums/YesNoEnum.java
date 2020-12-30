package kz.uco.tsadv.modules.personal.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum YesNoEnum implements EnumClass<String> {

    YES("Yes"),
    NO("No");

    private String id;

    YesNoEnum(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static YesNoEnum fromId(String id) {
        for (YesNoEnum at : YesNoEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}