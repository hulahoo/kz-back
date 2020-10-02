package kz.uco.tsadv.global.entity;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import java.util.Date;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import kz.uco.tsadv.global.entity.AssignmentAbsenceChartEntity;

@MetaClass(name = "tsadv$AbsenceChartEntity")
public class AbsenceChartEntity extends BaseUuidEntity {
    private static final long serialVersionUID = -7492956793653487771L;

    @MetaProperty
    protected Date dateFrom;

    @MetaProperty
    protected Date dateTo;

    @MetaProperty
    protected Integer absenceDays;

    @MetaProperty
    protected String color;

    @MetaProperty
    protected Integer index;

    @MetaProperty
    protected String absenceType;

    @MetaProperty
    protected AssignmentAbsenceChartEntity assignmentAbsence;

    public void setAssignmentAbsence(AssignmentAbsenceChartEntity assignmentAbsence) {
        this.assignmentAbsence = assignmentAbsence;
    }

    public AssignmentAbsenceChartEntity getAssignmentAbsence() {
        return assignmentAbsence;
    }


    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setAbsenceDays(Integer absenceDays) {
        this.absenceDays = absenceDays;
    }

    public Integer getAbsenceDays() {
        return absenceDays;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer getIndex() {
        return index;
    }

    public void setAbsenceType(String absenceType) {
        this.absenceType = absenceType;
    }

    public String getAbsenceType() {
        return absenceType;
    }


}