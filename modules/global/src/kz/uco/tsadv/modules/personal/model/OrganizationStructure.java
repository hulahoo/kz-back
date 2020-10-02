package kz.uco.tsadv.modules.personal.model;

import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.base.entity.shared.Hierarchy;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;

import javax.persistence.*;
import java.util.Date;

@Table(name = "tsadv_organization_structure")
@Entity(name = "tsadv$OrganizationStructure")
public class OrganizationStructure extends StandardEntity {
    private static final long serialVersionUID = -2217624132237086972L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HIERARCHY_ID")
    protected Hierarchy hierarchy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ID")
    protected HierarchyElementExt parent;

    @Column(name = "ELEMENT_TYPE")
    protected Integer elementType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_GROUP_ID")
    protected OrganizationGroupExt organizationGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ORGANIZATION_GROUP_ID")
    protected OrganizationGroupExt parentOrganizationGroup;

    @Column(name = "PATH_ORG_NAME1")
    protected String pathOrgName1;

    @Column(name = "PATH_ORG_NAME2")
    protected String pathOrgName2;

    @Column(name = "PATH_ORG_NAME3")
    protected String pathOrgName3;

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE")
    protected Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE")
    protected Date endDate;

    @Column(name = "PATH")
    protected String path;

    @Column(name = "_LEVEL")
    protected Integer level;

    public Hierarchy getHierarchy() {
        return hierarchy;
    }

    public void setHierarchy(Hierarchy hierarchy) {
        this.hierarchy = hierarchy;
    }

    public HierarchyElementExt getParent() {
        return parent;
    }

    public void setParent(HierarchyElementExt parent) {
        this.parent = parent;
    }

    public Integer getElementType() {
        return elementType;
    }

    public void setElementType(Integer elementType) {
        this.elementType = elementType;
    }

    public OrganizationGroupExt getOrganizationGroup() {
        return organizationGroup;
    }

    public void setOrganizationGroup(OrganizationGroupExt organizationGroup) {
        this.organizationGroup = organizationGroup;
    }

    public OrganizationGroupExt getParentOrganizationGroup() {
        return parentOrganizationGroup;
    }

    public void setParentOrganizationGroup(OrganizationGroupExt parentOrganizationGroup) {
        this.parentOrganizationGroup = parentOrganizationGroup;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getPathOrgName1() {
        return pathOrgName1;
    }

    public void setPathOrgName1(String pathOrgName1) {
        this.pathOrgName1 = pathOrgName1;
    }

    public String getPathOrgName2() {
        return pathOrgName2;
    }

    public void setPathOrgName2(String pathOrgName2) {
        this.pathOrgName2 = pathOrgName2;
    }

    public String getPathOrgName3() {
        return pathOrgName3;
    }

    public void setPathOrgName3(String pathOrgName3) {
        this.pathOrgName3 = pathOrgName3;
    }
}
