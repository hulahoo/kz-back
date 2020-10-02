package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import kz.uco.tsadv.modules.personal.group.JobGroup;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.*;

@NamePattern("%s  %s %s %s|caseType,language,longName,shortName")
@Table(name = "TSADV_CASE", uniqueConstraints = {
        @UniqueConstraint(name = "IDX_TSADV_CASE_UNQ", columnNames = {"LANGUAGE_", "CASE_TYPE_ID", "JOB_GROUP_ID"}),
        @UniqueConstraint(name = "IDX_TSADV_CASE_UNQ_1", columnNames = {"LANGUAGE_", "CASE_TYPE_ID", "ORGANIZATION_GROUP_ID"}),
        @UniqueConstraint(name = "IDX_TSADV_CASE_UNQ_2", columnNames = {"LANGUAGE_", "CASE_TYPE_ID", "PERSON_GROUP_ID"}),
        @UniqueConstraint(name = "IDX_TSADV_CASE_UNQ_3", columnNames = {"LANGUAGE_", "CASE_TYPE_ID", "POSITION_GROUP_ID"})
})
@Entity(name = "tsadv$Case")
public class Case extends AbstractParentEntity {
    private static final long serialVersionUID = -3373138479732132570L;

    @Column(name = "LONG_NAME", length = 1000)
    protected String longName;

    @Column(name = "SHORT_NAME", length = 1000)
    protected String shortName;


    @Column(name = "LANGUAGE_", nullable = false)
    protected String language;

    @Lookup(type = LookupType.DROPDOWN)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CASE_TYPE_ID")
    protected CaseType caseType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JOB_GROUP_ID")
    protected JobGroup jobGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_GROUP_ID")
    protected OrganizationGroupExt organizationGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POSITION_GROUP_ID")
    protected PositionGroupExt positionGroup;

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


    public void setOrganizationGroup(OrganizationGroupExt organizationGroup) {
        this.organizationGroup = organizationGroup;
    }

    public OrganizationGroupExt getOrganizationGroup() {
        return organizationGroup;
    }


    public void setCaseType(CaseType caseType) {
        this.caseType = caseType;
    }

    public CaseType getCaseType() {
        return caseType;
    }


    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }


    public void setJobGroup(JobGroup jobGroup) {
        this.jobGroup = jobGroup;
    }

    public JobGroup getJobGroup() {
        return jobGroup;
    }


    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getLongName() {
        return longName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getShortName() {
        return shortName;
    }


}