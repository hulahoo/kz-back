package kz.uco.tsadv.modules.integration.jsonobject;

import java.io.Serializable;
import java.util.ArrayList;

public class PersonDocumentDataJson implements Serializable {

    protected ArrayList<PersonDocumentJson> personDocuments;

    public ArrayList<PersonDocumentJson> getPersonDocuments() {
        return personDocuments;
    }

    public void setPersonDocuments(ArrayList<PersonDocumentJson> personDocuments) {
        this.personDocuments = personDocuments;
    }
}
