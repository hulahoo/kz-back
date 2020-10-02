package kz.uco.tsadv.entity;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.*;
import com.haulmont.cuba.core.global.DesignSupport;
import kz.uco.base.entity.shared.ElementType;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.Date;

@DesignSupport("{'dbView':true,'imported':true,'generateDdl':false}")
@Table(name = "timecard_hierarchy")
@Entity(name = "tsadv$TimecardHierarchy")
public class TimecardHierarchy extends BaseUuidEntity {
    private static final long serialVersionUID = 7744690940457793085L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    protected TimecardHierarchy parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_group_id")
    protected OrganizationGroupExt organizationGroup;

    //    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "parent_organization_group_id")
//    protected OrganizationGroupExt parentOrganizationGroup;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_group_id")
    protected PositionGroupExt positionGroup;

    @Column(name = "element_type")
    protected Integer elementType;

    @Lob
    @Column(name = "id_path")
    protected String idPath;

    @Temporal(TemporalType.DATE)
    @Column(name = "start_date")
    protected Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "end_date")
    protected Date endDate;

//    @Lob
//    @Column(name = "path")
//    protected String path;

    @Column(name = "_level")
    protected Integer level;
//
//    @Temporal(TemporalType.TIMESTAMP)
//    @Column(name = "create_ts")
//    protected Date createTs;
//
//    @Lob
//    @Column(name = "created_by")
//    protected String createdBy;
//
//    @Temporal(TemporalType.TIMESTAMP)
//    @Column(name = "delete_ts")
//    protected Date deleteTs;
//
//    @Lob
//    @Column(name = "deleted_by")
//    protected String deletedBy;
//
//    @Temporal(TemporalType.TIMESTAMP)
//    @Column(name = "update_ts")
//    protected Date updateTs;
//
//    @Lob
//    @Column(name = "updated_by")
//    protected String updatedBy;
//
//    @Version
//    @Column(name = "version")
//    protected Integer version;

    @Transient
    @MetaProperty
    protected PersonGroupExt personGroup;

    @Transient
    @MetaProperty
//    @Column(name = "HAS_CHILD")
    protected Boolean hasChild = false;

    public TimecardHierarchy getParent() {
        return parent;
    }

    public void setParent(TimecardHierarchy parent) {
        this.parent = parent;
    }

    public OrganizationGroupExt getOrganizationGroup() {
        return organizationGroup;
    }

    public void setOrganizationGroup(OrganizationGroupExt organizationGroup) {
        this.organizationGroup = organizationGroup;
    }

    public PositionGroupExt getPositionGroup() {
        return positionGroup;
    }

    public void setPositionGroup(PositionGroupExt positionGroup) {
        this.positionGroup = positionGroup;
    }

    public ElementType getElementType() {
        return elementType == null ? null : ElementType.fromId(elementType);
    }

    public void setElementType(ElementType elementType) {
        this.elementType = elementType == null ? null : elementType.getId();
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
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

    public String getIdPath() {
        return idPath;
    }

    public void setIdPath(String idPath) {
        this.idPath = idPath;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    @MetaProperty
    public String getName() {
        String name = "";
        if (organizationGroup != null) {
            name = organizationGroup.getOrganizationName();
            if (organizationGroup.getOrganization() != null
                    && organizationGroup.getOrganization().getCostCenter() != null
                    && organizationGroup.getOrganization().getCostCenter().getCode() != null
                    && !organizationGroup.getOrganization().getCostCenter().getCode().isEmpty()) {
                name += "." + organizationGroup.getOrganization().getCostCenter().getCode();
            }
        }

        if (positionGroup != null) {
            name = positionGroup.getFullName();
        }

        if (ElementType.PERSON.getId().equals(elementType)) {
            name = personGroup.getFioWithEmployeeNumber();
        }

        return name;
    }


    public Boolean getHasChild() {
        return hasChild;
    }

    public void setHasChild(Boolean hasChild) {
        this.hasChild = hasChild;
    }
}