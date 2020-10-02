package kz.uco.tsadv.entity.videoplay.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum VideoFileConvertStatus implements EnumClass<String> {

    TO_CONVERT("TO_CONVERT"),
    CONVERTED_SUCCESS("CONVERTED_SUCCESS"),
    CONVERTED_ERROR("CONVERTED_ERROR");

    private String id;

    VideoFileConvertStatus(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static VideoFileConvertStatus fromId(String id) {
        for (VideoFileConvertStatus at : VideoFileConvertStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}