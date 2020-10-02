package kz.uco.tsadv.modules.recruitment.model;

import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.recruitment.enums.OfferStatus;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.*;
import java.util.Date;

@Table(name = "TSADV_OFFER_HISTORY")
@Entity(name = "tsadv$OfferHistory")
public class OfferHistory extends AbstractParentEntity {
    private static final long serialVersionUID = -323584445857807740L;

    @Temporal(TemporalType.DATE)
    @Column(name = "STATUS_CHANGE_DATE")
    protected Date statusChangeDate;

    @Column(name = "STATUS")
    protected Integer status;

    @Lob
    @Column(name = "CHANGE_REASON")
    protected String changeReason;

    @Lob
    @Column(name = "DECLINE_REASON")
    protected String declineReason;

    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "clear"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHANGED_BY_ID")
    protected PersonGroupExt changedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OFFER_ID")
    protected kz.uco.tsadv.modules.recruitment.model.Offer offer;

    public void setOffer(kz.uco.tsadv.modules.recruitment.model.Offer offer) {
        this.offer = offer;
    }

    public Offer getOffer() {
        return offer;
    }


    public void setStatusChangeDate(Date statusChangeDate) {
        this.statusChangeDate = statusChangeDate;
    }

    public Date getStatusChangeDate() {
        return statusChangeDate;
    }

    public void setStatus(OfferStatus status) {
        this.status = status == null ? null : status.getId();
    }

    public OfferStatus getStatus() {
        return status == null ? null : OfferStatus.fromId(status);
    }

    public void setChangeReason(String changeReason) {
        this.changeReason = changeReason;
    }

    public String getChangeReason() {
        return changeReason;
    }

    public void setDeclineReason(String declineReason) {
        this.declineReason = declineReason;
    }

    public String getDeclineReason() {
        return declineReason;
    }

    public void setChangedBy(PersonGroupExt changedBy) {
        this.changedBy = changedBy;
    }

    public PersonGroupExt getChangedBy() {
        return changedBy;
    }


}