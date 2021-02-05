package kz.uco.tsadv.modules.integration.jsonobject;

import java.io.Serializable;
import java.util.ArrayList;

public class PersonDataJson implements Serializable {
    protected ArrayList<PersonJson> persons;

    public ArrayList<PersonJson> getPersons() {
        return persons;
    }

    public void setPersons(ArrayList<PersonJson> persons) {
        this.persons = persons;
    }
}
