package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;

import javax.persistence.*;
import java.util.Date;

@NamePattern("%s|id")
@Table(name = "TSADV_TRANSFER_REQUEST")
@Entity(name = "tsadv$TransferRequest")
public class TransferRequest extends AbstractParentEntity {
    private static final long serialVersionUID = 593237936153107247L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ASSIGNMENT_GROUP_ID")
    protected AssignmentGroupExt assignmentGroup;

    @Temporal(TemporalType.DATE)
    @Column(name = "REQUEST_DATE", nullable = false)
    protected Date requestDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "TRANSFER_DATE", nullable = false)
    protected Date transferDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "NEW_POSITION_GROUP_ID")
    protected PositionGroupExt newPositionGroup;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "NEW_ORGANIZATION_GROUP_ID")
    protected OrganizationGroupExt newOrganizationGroup;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "REQUEST_STATUS_ID")
    protected DicRequestStatus requestStatus;

    public void setAssignmentGroup(AssignmentGroupExt assignmentGroup) {
        this.assignmentGroup = assignmentGroup;
    }

    public AssignmentGroupExt getAssignmentGroup() {
        return assignmentGroup;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setTransferDate(Date transferDate) {
        this.transferDate = transferDate;
    }

    public Date getTransferDate() {
        return transferDate;
    }

    public void setNewPositionGroup(PositionGroupExt newPositionGroup) {
        this.newPositionGroup = newPositionGroup;
    }

    public PositionGroupExt getNewPositionGroup() {
        return newPositionGroup;
    }

    public void setNewOrganizationGroup(OrganizationGroupExt newOrganizationGroup) {
        this.newOrganizationGroup = newOrganizationGroup;
    }

    public OrganizationGroupExt getNewOrganizationGroup() {
        return newOrganizationGroup;
    }

    public void setRequestStatus(DicRequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    public DicRequestStatus getRequestStatus() {
        return requestStatus;
    }


}