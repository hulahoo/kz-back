package kz.uco.tsadv.modules.integration.jsonobject;

import java.io.Serializable;
import java.util.ArrayList;

public class HierarchyElementDataJson implements Serializable {

    private ArrayList<HierarchyElementJson> organizationHierarchyElements;

    public ArrayList<HierarchyElementJson> getOrganizationHierarchyElements() {
        return organizationHierarchyElements;
    }

    public void setOrganizationHierarchyElements(ArrayList<HierarchyElementJson> organizationHierarchyElements) {
        this.organizationHierarchyElements = organizationHierarchyElements;
    }
}
