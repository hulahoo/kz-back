package kz.uco.tsadv.modules.learning.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum KmsTestPurpose implements EnumClass<String> {

    ATTESTATION("ATTESTATION"),
    PROF_KNOWLEDGE("PROF_KNOWLEDGE"),
    RECRUITING("RECRUITING");

    private String id;

    KmsTestPurpose(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static KmsTestPurpose fromId(String id) {
        for (KmsTestPurpose at : KmsTestPurpose.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}