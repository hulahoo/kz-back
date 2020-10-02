package kz.uco.tsadv.api;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;

import java.util.UUID;

/**
 * Образование (Интеграционный объект)
 */
@MetaClass(name = "tsadv$PersonEducationInt")
public class PersonEducationInt extends AbstractEntityInt {
    private static final long serialVersionUID = -7753338254531569330L;

    /**
     * Наименование Учебного Заведения
     */
    @MetaProperty
    protected String school;

    /**
     * Год начала обучения
     */
    @MetaProperty
    protected Integer startYear;

    /**
     * Год окончания обучения
     */
    @MetaProperty
    protected Integer endYear;

    /**
     * Специальность
     */
    @MetaProperty
    protected String specialization;

    /**
     * Id уровня образования
     */
    @MetaProperty
    protected UUID level;

    /**
     * Уровень образования
     */
    @MetaProperty
    protected String levelName;

    /**
     * Id Степени
     */
    @MetaProperty
    protected UUID degree;

    /**
     * Степень
     */
    @MetaProperty
    protected String degreeName;

    /**
     * Адрес учебного заведения
     */
    @MetaProperty
    protected String location;

    /**
     * Участник программы Болашак (true / false)
     */
    @MetaProperty
    protected Boolean bolashak;



    public UUID getLevel() {
        return level;
    }

    public void setLevel(UUID level) {
        this.level = level;
    }


    public UUID getDegree() {
        return degree;
    }

    public void setDegree(UUID degree) {
        this.degree = degree;
    }



    public void setSchool(String school) {
        this.school = school;
    }

    public String getSchool() {
        return school;
    }

    public void setStartYear(Integer startYear) {
        this.startYear = startYear;
    }

    public Integer getStartYear() {
        return startYear;
    }

    public void setEndYear(Integer endYear) {
        this.endYear = endYear;
    }

    public Integer getEndYear() {
        return endYear;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setDegreeName(String degreeName) {
        this.degreeName = degreeName;
    }

    public String getDegreeName() {
        return degreeName;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public Boolean getBolashak() {
        return bolashak;
    }

    public void setBolashak(Boolean bolashak) {
        this.bolashak = bolashak;
    }
}