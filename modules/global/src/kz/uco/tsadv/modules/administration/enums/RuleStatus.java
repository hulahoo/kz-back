package kz.uco.tsadv.modules.administration.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum RuleStatus implements EnumClass<Integer> {

    DISABLE(10),
    WARNING(20),
    ERROR(30);

    private Integer id;

    RuleStatus(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static RuleStatus fromId(Integer id) {
        for (RuleStatus at : RuleStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}