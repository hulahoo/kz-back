package kz.uco.tsadv.entity.tb;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import kz.uco.tsadv.modules.personal.group.JobGroup;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import java.util.List;
import javax.persistence.OneToMany;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;

@NamePattern("%s|name")
@Table(name = "TSADV_WORK_PLACE")
@Entity(name = "tsadv$WorkPlace")
public class WorkPlace extends AbstractParentEntity {
    private static final long serialVersionUID = 6489717192604865484L;

    @NotNull
    @Column(name = "NAME", nullable = false)
    protected String name;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "workPlace")
    protected List<WorkPlaceMonitoring> monitoring;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "workPlace")
    protected List<WorkplaceMSDS> msds;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ORGANIZATION_ID")
    protected OrganizationGroupExt organization;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "JOB_ID")
    protected JobGroup job;



    public void setMonitoring(List<WorkPlaceMonitoring> monitoring) {
        this.monitoring = monitoring;
    }

    public List<WorkPlaceMonitoring> getMonitoring() {
        return monitoring;
    }


    public void setMsds(List<WorkplaceMSDS> msds) {
        this.msds = msds;
    }

    public List<WorkplaceMSDS> getMsds() {
        return msds;
    }


    public OrganizationGroupExt getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationGroupExt organization) {
        this.organization = organization;
    }


    public JobGroup getJob() {
        return job;
    }

    public void setJob(JobGroup job) {
        this.job = job;
    }



    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


}