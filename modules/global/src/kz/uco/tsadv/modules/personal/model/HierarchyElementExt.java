package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.annotation.Extends;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.core.global.PersistenceHelper;
import kz.uco.base.entity.abstraction.IGroupedEntity;
import kz.uco.base.entity.shared.ElementType;
import kz.uco.base.entity.shared.HierarchyElement;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Listeners("tsadv_HierarchyElementExtListener")
@Extends(HierarchyElement.class)
@Entity(name = "base$HierarchyElementExt")
public class HierarchyElementExt extends HierarchyElement implements IGroupedEntity<HierarchyElementGroup> {
    private static final long serialVersionUID = 5878141261343097969L;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POSITION_GROUP_ID")
    protected PositionGroupExt positionGroup;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_GROUP_ID")
    protected OrganizationGroupExt organizationGroup;

    @Transient
    @MetaProperty
    protected PersonGroupExt personGroup;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ID")
    protected HierarchyElementExt parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_GROUP_ID")
    protected HierarchyElementGroup parentGroup;

    @Transient
    @MetaProperty(related = "parentGroup")
    protected HierarchyElementExt parentFromGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID")
    protected HierarchyElementGroup group;

    @NotNull
    @Transient
    @MetaProperty(mandatory = true)
    protected Boolean hasChild = false;

    public Boolean getHasChild() {
        return hasChild;
    }

    public void setHasChild(Boolean hasChild) {
        this.hasChild = hasChild;
    }

    public HierarchyElementExt getParentFromGroup() {
        if (parentFromGroup == null && parentGroup != null) parentFromGroup = parentGroup.getHierarchyElement();
        return parentFromGroup;
    }

    public void setParentFromGroup(HierarchyElementExt parentFromGroup) {
        this.parentFromGroup = parentFromGroup;
    }

    public HierarchyElementGroup getParentGroup() {
        return parentGroup;
    }

    public void setParentGroup(HierarchyElementGroup parentGroup) {
        this.parentGroup = parentGroup;
    }

    public HierarchyElementGroup getGroup() {
        return group;
    }

    public void setGroup(HierarchyElementGroup group) {
        this.group = group;
    }

    public PositionGroupExt getPositionGroup() {
        return positionGroup;
    }

    public void setPositionGroup(PositionGroupExt positionGroup) {
        this.positionGroup = positionGroup;
    }

    public OrganizationGroupExt getOrganizationGroup() {
        return organizationGroup;
    }

    public void setOrganizationGroup(OrganizationGroupExt organizationGroup) {
        this.organizationGroup = organizationGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public HierarchyElementExt getParent() {
        return parent;
    }

    public void setParent(HierarchyElementExt parent) {
        this.parent = parent;
    }

    @Transient
    @MetaProperty(related = {"organizationGroup", "positionGroup"})
    public String getName() {
        if (organizationGroup != null && PersistenceHelper.isLoaded(this, "organizationGroup")) {
            if (PersistenceHelper.isLoaded(organizationGroup, "organization") && organizationGroup.getOrganization() != null) {
                return organizationGroup.getOrganization().getOrganizationName();
            }
        }

        if (positionGroup != null && PersistenceHelper.isLoaded(this, "positionGroup")) {
            if (PersistenceHelper.isLoaded(positionGroup, "position") && positionGroup.getPosition() != null) {
                return positionGroup.getPosition().getPositionFullName();
            }
        }

        if (ElementType.PERSON.getId().equals(elementType)) {
            return personGroup.getFullName();
        }

        return null;
    }
}