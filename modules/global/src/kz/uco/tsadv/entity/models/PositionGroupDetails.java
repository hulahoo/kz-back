package kz.uco.tsadv.entity.models;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

@MetaClass(name = "tsadv_PositionGroupDetails")
public class PositionGroupDetails extends BaseUuidEntity {
    private static final long serialVersionUID = -2988558908223784257L;

    @MetaProperty
    private String positionGroupName;

    @MetaProperty
    private String functionalManagerId;

    @MetaProperty
    private String functionalManagerName;

    @MetaProperty
    private String administrativeManagerId;

    @MetaProperty
    private String administrativeManagerName;

    @MetaProperty
    private String structuralOrganizationsTree;

    public String getStructuralOrganizationsTree() {
        return structuralOrganizationsTree;
    }

    public void setStructuralOrganizationsTree(String structuralOrganizationsTree) {
        this.structuralOrganizationsTree = structuralOrganizationsTree;
    }

    public String getAdministrativeManagerName() {
        return administrativeManagerName;
    }

    public void setAdministrativeManagerName(String administrativeManagerName) {
        this.administrativeManagerName = administrativeManagerName;
    }

    public String getAdministrativeManagerId() {
        return administrativeManagerId;
    }

    public void setAdministrativeManagerId(String administrativeManagerId) {
        this.administrativeManagerId = administrativeManagerId;
    }

    public String getFunctionalManagerName() {
        return functionalManagerName;
    }

    public void setFunctionalManagerName(String functionalManagerName) {
        this.functionalManagerName = functionalManagerName;
    }

    public String getFunctionalManagerId() {
        return functionalManagerId;
    }

    public void setFunctionalManagerId(String functionalManagerId) {
        this.functionalManagerId = functionalManagerId;
    }

    public String getPositionGroupName() {
        return positionGroupName;
    }

    public void setPositionGroupName(String positionGroupName) {
        this.positionGroupName = positionGroupName;
    }
}