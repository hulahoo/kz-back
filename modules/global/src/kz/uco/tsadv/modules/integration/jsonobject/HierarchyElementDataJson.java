package kz.uco.tsadv.modules.integration.jsonobject;

import java.io.Serializable;
import java.util.ArrayList;

public class HierarchyElementDataJson implements Serializable {

    private ArrayList<HierarchyElementJson> organizationHierarchyElements;

    private ArrayList<HierarchyElementJson> positionHierarchyElements;

    public ArrayList<HierarchyElementJson> getPositionHierarchyElements() {
        return positionHierarchyElements;
    }

    public void setPositionHierarchyElements(ArrayList<HierarchyElementJson> positionHierarchyElements) {
        this.positionHierarchyElements = positionHierarchyElements;
    }

    public ArrayList<HierarchyElementJson> getOrganizationHierarchyElements() {
        return organizationHierarchyElements;
    }

    public void setOrganizationHierarchyElements(ArrayList<HierarchyElementJson> organizationHierarchyElements) {
        this.organizationHierarchyElements = organizationHierarchyElements;
    }
}
