package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import kz.uco.base.entity.dictionary.DicLocation;

@MetaClass(name = "tsadv$PositionPercentage")
public class PositionPercentage extends BaseUuidEntity {
    private static final long serialVersionUID = -9087702549905921330L;

    @MetaProperty
    private PositionExt position;

    @MetaProperty
    private OrganizationExt organization;

    @MetaProperty
    private DicLocation location;

    @MetaProperty
    protected Integer allCount;

    @MetaProperty
    protected Integer count;

    @MetaProperty
    protected Integer match;

    public Integer getMatch() {
        return match;
    }

    public void setMatch(Integer match) {
        this.match = match;
    }

    public DicLocation getLocation() {
        return location;
    }

    public void setLocation(DicLocation location) {
        this.location = location;
    }

    public Integer getAllCount() {
        return allCount;
    }

    public void setAllCount(Integer allCount) {
        this.allCount = allCount;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
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
}