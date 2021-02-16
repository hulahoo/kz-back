package kz.uco.tsadv.modules.integration.jsonobject;

import java.io.Serializable;
import java.util.ArrayList;

public class AbsenceDataJson implements Serializable {

    protected ArrayList<AbsenceJson> absences;

    public ArrayList<AbsenceJson> getAbsences() {
        return absences;
    }

    public void setAbsences(ArrayList<AbsenceJson> absences) {
        this.absences = absences;
    }
}
