package kz.uco.tsadv.modules.administration.importer;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum ImportLogLevel implements EnumClass<String> {
    LOG("LOG"),
    ERROR("ERROR"),
    SUCCESS("SUCCESS");
    private String id;

    ImportLogLevel(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static ImportLogLevel fromId(String id) {
        for (ImportLogLevel at : ImportLogLevel.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}