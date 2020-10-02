package kz.uco.tsadv.modules.performance.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum BudgetHeaderStatus implements EnumClass<String> {

    DRAFT("DRAFT"),
    ON_AGREEMENT("ON_AGREEMENT"),
    APPROVED("APPROVED"),
    OBSOLETE("OBSOLETE"),
    AGREED("AGREED"),
    ON_APPROVAL("ON_APPROVAL");

    private String id;

    BudgetHeaderStatus(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static BudgetHeaderStatus fromId(String id) {
        for (BudgetHeaderStatus at : BudgetHeaderStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}