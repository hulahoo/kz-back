package kz.uco.tsadv.modules.recruitment.model;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import kz.uco.tsadv.modules.learning.dictionary.DicEducationLevel;

import javax.validation.constraints.DecimalMin;

@MetaClass(name = "tsadv$RequisitionSearchCandidate")
public class RequisitionSearchCandidate extends BaseUuidEntity {
    private static final long serialVersionUID = 4531434593700208173L;

    @MetaProperty
    protected Boolean reserve;

    @MetaProperty
    protected Boolean employee;

    @MetaProperty
    protected Boolean student;

    @MetaProperty
    protected Boolean externalCandidate;

    @DecimalMin("0")
    @MetaProperty
    protected Double experience;

    @MetaProperty
    protected DicEducationLevel levelEducation;

    @MetaProperty
    protected Boolean readRelocation;

    @MetaProperty
    protected Boolean reservedCandidate;

    public void setReservedCandidate(Boolean reservedCandidate) {
        this.reservedCandidate = reservedCandidate;
    }

    public Boolean getReservedCandidate() {
        return reservedCandidate;
    }


    public DicEducationLevel getLevelEducation() {
        return levelEducation;
    }

    public void setLevelEducation(DicEducationLevel levelEducation) {
        this.levelEducation = levelEducation;
    }


    public Double getExperience() {
        return experience;
    }

    public void setExperience(Double experience) {
        this.experience = experience;
    }


    public void setReserve(Boolean reserve) {
        this.reserve = reserve;
    }

    public Boolean getReserve() {
        return reserve;
    }


    public void setEmployee(Boolean employee) {
        this.employee = employee;
    }

    public Boolean getEmployee() {
        return employee;
    }

    public void setStudent(Boolean student) {
        this.student = student;
    }

    public Boolean getStudent() {
        return student;
    }

    public void setExternalCandidate(Boolean externalCandidate) {
        this.externalCandidate = externalCandidate;
    }

    public Boolean getExternalCandidate() {
        return externalCandidate;
    }

    public void setReadRelocation(Boolean readRelocation) {
        this.readRelocation = readRelocation;
    }

    public Boolean getReadRelocation() {
        return readRelocation;
    }


}