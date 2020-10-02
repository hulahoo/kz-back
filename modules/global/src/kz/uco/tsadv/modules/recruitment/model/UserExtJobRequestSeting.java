package kz.uco.tsadv.modules.recruitment.model;

import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.tsadv.modules.recruitment.model.*;
import kz.uco.tsadv.modules.recruitment.model.JobRequest;
import kz.uco.base.entity.extend.UserExt;

import javax.persistence.*;

@Table(name = "TSADV_USER_EXT_JOB_REQUEST_SETING")
@Entity(name = "tsadv$UserExtJobRequestSeting")
public class UserExtJobRequestSeting extends StandardEntity {
    private static final long serialVersionUID = -4422267689405099707L;

    @Column(name = "VIEW_LATER", nullable = false)
    protected Boolean viewLater = false;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_EXT_ID")
    protected UserExt userExt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JOB_REQUEST_ID")
    protected kz.uco.tsadv.modules.recruitment.model.JobRequest jobRequest;


    public void setUserExt(UserExt userExt) {
        this.userExt = userExt;
    }

    public UserExt getUserExt() {
        return userExt;
    }


    public void setJobRequest(kz.uco.tsadv.modules.recruitment.model.JobRequest jobRequest) {
        this.jobRequest = jobRequest;
    }

    public JobRequest getJobRequest() {
        return jobRequest;
    }


    public void setViewLater(Boolean viewLater) {
        this.viewLater = viewLater;
    }

    public Boolean getViewLater() {
        return viewLater;
    }


}