package kz.uco.tsadv.modules.integration.jsonobject;

import java.io.Serializable;
import java.util.ArrayList;

public class AssignmentScheduleJsonData implements Serializable {
    protected ArrayList<AssignmentScheduleJson> assignmentSchedules;

    public ArrayList<AssignmentScheduleJson> getAssignmentSchedules() {
        return assignmentSchedules;
    }

    public void setAssignmentSchedules(ArrayList<AssignmentScheduleJson> assignmentSchedules) {
        this.assignmentSchedules = assignmentSchedules;
    }

}
