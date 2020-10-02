package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum SurChargePeriod implements EnumClass<Integer> {

    PERIODIC(1),
    ONEOFF(2);

    private Integer id;

    SurChargePeriod(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static kz.uco.tsadv.modules.personal.model.SurChargePeriod fromId(Integer id) {
        for (kz.uco.tsadv.modules.personal.model.SurChargePeriod at : kz.uco.tsadv.modules.personal.model.SurChargePeriod.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}