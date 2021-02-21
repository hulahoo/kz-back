package kz.uco.tsadv.modules.integration.jsonobject;

import java.io.Serializable;
import java.util.ArrayList;

public class PersonContactDataJson implements Serializable {

    protected ArrayList<PersonContactJson> personContacts;

    public ArrayList<PersonContactJson> getPersonContacts() {
        return personContacts;
    }

    public void setPersonContacts(ArrayList<PersonContactJson> personContacts) {
        this.personContacts = personContacts;
    }
}
