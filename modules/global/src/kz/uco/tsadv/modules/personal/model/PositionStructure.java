package kz.uco.tsadv.modules.personal.model;

import com.haulmont.cuba.core.global.DesignSupport;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.base.entity.shared.ElementType;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;

import javax.persistence.*;
import java.util.Date;

@DesignSupport("{'dbView':true,'generateDdl':false}")
@Table(name = "TSADV_POSITION_STRUCTURE")
@Entity(name = "tsadv$PositionStructure")
public class PositionStructure extends AbstractParentEntity {
    private static final long serialVersionUID = 956532198794497966L;

    @Column(name = "LVL")
    protected Integer lvl;

    @Column(name = "ELEMENT_TYPE")
    protected Integer elementType;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_GROUP_ID")
    protected OrganizationGroupExt organizationGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POSITION_GROUP_ID")
    protected PositionGroupExt positionGroup;

    @Column(name = "MANAGER_FLAG")
    protected Boolean managerFlag;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ORGANIZATION_GROUP_ID")
    protected OrganizationGroupExt parentOrganizationGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_POSITION_GROUP_ID")
    protected PositionGroupExt parentPositionGroup;

    @Column(name = "POSITION_GROUP_PATH")
    protected String positionGroupPath;

    @Column(name = "ORGANIZATION_GROUP_PATH")
    protected String organizationGroupPath;

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE")
    protected Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE")
    protected Date endDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "POS_START_DATE")
    protected Date posStartDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "POS_END_DATE")
    protected Date posEndDate;

    public void setPosStartDate(Date posStartDate) {
        this.posStartDate = posStartDate;
    }

    public Date getPosStartDate() {
        return posStartDate;
    }

    public void setPosEndDate(Date posEndDate) {
        this.posEndDate = posEndDate;
    }

    public Date getPosEndDate() {
        return posEndDate;
    }

    public ElementType getElementType() {
        return elementType == null ? null : ElementType.fromId(elementType);
    }

    public void setElementType(ElementType elementType) {
        this.elementType = elementType == null ? null : elementType.getId();
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getEndDate() {
        return endDate;
    }


    public void setOrganizationGroup(OrganizationGroupExt organizationGroup) {
        this.organizationGroup = organizationGroup;
    }

    public OrganizationGroupExt getOrganizationGroup() {
        return organizationGroup;
    }

    public void setPositionGroup(PositionGroupExt positionGroup) {
        this.positionGroup = positionGroup;
    }

    public PositionGroupExt getPositionGroup() {
        return positionGroup;
    }

    public void setParentOrganizationGroup(OrganizationGroupExt parentOrganizationGroup) {
        this.parentOrganizationGroup = parentOrganizationGroup;
    }

    public OrganizationGroupExt getParentOrganizationGroup() {
        return parentOrganizationGroup;
    }

    public void setParentPositionGroup(PositionGroupExt parentPositionGroup) {
        this.parentPositionGroup = parentPositionGroup;
    }

    public PositionGroupExt getParentPositionGroup() {
        return parentPositionGroup;
    }


    public void setLvl(Integer lvl) {
        this.lvl = lvl;
    }

    public Integer getLvl() {
        return lvl;
    }


    public void setManagerFlag(Boolean managerFlag) {
        this.managerFlag = managerFlag;
    }

    public Boolean getManagerFlag() {
        return managerFlag;
    }


    public String getPositionGroupPath() {
        return positionGroupPath;
    }

    public void setPositionGroupPath(String positionGroupPath) {
        this.positionGroupPath = positionGroupPath;
    }

    public String getOrganizationGroupPath() {
        return organizationGroupPath;
    }

    public void setOrganizationGroupPath(String organizationGroupPath) {
        this.organizationGroupPath = organizationGroupPath;
    }
}