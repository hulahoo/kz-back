package kz.uco.tsadv.modules.recruitment.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.tsadv.modules.recruitment.model.*;
import kz.uco.tsadv.modules.recruitment.model.Requisition;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.recruitment.dictionary.DicPostingChannel;

import javax.persistence.*;
import java.util.Date;

@NamePattern("%s|id")
@Table(name = "TSADV_REQUISITION_POSTING_CHANNEL")
@Entity(name = "tsadv$RequisitionPostingChannel")
public class RequisitionPostingChannel extends AbstractParentEntity {
    private static final long serialVersionUID = -5699686877279818705L;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "REQUISITION_ID")
    protected kz.uco.tsadv.modules.recruitment.model.Requisition requisition;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POSTING_CHANNEL_ID")
    protected DicPostingChannel postingChannel;

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE")
    protected Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE")
    protected Date endDate;

    public DicPostingChannel getPostingChannel() {
        return postingChannel;
    }

    public void setPostingChannel(DicPostingChannel postingChannel) {
        this.postingChannel = postingChannel;
    }


    public void setRequisition(kz.uco.tsadv.modules.recruitment.model.Requisition requisition) {
        this.requisition = requisition;
    }

    public Requisition getRequisition() {
        return requisition;
    }


    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getEndDate() {
        return endDate;
    }


}