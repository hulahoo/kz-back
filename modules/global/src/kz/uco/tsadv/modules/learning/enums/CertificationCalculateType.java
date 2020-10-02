package kz.uco.tsadv.modules.learning.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum CertificationCalculateType implements EnumClass<Integer> {

    FIRST_ATTEMPT(1),
    LAST_DATE(2);

    private Integer id;

    CertificationCalculateType(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static CertificationCalculateType fromId(Integer id) {
        for (CertificationCalculateType at : CertificationCalculateType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}