package kz.uco.tsadv.modules.learning.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum TestSectionOrder implements EnumClass<Integer> {

    FIX(1),
    RANDOM(2);

    private Integer id;

    TestSectionOrder(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static TestSectionOrder fromId(Integer id) {
        for (TestSectionOrder at : TestSectionOrder.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}