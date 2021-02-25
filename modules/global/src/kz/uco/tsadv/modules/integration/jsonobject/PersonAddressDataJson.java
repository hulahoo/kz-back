package kz.uco.tsadv.modules.integration.jsonobject;

import java.io.Serializable;
import java.util.ArrayList;

public class PersonAddressDataJson implements Serializable {
    protected ArrayList<PersonAddressJson> personAddresses;
    public ArrayList<PersonAddressJson> getPersonAddresses() {
        return personAddresses;
    }

    public void setPersonAddresses(ArrayList<PersonAddressJson> personAddresses) {
        this.personAddresses = personAddresses;
    }
}
