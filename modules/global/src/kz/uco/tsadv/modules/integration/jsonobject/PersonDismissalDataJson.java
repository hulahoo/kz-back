package kz.uco.tsadv.modules.integration.jsonobject;

import java.io.Serializable;
import java.util.ArrayList;

public class PersonDismissalDataJson implements Serializable {

    protected ArrayList<PersonDismissalJson> personDismissals;

    public ArrayList<PersonDismissalJson> getPersonDismissals() {
        return personDismissals;
    }

    public void setPersonDismissals(ArrayList<PersonDismissalJson> personDismissals) { this.personDismissals = personDismissals; }
}
