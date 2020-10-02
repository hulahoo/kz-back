package kz.uco.tsadv.modules.recruitment.model;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.group.JobGroup;

import javax.persistence.*;
import java.util.List;

@NamePattern("%s|groupName")
@Table(name = "TSADV_RC_JOB_GROUP")
@Entity(name = "tsadv$RcJobGroup")
public class RcJobGroup extends AbstractParentEntity {
    private static final long serialVersionUID = -7111456532192928969L;

    @Column(name = "GROUP_NAME")
    protected String groupName;

    @JoinTable(name = "TSADV_RC_JOB_GROUP_JOB_GROUP_LINK",
            joinColumns = @JoinColumn(name = "RC_JOB_GROUP_ID"),
            inverseJoinColumns = @JoinColumn(name = "JOB_GROUP_ID"))
    @ManyToMany
    protected List<JobGroup> jobs;

    public List<JobGroup> getJobs() {
        return jobs;
    }

    public void setJobs(List<JobGroup> jobs) {
        this.jobs = jobs;
    }


    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }


}