package kz.uco.tsadv.modules.recruitment.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum JobRequestStatus implements EnumClass<Integer> {

    DRAFT(0),
    ON_APPROVAL(1),
    REJECTED(2),
    INTERVIEW(3),
    MADE_OFFER(4),
    HIRED(5),
    SELECTED(6),
    FROM_RESERVE(7);

    private Integer id;

    JobRequestStatus(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static JobRequestStatus fromId(Integer id) {
        for (JobRequestStatus at : JobRequestStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}