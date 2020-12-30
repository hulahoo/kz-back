package kz.uco.tsadv.modules.performance.dictionary;

import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.group.JobGroup;

import javax.persistence.*;

@Table(name = "TSADV_DIC_JOB_GROUP")
@Entity(name = "tsadv_DicJobGroup")
public class DicJobGroup extends AbstractParentEntity {
    private static final long serialVersionUID = 4046562144427005660L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JOB_GROUP_ID")
    protected JobGroup jobGroup;

    public JobGroup getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(JobGroup jobGroup) {
        this.jobGroup = jobGroup;
    }
}