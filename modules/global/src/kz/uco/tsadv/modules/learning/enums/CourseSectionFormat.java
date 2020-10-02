package kz.uco.tsadv.modules.learning.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum CourseSectionFormat implements EnumClass<Integer> {

    ONLINE(1),
    OFFLINE(2),
    WEBINAR(3);

    private Integer id;

    CourseSectionFormat(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static CourseSectionFormat fromId(Integer id) {
        for (CourseSectionFormat at : CourseSectionFormat.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}