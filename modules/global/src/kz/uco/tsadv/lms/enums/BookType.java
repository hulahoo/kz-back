package kz.uco.tsadv.lms.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;

public enum BookType implements EnumClass<String> {
    PDF("PDF"),
    DJVU("DJVU");

    private String id;

    BookType(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static BookType fromId(String id) {
        for (BookType at : BookType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}
