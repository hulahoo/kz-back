package kz.uco.tsadv.modules.timesheet.model;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;
import kz.uco.tsadv.modules.timesheet.enums.MaterialDesignColorsEnum;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Listeners("tsadv_AssignmentScheduleListener")
@NamePattern("%s|id")
@Table(name = "TSADV_ASSIGNMENT_SCHEDULE")
@Entity(name = "tsadv$AssignmentSchedule")
public class AssignmentSchedule extends AbstractParentEntity {
    private static final long serialVersionUID = 2301325069989521412L;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ASSIGNMENT_GROUP_ID")
    protected AssignmentGroupExt assignmentGroup;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SCHEDULE_ID")
    protected kz.uco.tsadv.modules.timesheet.model.StandardSchedule schedule;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "OFFSET_ID")
    protected StandardOffset offset;

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE", nullable = false)
    protected Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE", nullable = false)
    protected Date endDate;

    @NotNull
    @Column(name = "COLORS_SET", nullable = false)
    protected Integer colorsSet;

    @MetaProperty
    @Transient
    String name;

    public void setAssignmentGroup(AssignmentGroupExt assignmentGroup) {
        this.assignmentGroup = assignmentGroup;
    }

    public AssignmentGroupExt getAssignmentGroup() {
        return assignmentGroup;
    }

    public void setSchedule(kz.uco.tsadv.modules.timesheet.model.StandardSchedule schedule) {
        this.schedule = schedule;
    }

    public StandardSchedule getSchedule() {
        return schedule;
    }

    public void setOffset(StandardOffset offset) {
        this.offset = offset;
    }

    public StandardOffset getOffset() {
        return offset;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public MaterialDesignColorsEnum getColorsSet() {
        return colorsSet == null ? null : MaterialDesignColorsEnum.fromId(colorsSet);
    }

    public void setColorsSet(MaterialDesignColorsEnum colorsSet) {
        this.colorsSet = colorsSet == null ? null : colorsSet.getId();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}