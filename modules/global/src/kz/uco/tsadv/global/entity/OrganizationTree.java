package kz.uco.tsadv.global.entity;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;

import java.util.UUID;

@MetaClass(name = "tsadv$OrganizationTree")
public class OrganizationTree extends BaseUuidEntity {
    private static final long serialVersionUID = -454924937357648276L;

    @MetaProperty
    protected OrganizationTree parent;

    @MetaProperty
    protected UUID organizationGroupId;

    @MetaProperty
    protected String organizationName;

    @MetaProperty
    protected Boolean hasChild = false;

    @MetaProperty
    protected OrganizationGroupExt organizationGroupExt;

    public OrganizationGroupExt getOrganizationGroupExt() {
        return organizationGroupExt;
    }

    public void setOrganizationGroupExt(OrganizationGroupExt organizationGroupExt) {
        this.organizationGroupExt = organizationGroupExt;
    }

    public Boolean getHasChild() {
        return hasChild;
    }

    public void setHasChild(Boolean hasChild) {
        this.hasChild = hasChild;
    }

    public OrganizationTree getParent() {
        return parent;
    }

    public void setParent(OrganizationTree parent) {
        this.parent = parent;
    }

    public UUID getOrganizationGroupId() {
        return organizationGroupId;
    }

    public void setOrganizationGroupId(UUID organizationGroupId) {
        this.organizationGroupId = organizationGroupId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }
}