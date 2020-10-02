package kz.uco.tsadv.modules.personal.model;

import javax.persistence.*;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.annotation.Extends;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.core.global.PersistenceHelper;
import kz.uco.base.entity.shared.ElementType;
import kz.uco.base.entity.shared.HierarchyElement;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import com.haulmont.cuba.core.entity.annotation.Listeners;

@Listeners("tsadv_HierarchyElementExtListener")
@Extends(HierarchyElement.class)
@Entity(name = "base$HierarchyElementExt")
public class HierarchyElementExt extends HierarchyElement {
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