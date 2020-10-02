package kz.uco.tsadv.modules.recruitment.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.chile.core.annotations.NumberFormat;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.tsadv.modules.learning.dictionary.DicEducationDegree;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.learning.dictionary.DicEducationLevel;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import java.util.Date;
import javax.validation.constraints.NotNull;

@NamePattern("%s (%s-%s)|school,startYear,endYear")
@Table(name = "TSADV_PERSON_EDUCATION")
@Entity(name = "tsadv$PersonEducation")
public class PersonEducation extends AbstractParentEntity {
    private static final long serialVersionUID = -4257545074051965436L;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @Column(name = "DIPLOMA_NUMBER")
    protected String diplomaNumber;

    @Temporal(TemporalType.DATE)
    @Column(name = "GRADUATION_DATE")
    protected Date graduationDate;

    @NotNull
    @Column(name = "FOREIGN_EDUCATION", nullable = false)
    protected Boolean foreignEducation = false;

    @Column(name = "SCHOOL")
    protected String school;

    @Digits(integer = 4, fraction = 0)
    @Column(name = "START_YEAR")
    @NumberFormat(pattern = "#", decimalSeparator = "", groupingSeparator = "")
    protected Integer startYear;

    @Digits(integer = 4, fraction = 0)
    @Column(name = "END_YEAR")
    @NumberFormat(pattern = "#", decimalSeparator = "", groupingSeparator = "")
    protected Integer endYear;

    @Column(name = "SPECIALIZATION")
    protected String specialization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEGREE_ID")
    protected DicEducationDegree degree;

    @Column(name = "LOCATION")
    protected String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LEVEL_ID")
    protected DicEducationLevel level;

    public void setDiplomaNumber(String diplomaNumber) {
        this.diplomaNumber = diplomaNumber;
    }

    public String getDiplomaNumber() {
        return diplomaNumber;
    }

    public void setGraduationDate(Date graduationDate) {
        this.graduationDate = graduationDate;
    }

    public Date getGraduationDate() {
        return graduationDate;
    }

    public void setForeignEducation(Boolean foreignEducation) {
        this.foreignEducation = foreignEducation;
    }

    public Boolean getForeignEducation() {
        return foreignEducation;
    }


    public void setLevel(DicEducationLevel level) {
        this.level = level;
    }

    public DicEducationLevel getLevel() {
        return level;
    }


    public DicEducationDegree getDegree() {
        return degree;
    }

    public void setDegree(DicEducationDegree degree) {
        this.degree = degree;
    }


    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
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

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }


}