package kz.uco.tsadv.api;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;

import java.util.UUID;

/**
 * Навык (Интеграционный объект)
 */
@MetaClass(name = "tsadv$PersonCompetenceInt")
public class PersonCompetenceInt extends AbstractEntityInt {
    private static final long serialVersionUID = 2124184444809201353L;

    /**
     * Id Навыка
     */
    @MetaProperty
    protected UUID competence;

    /**
     * Навык
     */
    @MetaProperty
    protected String competenceName;

    /**
     * Id уровня владения навыком
     */
    @MetaProperty
    protected UUID scaleLevel;

    /**
     * Уровень владения навыком
     */
    @MetaProperty
    protected String scaleLevelName;

    /**
     * Код типа навыка
     */
    @MetaProperty
    protected String competenceTypeCode;



    public void setCompetence(UUID competence) {
        this.competence = competence;
    }

    public UUID getCompetence() {
        return competence;
    }

    public void setCompetenceName(String competenceName) {
        this.competenceName = competenceName;
    }

    public String getCompetenceName() {
        return competenceName;
    }

    public void setScaleLevel(UUID scaleLevel) {
        this.scaleLevel = scaleLevel;
    }

    public UUID getScaleLevel() {
        return scaleLevel;
    }

    public void setScaleLevelName(String scaleLevelName) {
        this.scaleLevelName = scaleLevelName;
    }

    public String getScaleLevelName() {
        return scaleLevelName;
    }

    public String getCompetenceTypeCode() {
        return competenceTypeCode;
    }

    public void setCompetenceTypeCode(String competenceTypeCode) {
        this.competenceTypeCode = competenceTypeCode;
    }
}