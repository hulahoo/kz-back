package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import kz.uco.base.entity.dictionary.DicLocation;

/**
 * @author Adilbekov Yernar
 */

@MetaClass(name = "tsadv$PersonPercentage")
public class PersonPercentage extends BaseUuidEntity {
    private static final long serialVersionUID = -4673598244875278097L;

    @MetaProperty
    protected AssignmentExt assignment;

    @MetaProperty
    protected AssignmentExt
            managerAssignment;

    @MetaProperty
    protected OrganizationExt organization;

    @MetaProperty
    protected PositionExt position;

    @MetaProperty
    protected Integer matrix;

    @MetaProperty
    protected Integer match;

    @MetaProperty
    public String getLocation() {
        if (position != null) {
            DicLocation location = position.getLocation();
            if (location != null && location.getLangValue() != null) {
                return location.getLangValue();
            }
        }

        if (organization != null) {
            DicLocation location = organization.getLocation();
            if (location != null) {
                return location.getLangValue();
            }
        }
        return "";
    }

    public AssignmentExt getManagerAssignment() {
        return managerAssignment;
    }

    public void setManagerAssignment(AssignmentExt managerAssignment) {
        this.managerAssignment = managerAssignment;
    }

    public AssignmentExt getAssignment() {
        return assignment;
    }

    public void setAssignment(AssignmentExt assignment) {
        this.assignment = assignment;
    }

    public Integer getMatch() {
        return match;
    }

    public void setMatch(Integer match) {
        this.match = match;
    }

    public PositionExt getPosition() {
        return position;
    }

    public void setPosition(PositionExt position) {
        this.position = position;
    }

    public OrganizationExt getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationExt organization) {
        this.organization = organization;
    }

    public Integer getMatrix() {
        return matrix;
    }

    public void setMatrix(Integer matrix) {
        this.matrix = matrix;
    }
}