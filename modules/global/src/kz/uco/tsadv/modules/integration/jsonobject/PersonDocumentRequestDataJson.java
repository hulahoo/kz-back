package kz.uco.tsadv.modules.integration.jsonobject;

import java.io.Serializable;
import java.util.ArrayList;

public class PersonDocumentRequestDataJson implements Serializable {

    protected ArrayList<PersonDocumentRequestJson> personDocumentRequestJsons;

    public ArrayList<PersonDocumentRequestJson> getPersonDocumentRequestJsons() {
        return personDocumentRequestJsons;
    }

    public void setPersonDocumentRequestJsons(ArrayList<PersonDocumentRequestJson> personDocumentRequestJsons) {
        this.personDocumentRequestJsons = personDocumentRequestJsons;
    }
}
