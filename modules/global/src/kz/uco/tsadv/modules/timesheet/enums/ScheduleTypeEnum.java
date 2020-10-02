package kz.uco.tsadv.modules.timesheet.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum ScheduleTypeEnum implements EnumClass<String> {

    NORMATIVE("NORMATIVE"),
    WATCH("WATCH"),
    SHIFT_WORK("SHIFT_WORK");

    private String id;

    ScheduleTypeEnum(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static ScheduleTypeEnum fromId(String id) {
        for (ScheduleTypeEnum at : ScheduleTypeEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}