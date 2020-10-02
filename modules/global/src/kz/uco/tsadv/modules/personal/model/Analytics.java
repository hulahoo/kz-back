package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.enums.AnalyticsTypeEnum;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|analyticsType")
@Table(name = "TSADV_ANALYTICS")
@Entity(name = "tsadv$Analytics")
public class Analytics extends AbstractParentEntity {
    private static final long serialVersionUID = -5290391085883236417L;

    @Column(name = "ANALYTICS_TYPE", nullable = false)
    protected String analyticsType;

    @Column(name = "DATA_TYPE", nullable = false)
    protected String dataType;

    @Column(name = "ACTIVE")
    protected Boolean active;

    @Column(name = "ORGANIZATION")
    protected Boolean organization;

    @Column(name = "JOB")
    protected Boolean job;

    @Column(name = "GRADE")
    protected Boolean grade;

    @Column(name = "POSITION_")
    protected Boolean position;

    @Column(name = "JOB_CATEGORY")
    protected Boolean jobCategory;

    @Column(name = "PERSON_CATEGORY")
    protected Boolean personCategory;

    public void setAnalyticsType(String analyticsType) {
        this.analyticsType = analyticsType;
    }

    public String getAnalyticsType() {
        return analyticsType;
    }

    public void setDataType(AnalyticsTypeEnum dataType) {
        this.dataType = dataType == null ? null : dataType.getId();
    }

    public AnalyticsTypeEnum getDataType() {
        return dataType == null ? null : AnalyticsTypeEnum.fromId(dataType);
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getActive() {
        return active;
    }

    public void setOrganization(Boolean organization) {
        this.organization = organization;
    }

    public Boolean getOrganization() {
        return organization;
    }

    public void setJob(Boolean job) {
        this.job = job;
    }

    public Boolean getJob() {
        return job;
    }

    public void setGrade(Boolean grade) {
        this.grade = grade;
    }

    public Boolean getGrade() {
        return grade;
    }

    public void setPosition(Boolean position) {
        this.position = position;
    }

    public Boolean getPosition() {
        return position;
    }

    public void setJobCategory(Boolean jobCategory) {
        this.jobCategory = jobCategory;
    }

    public Boolean getJobCategory() {
        return jobCategory;
    }

    public void setPersonCategory(Boolean personCategory) {
        this.personCategory = personCategory;
    }

    public Boolean getPersonCategory() {
        return personCategory;
    }


}