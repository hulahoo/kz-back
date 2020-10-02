package kz.uco.tsadv.modules.recruitment.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum OfferStatus implements EnumClass<Integer> {

    DRAFT(10),
    ONAPPROVAL(20),
    APPROVED(30),
    ACCEPTED(40),
    REJECTED(50);

    private Integer id;

    OfferStatus(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static OfferStatus fromId(Integer id) {
        for (OfferStatus at : OfferStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}