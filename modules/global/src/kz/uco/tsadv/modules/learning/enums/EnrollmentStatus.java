package kz.uco.tsadv.modules.learning.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum EnrollmentStatus implements EnumClass<Integer> {

    REQUEST(1),
    WAITLIST(2),
    APPROVED(3),
    CANCELLED(4),
    COMPLETED(5),
    REQUIRED_SETTING(6);

    private Integer id;

    EnrollmentStatus(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static EnrollmentStatus fromId(Integer id) {
        for (EnrollmentStatus at : EnrollmentStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}