package kz.uco.tsadv.modules.performance.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum BudgetRequestItemEnum implements EnumClass<String> {

    PERSON_COUNT("PERSON_COUNT"),
    BUS_TRIP_COUNT("BUS_TRIP_COUNT"),
    BUS_TRIP_AMOUNT("BUS_TRIP_AMOUNT"),
    ALL_AMOUNT("ALL_AMOUNT");

    private String id;

    BudgetRequestItemEnum(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static BudgetRequestItemEnum fromId(String id) {
        for (BudgetRequestItemEnum at : BudgetRequestItemEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}