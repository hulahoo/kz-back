package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum SurChargeType implements EnumClass<Integer> {

    AMOUNT(1),
    PERCENT(2);

    private Integer id;

    SurChargeType(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static kz.uco.tsadv.modules.personal.model.SurChargeType fromId(Integer id) {
        for (kz.uco.tsadv.modules.personal.model.SurChargeType at : kz.uco.tsadv.modules.personal.model.SurChargeType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}