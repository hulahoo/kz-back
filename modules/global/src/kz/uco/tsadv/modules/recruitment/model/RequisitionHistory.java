package kz.uco.tsadv.modules.recruitment.model;

import kz.uco.tsadv.modules.recruitment.model.*;
import kz.uco.tsadv.modules.recruitment.model.Requisition;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.recruitment.enums.RequisitionStatus;

import javax.persistence.*;

@Table(name = "TSADV_REQUISITION_HISTORY")
@Entity(name = "tsadv$RequisitionHistory")
public class RequisitionHistory extends AbstractParentEntity {
    private static final long serialVersionUID = 3928336565184545620L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "REQUISITION_ID")
    protected kz.uco.tsadv.modules.recruitment.model.Requisition requisition;

    @Column(name = "STATUS", nullable = false)
    protected Integer status;


    @Column(name = "REASON", length = 2000)
    protected String reason;

    @Column(name = "OPENED_POSITIONS_COUNT", nullable = false)
    protected Double openedPositionsCount;

    public void setOpenedPositionsCount(Double openedPositionsCount) {
        this.openedPositionsCount = openedPositionsCount;
    }

    public Double getOpenedPositionsCount() {
        return openedPositionsCount;
    }


    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }


    public void setRequisition(kz.uco.tsadv.modules.recruitment.model.Requisition requisition) {
        this.requisition = requisition;
    }

    public Requisition getRequisition() {
        return requisition;
    }

    public void setStatus(RequisitionStatus status) {
        this.status = status == null ? null : status.getId();
    }

    public RequisitionStatus getStatus() {
        return status == null ? null : RequisitionStatus.fromId(status);
    }


}