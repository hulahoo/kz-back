package kz.uco.tsadv.modules.timesheet.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum TimecardHeaderStatusEnum implements EnumClass<Integer> {

    DRAFT(10),
    BLOCKED(20),
    ONAPPROVAL(30),
    ACCEPTED(40);

    private Integer id;

    TimecardHeaderStatusEnum(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static TimecardHeaderStatusEnum fromId(Integer id) {
        for (TimecardHeaderStatusEnum at : TimecardHeaderStatusEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}