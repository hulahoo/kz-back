package kz.uco.tsadv.modules.personal.enums;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum CompetenceAssessmentStatus implements EnumClass<String> {

    DRAFT("DRAFT"),
    ONAPPROVAL("ONAPPROVAL"),
    APPROVED("APPROVED");

    private String id;

    CompetenceAssessmentStatus(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static CompetenceAssessmentStatus fromId(String id) {
        for (CompetenceAssessmentStatus at : CompetenceAssessmentStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}