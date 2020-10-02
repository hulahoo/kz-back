package kz.uco.tsadv.entity.tb;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.entity.tb.dictionary.StatusMsds;

@Table(name = "TSADV_WORKPLACE_MSDS")
@Entity(name = "tsadv$WorkplaceMSDS")
public class WorkplaceMSDS extends AbstractParentEntity {
    private static final long serialVersionUID = 1756059589300756502L;

    @Temporal(TemporalType.DATE)
    @Column(name = "DEVELOP_PLAN_DATE", nullable = false)
    protected Date developPlanDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "DEVELOP_FACT_DATE", nullable = false)
    protected Date developFactDate;

    @OnDelete(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MSDS_ID")
    protected FileDescriptor msds;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DOCUMENT_ID")
    protected FileDescriptor document;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "STATUS_ID")
    protected StatusMsds status;

    @Temporal(TemporalType.DATE)
    @Column(name = "DONE_PLAN_DATE")
    protected Date donePlanDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "DONE_FACT_DATE")
    protected Date doneFactDate;





    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WORK_PLACE_ID")
    protected WorkPlace workPlace;

    public void setWorkPlace(WorkPlace workPlace) {
        this.workPlace = workPlace;
    }

    public WorkPlace getWorkPlace() {
        return workPlace;
    }


    public StatusMsds getStatus() {
        return status;
    }

    public void setStatus(StatusMsds status) {
        this.status = status;
    }


    public void setDevelopPlanDate(Date developPlanDate) {
        this.developPlanDate = developPlanDate;
    }

    public Date getDevelopPlanDate() {
        return developPlanDate;
    }

    public void setDevelopFactDate(Date developFactDate) {
        this.developFactDate = developFactDate;
    }

    public Date getDevelopFactDate() {
        return developFactDate;
    }

    public void setDonePlanDate(Date donePlanDate) {
        this.donePlanDate = donePlanDate;
    }

    public Date getDonePlanDate() {
        return donePlanDate;
    }

    public void setDoneFactDate(Date doneFactDate) {
        this.doneFactDate = doneFactDate;
    }

    public Date getDoneFactDate() {
        return doneFactDate;
    }


    public void setMsds(FileDescriptor msds) {
        this.msds = msds;
    }

    public FileDescriptor getMsds() {
        return msds;
    }

    public void setDocument(FileDescriptor document) {
        this.document = document;
    }

    public FileDescriptor getDocument() {
        return document;
    }



}