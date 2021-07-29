package kz.uco.tsadv.modules.integration.jsonobject;

import java.io.Serializable;
import java.util.ArrayList;

public class PersonalDataRequestDataJson implements Serializable {

    protected ArrayList<PersonalDataRequestJson> personalDataRequestJsons;

    public ArrayList<PersonalDataRequestJson> getPersonalDataRequestJsons() {
        return personalDataRequestJsons;
    }

    public void setPersonalDataRequestJsons(ArrayList<PersonalDataRequestJson> personalDataRequestJsons) {
        this.personalDataRequestJsons = personalDataRequestJsons;
    }
}
