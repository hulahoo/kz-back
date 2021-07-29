package kz.uco.tsadv.modules.integration.jsonobject;

import java.io.Serializable;
import java.util.ArrayList;

public class AddressRequestDataJson implements Serializable {

    protected ArrayList<AddressRequestJson> addressRequestJsons;

    public ArrayList<AddressRequestJson> getAddressRequestJsons() {
        return addressRequestJsons;
    }

    public void setAddressRequestJsons(ArrayList<AddressRequestJson> addressRequestJsons) {
        this.addressRequestJsons = addressRequestJsons;
    }
}
