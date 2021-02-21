package kz.uco.tsadv.modules.integration.jsonobject;

import java.io.Serializable;
import java.util.ArrayList;

public class PersonEducationDataJson implements Serializable {

    protected ArrayList<PersonEducationJson> personEducations;

    public ArrayList<PersonEducationJson> getPersonEducations() {
        return personEducations;
    }

    public void setPersonEducations(ArrayList<PersonEducationJson> personEducations) {
        this.personEducations = personEducations;
    }
}
