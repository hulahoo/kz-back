package kz.uco.tsadv.modules.learning.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;

/**
 * @author adilbekov.yernar
 */
public enum Months implements EnumClass<Integer> {

    JANUARY(1),
    FEBRUARY(2),
    MARCH(3),
    APRIL(4),
    MAY(5),
    JUNE(6),
    JULY(7),
    AUGUST(8),
    SEPTEMBER(9),
    OCTOBER(10),
    NOVEMBER(11),
    DECEMBER(12);

    private Integer id;

    Months(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static Months fromId(Integer id) {
        for (Months at : Months.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}