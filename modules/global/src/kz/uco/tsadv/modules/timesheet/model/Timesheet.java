package kz.uco.tsadv.modules.timesheet.model;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;

import java.util.List;
import java.util.UUID;

@NamePattern("%s|id")
@MetaClass(name = "tsadv$PositionAssignmentSchedule")
public class Timesheet extends BaseUuidEntity {
    private static final long serialVersionUID = -6509173293236074142L;

    @MetaProperty
    protected String name;

    @MetaProperty
    protected AssignmentGroupExt assignmentGroup;

    @MetaProperty
    protected List<AssignmentSchedule> assignmentSchedules;

    public AssignmentGroupExt getAssignmentGroup() {
        return assignmentGroup;
    }

    public void setAssignmentGroup(AssignmentGroupExt assignmentGroup) {
        this.assignmentGroup = assignmentGroup;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AssignmentSchedule> getAssignmentSchedules() {
        return assignmentSchedules;
    }

    public void setAssignmentSchedules(List<AssignmentSchedule> assignmentSchedules) {
        this.assignmentSchedules = assignmentSchedules;
    }
}