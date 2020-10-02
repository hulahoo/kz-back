package kz.uco.tsadv.entity.dbview;

import javax.persistence.*;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.chile.core.model.impl.AbstractInstance;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DesignSupport;

import com.haulmont.cuba.core.global.Metadata;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;

import java.util.UUID;

@DesignSupport("{'dbView':true,'generateDdl':false}")
@Table(name = "TSADV_MY_TEAM_VIEW")
@Entity(name = "tsadv$MyTeam")
public class MyTeam extends BaseUuidEntity {
    private static final long serialVersionUID = 6700167249963238535L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_group_id")
    protected PersonGroupExt personGroup;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "organization_group_id")
//    protected OrganizationGroupExt organizationGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_group_id")
    protected PositionGroupExt positionGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_person_group_id")
    protected PersonGroupExt parentPersonGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    protected MyTeam parent;

    @Column(name="path")
    protected String personPath;

    @Column(name="manager_flag")
    protected Boolean managerFlag;

    public void setParentPersonGroup(PersonGroupExt parentPersonGroup) {
        this.parentPersonGroup = parentPersonGroup;
    }

    public PersonGroupExt getParentPersonGroup() {
        return parentPersonGroup;
    }


    public MyTeam getParent() {
        return parent;
    }

    public void setParent(MyTeam parent) {
        this.parent = parent;
    }


//    public void setOrganizationGroup(OrganizationGroupExt organizationGroup) {
//        this.organizationGroup = organizationGroup;
//    }
//
//    public OrganizationGroupExt getOrganizationGroup() {
//        return organizationGroup;
//    }

    public void setPositionGroup(PositionGroupExt positionGroup) {
        this.positionGroup = positionGroup;
    }

    public PositionGroupExt getPositionGroup() {
        return positionGroup;
    }


    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public String getPersonPath() {
        return personPath;
    }

    public void setPersonPath(String personPath) {
        this.personPath = personPath;
    }

    public Boolean getManagerFlag() {
        return managerFlag;
    }

    public void setManagerFlag(Boolean managerFlag) {
        this.managerFlag = managerFlag;
    }

    @Override
    public MetaClass getMetaClass() {
        Metadata metadata = AppBeans.get(Metadata.NAME);
        return metadata.getSession().getClassNN(getClass());
    }

    @MetaProperty
    public String getFioWithEmployeeNumber(){
        return personGroup.getFioWithEmployeeNumber();
    }
}
