package kz.uco.tsadv.modules.timesheet.model;

import com.haulmont.cuba.core.entity.annotation.Listeners;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Listeners("tsadv_TimecardDeviationListener")
@Table(name = "TSADV_TIMECARD_DEVIATION")
@Entity(name = "tsadv$TimecardDeviation")
public class TimecardDeviation extends AbstractParentEntity {
    private static final long serialVersionUID = 5110419613826162562L;


    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ASSIGNMENT_GROUP_ID")
    protected AssignmentGroupExt assignmentGroup;

    @NotNull
    @Column(name = "HOURS", nullable = false)
    protected Double hours;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "DATE_FROM", nullable = false)
    protected Date dateFrom;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "DATE_TO", nullable = false)
    protected Date dateTo;

    @NotNull
    @Column(name = "IS_CHANGES_FACT_HOURS", nullable = false)
    protected Boolean isChangesFactHours = false;

    @NotNull
    @Column(name = "IS_CHANGES_PLAN_HOURS", nullable = false)
    protected Boolean isChangesPlanHours = false;

    @NotNull
    @Column(name = "IS_CHANGES_DETAILS_FROM_BEGIN", nullable = false)
    protected Boolean isChangesDetailsFromBegin = false;

    @NotNull
    @Column(name = "CHANGES_WEEKENDS", nullable = false)
    protected Boolean changesWeekends = false;

    public void setAssignmentGroup(AssignmentGroupExt assignmentGroup) {
        this.assignmentGroup = assignmentGroup;
    }

    public AssignmentGroupExt getAssignmentGroup() {
        return assignmentGroup;
    }

    public Boolean getIsChangesDetailsFromBegin() {
        return isChangesDetailsFromBegin;
    }

    public void setIsChangesDetailsFromBegin(Boolean isChangesDetailsFromBegin) {
        this.isChangesDetailsFromBegin = isChangesDetailsFromBegin;
    }


    public Double getHours() {
        return hours;
    }

    public void setHours(Double hours) {
        this.hours = hours;
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

    public void setIsChangesFactHours(Boolean isChangesFactHours) {
        this.isChangesFactHours = isChangesFactHours;
    }

    public Boolean getIsChangesFactHours() {
        return isChangesFactHours;
    }

    public void setIsChangesPlanHours(Boolean isChangesPlanHours) {
        this.isChangesPlanHours = isChangesPlanHours;
    }

    public Boolean getIsChangesPlanHours() {
        return isChangesPlanHours;
    }

    public Boolean getChangesWeekends() {
        return changesWeekends;
    }

    public void setChangesWeekends(Boolean changesWeekends) {
        this.changesWeekends = changesWeekends;
    }
}