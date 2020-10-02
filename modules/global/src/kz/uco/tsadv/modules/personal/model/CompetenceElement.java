package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import kz.uco.tsadv.modules.personal.group.*;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;

import javax.persistence.*;
import java.util.UUID;

@NamePattern("%s %s %s|competenceGroup,objectId,objectTypeId")
@Table(name = "TSADV_COMPETENCE_ELEMENT")
@Entity(name = "tsadv$CompetenceElement")
public class CompetenceElement extends AbstractParentEntity {
    private static final long serialVersionUID = -1324997804007600202L;


    @Lookup(type = LookupType.SCREEN, actions = {"lookup"})
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COMPETENCE_GROUP_ID")
    protected CompetenceGroup competenceGroup;


    @Column(name = "OBJECT_ID")
    protected UUID objectId;

    @Column(name = "OBJECT_TYPE_ID")
    protected UUID objectTypeId;


    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "open", "clear"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "open", "clear"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_GROUP_ID")
    protected OrganizationGroupExt organizationGroup;

    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "open", "clear"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POSITION_GROUP_ID")
    protected PositionGroupExt positionGroup;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SCALE_LEVEL_ID")
    protected ScaleLevel scaleLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JOB_GROUP_ID")
    protected JobGroup jobGroup;

    public void setJobGroup(JobGroup jobGroup) {
        this.jobGroup = jobGroup;
    }

    public JobGroup getJobGroup() {
        return jobGroup;
    }





    public void setScaleLevel(ScaleLevel scaleLevel) {
        this.scaleLevel = scaleLevel;
    }

    public ScaleLevel getScaleLevel() {
        return scaleLevel;
    }


    public void setCompetenceGroup(CompetenceGroup competenceGroup) {
        this.competenceGroup = competenceGroup;
    }

    public CompetenceGroup getCompetenceGroup() {
        return competenceGroup;
    }


    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
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


    public void setObjectId(UUID objectId) {
        this.objectId = objectId;
    }

    public UUID getObjectId() {
        return objectId;
    }

    public void setObjectTypeId(UUID objectTypeId) {
        this.objectTypeId = objectTypeId;
    }

    public UUID getObjectTypeId() {
        return objectTypeId;
    }


}