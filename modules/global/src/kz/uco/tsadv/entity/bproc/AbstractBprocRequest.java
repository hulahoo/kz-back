package kz.uco.tsadv.entity.bproc;

import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
public class AbstractBprocRequest extends AbstractParentEntity {
    private static final long serialVersionUID = -1908181720688177085L;

    @Column(name = "REQUEST_NUMBER")
    protected Long requestNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STATUS_ID")
    protected DicRequestStatus status;

    @Temporal(TemporalType.DATE)
    @Column(name = "REQUEST_DATE")
    protected Date requestDate;

    public void setRequestNumber(Long requestNumber) {
        this.requestNumber = requestNumber;
    }

    public Long getRequestNumber() {
        return requestNumber;
    }

    public void setStatus(DicRequestStatus status) {
        this.status = status;
    }

    public DicRequestStatus getStatus() {
        return status;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public Date getRequestDate() {
        return requestDate;
    }
}