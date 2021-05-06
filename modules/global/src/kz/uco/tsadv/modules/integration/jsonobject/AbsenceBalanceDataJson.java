package kz.uco.tsadv.modules.integration.jsonobject;

import java.io.Serializable;
import java.util.ArrayList;

public class AbsenceBalanceDataJson implements Serializable {

    protected ArrayList<AbsenceBalanceJson> absenceBalances;

    public ArrayList<AbsenceBalanceJson> getAbsenceBalances() {
        return absenceBalances;
    }

    public void setAbsenceBalances(ArrayList<AbsenceBalanceJson> absenceBalances) {
        this.absenceBalances = absenceBalances;
    }
}