package kz.uco.tsadv.modules.integration.jsonobject;

import java.io.Serializable;
import java.util.ArrayList;

public class PersonQualificationDataJson implements Serializable {

    protected ArrayList<PersonQualificationJson> personQualifications;

    public ArrayList<PersonQualificationJson> getPersonQualifications() {
        return personQualifications;
    }

    public void setPersonQualifications(ArrayList<PersonQualificationJson> personQualifications) {
        this.personQualifications = personQualifications;
    }
}
