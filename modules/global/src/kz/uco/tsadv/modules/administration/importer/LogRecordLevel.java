package kz.uco.tsadv.modules.administration.importer;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;

public enum LogRecordLevel implements EnumClass<String> {

    ERROR("error"),
    WARN("warn"),
    DEBUG("debug"),
    INFO("info");

    private String id;

    LogRecordLevel(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static kz.uco.tsadv.modules.administration.importer.LogRecordLevel fromId(String id) {
        for (kz.uco.tsadv.modules.administration.importer.LogRecordLevel at : kz.uco.tsadv.modules.administration.importer.LogRecordLevel.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}