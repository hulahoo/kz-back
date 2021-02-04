package kz.uco.tsadv.modules.integration.jsonobject;

import java.io.Serializable;
import java.util.ArrayList;

public class OrganizationDataJson implements Serializable {

    public ArrayList<OrganizationJson> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(ArrayList<OrganizationJson> organizations) {
        this.organizations = organizations;
    }

    protected ArrayList<OrganizationJson> organizations;
}
