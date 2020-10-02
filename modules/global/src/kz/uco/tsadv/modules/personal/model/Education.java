package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import kz.uco.tsadv.modules.learning.dictionary.DicEducationType;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;

@NamePattern("%s %s %s %s %s %s %s %s|educationType,startYear,endYear,faculty,person,qualification,scholl,specialization")
@Table(name = "TSADV_EDUCATION")
@Entity(name = "tsadv$Education")
public class Education extends AbstractParentEntity {
    private static final long serialVersionUID = 293959493822995191L;

    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "clear", "open"})
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_ID")
    protected PersonGroupExt person;

    @Column(name = "SCHOLL", nullable = false, length = 500)
    protected String scholl;

    @Column(name = "FACULTY", length = 500)
    protected String faculty;

    @Column(name = "START_YEAR")
    protected Integer startYear;

    @Column(name = "END_YEAR")
    protected Integer endYear;

    @Column(name = "SPECIALIZATION", length = 500)
    protected String specialization;

    @Column(name = "QUALIFICATION", length = 500)
    protected String qualification;


    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "clear", "open"})
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "EDUCATION_TYPE_ID")
    protected DicEducationType educationType;

    public PersonGroupExt getPerson() {
        return person;
    }

    public void setPerson(PersonGroupExt person) {
        this.person = person;
    }


    public void setEducationType(DicEducationType educationType) {
        this.educationType = educationType;
    }

    public DicEducationType getEducationType() {
        return educationType;
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


    public void setScholl(String scholl) {
        this.scholl = scholl;
    }

    public String getScholl() {
        return scholl;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getQualification() {
        return qualification;
    }


}