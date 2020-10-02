package kz.uco.tsadv.global.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;

/**
 * Status of completion of some operation, action, or process
 * @author Felix Kamalov
 * @version 1.0
 */
public enum CompletionStatus implements EnumClass<String> {
    SUCCESS("SUCCESS"),
    WARNING("WARNING"),
    ERROR("ERROR");

    private String id;

    CompletionStatus(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static CompletionStatus fromId(String id) {
        for (CompletionStatus at : CompletionStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}