package kz.uco.tsadv.modules.timesheet.model;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

@MetaClass(name = "tsadv$AssignmentScheduleWithObjectsDto")
public class AssignmentScheduleWithObjectsDto extends BaseUuidEntity {
    private static final long serialVersionUID = 6531161296797769052L;

    @MetaProperty
    protected AssignmentSchedule assignmentSchedule;

    protected Object object;

    public void setAssignmentSchedule(AssignmentSchedule assignmentSchedule) {
        this.assignmentSchedule = assignmentSchedule;
    }

    public AssignmentSchedule getAssignmentSchedule() {
        return assignmentSchedule;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}