package kz.uco.tsadv.modules.integration.jsonobject;

import java.io.Serializable;
import java.util.ArrayList;

public class BeneficiaryDataJson implements Serializable {

    protected ArrayList<BeneficiaryJson> beneficiaries;

    public ArrayList<BeneficiaryJson> getBeneficiaries() {
        return beneficiaries;
    }

    public void setBeneficiaries(ArrayList<BeneficiaryJson> beneficiaries) {
        this.beneficiaries = beneficiaries;
    }
}
