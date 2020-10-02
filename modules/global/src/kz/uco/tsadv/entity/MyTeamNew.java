package kz.uco.tsadv.entity;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

import java.util.UUID;

@NamePattern("%s|id")
@MetaClass(name = "tsadv$MyTeamNew")
public class MyTeamNew extends BaseUuidEntity {
    private static final long serialVersionUID = 3821687778348771182L;

    @MetaProperty
    protected MyTeamNew parent;

    @MetaProperty
    protected UUID personGroupId;

    @MetaProperty
    protected UUID positionGroupId;

    @MetaProperty
    protected String fullName;

    @MetaProperty
    protected String organizationNameLang1;

    @MetaProperty
    protected String positionNameLang1;

    @MetaProperty
    protected String gradeName;

    @MetaProperty
    protected Boolean linkEnabled;

    @MetaProperty
    protected Boolean hasChild;

    public void setHasChild(Boolean hasChild) {
        this.hasChild = hasChild;
    }

    public Boolean getHasChild() {
        return hasChild;
    }

    public void setPersonGroupId(UUID personGroupId) {
        this.personGroupId = personGroupId;
    }

    public UUID getPersonGroupId() {
        return personGroupId;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setOrganizationNameLang1(String organizationNameLang1) {
        this.organizationNameLang1 = organizationNameLang1;
    }

    public String getOrganizationNameLang1() {
        return organizationNameLang1;
    }

    public void setPositionNameLang1(String positionNameLang1) {
        this.positionNameLang1 = positionNameLang1;
    }

    public String getPositionNameLang1() {
        return positionNameLang1;
    }

    public void setParent(MyTeamNew parent) {
        this.parent = parent;
    }

    public MyTeamNew getParent() {
        return parent;
    }

    public UUID getPositionGroupId() {
        return positionGroupId;
    }

    public void setPositionGroupId(UUID positionGroupId) {
        this.positionGroupId = positionGroupId;
    }

    public Boolean getLinkEnabled() {
        return linkEnabled;
    }

    public void setLinkEnabled(Boolean linkEnabled) {
        this.linkEnabled = linkEnabled;
    }

    public String getGradeName() {
        return gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }
}