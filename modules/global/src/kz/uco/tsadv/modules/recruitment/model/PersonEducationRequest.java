package kz.uco.tsadv.modules.recruitment.model;

import com.haulmont.chile.core.annotations.NumberFormat;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.base.entity.dictionary.DicEducationType;
import kz.uco.tsadv.modules.learning.dictionary.DicEducationDegree;
import kz.uco.tsadv.modules.learning.dictionary.DicEducationLevel;
import kz.uco.tsadv.modules.learning.dictionary.DicEducationalEstablishment;
import kz.uco.tsadv.modules.personal.dictionary.DicFormStudy;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Table(name = "TSADV_PERSON_EDUCATION_REQUEST")
@Entity(name = "tsadv_PersonEducationRequest")
public class PersonEducationRequest extends AbstractParentEntity {
    private static final long serialVersionUID = 3196391087993243827L;


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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EDUCATIONAL_INSTITUTION_ID")
    private DicEducationalEstablishment educationalEstablishment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EDUCATION_TYPE_ID")
    private DicEducationType educationType;

    @Digits(integer = 4, fraction = 0)
    @Column(name = "START_YEAR")
    @NumberFormat(pattern = "#")
    protected Integer startYear;

    @Digits(integer = 4, fraction = 0)
    @Column(name = "END_YEAR")
    @NumberFormat(pattern = "#")
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

    @Column(name = "FACULTY", length = 2000)
    private String faculty;

    @Column(name = "QUALIFICATION", length = 2000)
    private String qualification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FORM_STUDY_ID")
    private DicFormStudy formStudy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STATUS_ID")
    private DicRequestStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_ID")
    private FileDescriptor file;

    @OrderBy("name")
    @JoinTable(name = "TSADV_PERSON_EDUCATION_REQUEST_FILE_DESCRIPTOR_LINK",
            joinColumns = @JoinColumn(name = "PERSON_EDUCATION_REQUEST_ID"),
            inverseJoinColumns = @JoinColumn(name = "FILE_DESCRIPTOR_ID"))
    @ManyToMany
    private List<FileDescriptor> attachments;

    public List<FileDescriptor> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<FileDescriptor> attachments) {
        this.attachments = attachments;
    }

    public FileDescriptor getFile() {
        return file;
    }

    public void setFile(FileDescriptor file) {
        this.file = file;
    }

    public DicRequestStatus getStatus() {
        return status;
    }

    public void setStatus(DicRequestStatus status) {
        this.status = status;
    }

    public DicFormStudy getFormStudy() {
        return formStudy;
    }

    public void setFormStudy(DicFormStudy formStudy) {
        this.formStudy = formStudy;
    }

    public void setEducationalEstablishment(DicEducationalEstablishment educationalInstitution) {
        this.educationalEstablishment = educationalInstitution;
    }

    public DicEducationalEstablishment getEducationalEstablishment() {
        return educationalEstablishment;
    }

    public DicEducationType getEducationType() {
        return educationType;
    }

    public void setEducationType(DicEducationType educationType) {
        this.educationType = educationType;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

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