package kz.uco.tsadv.modules.recruitment.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum RequisitionType implements EnumClass<Integer> {

    STANDARD(1),
    TEMPLATE(2);

    private Integer id;

    RequisitionType(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static RequisitionType fromId(Integer id) {
        for (RequisitionType at : RequisitionType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}