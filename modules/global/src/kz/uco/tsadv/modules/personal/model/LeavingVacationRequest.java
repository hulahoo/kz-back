package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.modules.recruitment.dictionary.DicRequisitionType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Table(name = "TSADV_LEAVING_VACATION_REQUEST")
@Entity(name = "tsadv$LeavingVacationRequest")
@NamePattern("%s %s %s|id,startDate,endData")
public class LeavingVacationRequest extends AbstractParentEntity {
    private static final long serialVersionUID = -3518404858438386443L;

    @Column(name = "REQUEST_NUMBER", nullable = false)
    @NotNull
    private Long requestNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STATUS_REQUEST_ID")
    @NotNull
    private DicRequestStatus statusRequest;

    @Column(name = "REQUEST_DATE")
    @NotNull
    @Temporal(TemporalType.DATE)
    private Date requestDate;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUEST_TYPE_ID")
    private DicRequisitionType requestType;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VACATION_ID")
    private Absence vacation;

    @Column(name = "START_DATE")
    @NotNull
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Column(name = "END_DATA")
    @NotNull
    @Temporal(TemporalType.DATE)
    private Date endData;

    @Column(name = "PLANNED_START_DATE")
    @NotNull
    @Temporal(TemporalType.DATE)
    private Date plannedStartDate;

    @Column(name = "COMMENT_", length = 2500)
    private String comment;

    public void setStatusRequest(DicRequestStatus statusRequest) {
        this.statusRequest = statusRequest;
    }

    public DicRequestStatus getStatusRequest() {
        return statusRequest;
    }

    public Long getRequestNumber() {
        return requestNumber;
    }

    public void setRequestNumber(Long requestNumber) {
        this.requestNumber = requestNumber;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public DicRequisitionType getRequestType() {
        return requestType;
    }

    public void setRequestType(DicRequisitionType requestType) {
        this.requestType = requestType;
    }

    public Absence getVacation() {
        return vacation;
    }

    public void setVacation(Absence vacation) {
        this.vacation = vacation;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndData() {
        return endData;
    }

    public void setEndData(Date endData) {
        this.endData = endData;
    }

    public Date getPlannedStartDate() {
        return plannedStartDate;
    }

    public void setPlannedStartDate(Date plannedStartDate) {
        this.plannedStartDate = plannedStartDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}