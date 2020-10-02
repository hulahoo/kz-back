package kz.uco.tsadv.modules.personal.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum AnalyticsTypeEnum implements EnumClass<String> {

    String("String"),
    Number("Number"),
    Date("Date"),
    Boolean("Boolean"),
    Dictionary("Dictionary");

    private String id;

    AnalyticsTypeEnum(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static AnalyticsTypeEnum fromId(String id) {
        for (AnalyticsTypeEnum at : AnalyticsTypeEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}