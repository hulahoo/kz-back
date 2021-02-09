package kz.uco.tsadv.modules.integration.jsonobject;

import java.io.Serializable;
import java.util.ArrayList;

public class AssignmentDataJson implements Serializable {

    protected ArrayList<AssignmentJson> assignmentJsons;

    public ArrayList<AssignmentJson> getAssignmentJsons() {
        return assignmentJsons;
    }

    public void setAssignmentJsons(ArrayList<AssignmentJson> assignmentJsons) {
        this.assignmentJsons = assignmentJsons;
    }

}