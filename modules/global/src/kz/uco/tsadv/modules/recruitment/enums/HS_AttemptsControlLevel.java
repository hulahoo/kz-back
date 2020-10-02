package kz.uco.tsadv.modules.recruitment.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum HS_AttemptsControlLevel implements EnumClass<Integer> {

    CANDIDATE(10),
    VACANCY(20);

    private Integer id;

    HS_AttemptsControlLevel(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static HS_AttemptsControlLevel fromId(Integer id) {
        for (HS_AttemptsControlLevel at : HS_AttemptsControlLevel.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}