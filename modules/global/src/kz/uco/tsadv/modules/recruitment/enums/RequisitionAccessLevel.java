package kz.uco.tsadv.modules.recruitment.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum RequisitionAccessLevel implements EnumClass<Integer> {

    READ(1),
    EDIT(2);

    private Integer id;

    RequisitionAccessLevel(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static RequisitionAccessLevel fromId(Integer id) {
        for (RequisitionAccessLevel at : RequisitionAccessLevel.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}