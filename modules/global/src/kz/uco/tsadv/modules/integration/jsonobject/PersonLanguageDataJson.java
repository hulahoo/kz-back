package kz.uco.tsadv.modules.integration.jsonobject;

import java.io.Serializable;
import java.util.ArrayList;

public class PersonLanguageDataJson implements Serializable {
    protected ArrayList<PersonLanguageJson> personLanguages;

    public ArrayList<PersonLanguageJson> getPersonLanguages() {
        return personLanguages;
    }

    public void setPersonLanguages(ArrayList<PersonLanguageJson> personLanguages) {
        this.personLanguages = personLanguages;
    }
}
