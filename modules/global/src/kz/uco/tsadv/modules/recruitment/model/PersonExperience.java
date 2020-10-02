package kz.uco.tsadv.modules.recruitment.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@NamePattern("%s (%s - %s)|company,startMonth,endMonth")
@Table(name = "TSADV_PERSON_EXPERIENCE")
@Entity(name = "tsadv$PersonExperience")
public class PersonExperience extends AbstractParentEntity {
    private static final long serialVersionUID = -5431966919596389466L;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @Column(name = "UNTIL_NOW")
    protected Boolean untilNow;

    @Column(name = "COMPANY", nullable = false)
    protected String company;

    @Column(name = "JOB", nullable = false)
    protected String job;

    @Temporal(TemporalType.DATE)
    @Column(name = "START_MONTH", nullable = false)
    protected Date startMonth;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_MONTH")
    protected Date endMonth;

    @Column(name = "DESCRIPTION", length = 4000)
    protected String description;

    public void setUntilNow(Boolean untilNow) {
        this.untilNow = untilNow;
    }

    public Boolean getUntilNow() {
        return untilNow;
    }


    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCompany() {
        return company;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getJob() {
        return job;
    }

    public void setStartMonth(Date startMonth) {
        this.startMonth = startMonth;
    }

    public Date getStartMonth() {
        return startMonth;
    }

    public void setEndMonth(Date endMonth) {
        this.endMonth = endMonth;
    }

    public Date getEndMonth() {
        return endMonth;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}