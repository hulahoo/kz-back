package kz.uco.tsadv.modules.recruitment.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum InterviewStatus implements EnumClass<Integer> {

    DRAFT(10),
    ON_APPROVAL(20),
    PLANNED(30),
    COMPLETED(40),
    CANCELLED(50),
    FAILED(60);

    private Integer id;

    InterviewStatus(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static InterviewStatus fromId(Integer id) {
        for (InterviewStatus at : InterviewStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}