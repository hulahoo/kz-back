package kz.uco.tsadv.modules.learning.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum CertificationStatus implements EnumClass<Integer> {

    ACTIVE(1),
    PAST(2);

    private Integer id;

    CertificationStatus(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static CertificationStatus fromId(Integer id) {
        for (CertificationStatus at : CertificationStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}