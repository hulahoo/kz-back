package kz.uco.tsadv.global.entity;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import kz.uco.tsadv.global.entity.*;
import kz.uco.tsadv.global.entity.AbsenceChartEntity;

import java.util.List;
import java.util.UUID;

@MetaClass(name = "tsadv$AssignmentAbsenceChartEntity")
public class AssignmentAbsenceChartEntity extends BaseUuidEntity {
    private static final long serialVersionUID = 869905776734598388L;

    @MetaProperty
    protected String personFullName;

    @MetaProperty
    protected List<kz.uco.tsadv.global.entity.AbsenceChartEntity> absences;

    @MetaProperty
    protected UUID assignmentGroupId;

    public void setAssignmentGroupId(UUID assignmentGroupId) {
        this.assignmentGroupId = assignmentGroupId;
    }

    public UUID getAssignmentGroupId() {
        return assignmentGroupId;
    }


    public void setAbsences(List<kz.uco.tsadv.global.entity.AbsenceChartEntity> absences) {
        this.absences = absences;
    }

    public List<AbsenceChartEntity> getAbsences() {
        return absences;
    }


    public void setPersonFullName(String personFullName) {
        this.personFullName = personFullName;
    }

    public String getPersonFullName() {
        return personFullName;
    }


}