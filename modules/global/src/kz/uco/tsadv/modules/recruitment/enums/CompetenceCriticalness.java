package kz.uco.tsadv.modules.recruitment.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum CompetenceCriticalness implements EnumClass<Integer> {

    CRITICAL(10),
    DESIRABLE(20);

    private Integer id;

    CompetenceCriticalness(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static CompetenceCriticalness fromId(Integer id) {
        for (CompetenceCriticalness at : CompetenceCriticalness.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}