package kz.uco.tsadv.modules.recruitment.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.tsadv.modules.recruitment.enums.JobRequestStatus;
import kz.uco.tsadv.modules.recruitment.model.*;
import kz.uco.tsadv.modules.recruitment.model.JobRequest;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.*;

@NamePattern("%s|id")
@Table(name = "TSADV_JOB_REQUEST_HISTORY")
@Entity(name = "tsadv$JobRequestHistory")
public class JobRequestHistory extends AbstractParentEntity {
    private static final long serialVersionUID = -6199640006290244851L;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "JOB_REQUEST_ID")
    protected kz.uco.tsadv.modules.recruitment.model.JobRequest jobRequest;

    @Column(name = "JOB_REQUEST_STATUS", nullable = false)
    protected Integer jobRequestStatus;

    public void setJobRequest(kz.uco.tsadv.modules.recruitment.model.JobRequest jobRequest) {
        this.jobRequest = jobRequest;
    }

    public JobRequest getJobRequest() {
        return jobRequest;
    }

    public void setJobRequestStatus(JobRequestStatus jobRequestStatus) {
        this.jobRequestStatus = jobRequestStatus == null ? null : jobRequestStatus.getId();
    }

    public JobRequestStatus getJobRequestStatus() {
        return jobRequestStatus == null ? null : JobRequestStatus.fromId(jobRequestStatus);
    }


}