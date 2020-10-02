package kz.uco.tsadv.modules.recruitment.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum RequisitionStatus implements EnumClass<Integer> {

    OPEN(1),
    CANCELED(2),
    CLOSED(3),
    ON_APPROVAL(4),
    CLOSED_MANUALLY(5),
    FINISH_COLLECT(6),
    DRAFT(7);

    private Integer id;

    RequisitionStatus(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static RequisitionStatus fromId(Integer id) {
        for (RequisitionStatus at : RequisitionStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}