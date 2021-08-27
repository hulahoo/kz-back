package kz.uco.tsadv.modules.integration.jsonobject;

import java.io.Serializable;
import java.util.ArrayList;

public class PersonPayslipDataJson implements Serializable {

    protected ArrayList<PersonPayslipJson> personPayslipJsons;

    public ArrayList<PersonPayslipJson> getPersonPayslipJsons() {
        return personPayslipJsons;
    }

    public void setPersonPayslipJsons(ArrayList<PersonPayslipJson> personPayslipJsons) {
        this.personPayslipJsons = personPayslipJsons;
    }
}
