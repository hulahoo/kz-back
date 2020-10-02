package kz.uco.tsadv.modules.recruitment.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum HS_Periods implements EnumClass<Integer> {

    DAY(10),
    WEEK(20),
    QUARTER(30),
    MONTH(40),
    HALF_YEAR(50),
    YEAR(60);

    private Integer id;

    HS_Periods(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static HS_Periods fromId(Integer id) {
        for (HS_Periods at : HS_Periods.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}