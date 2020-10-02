package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.dictionary.DicDismissalReason;
import kz.uco.tsadv.modules.personal.dictionary.DicDismissalStatus;
import kz.uco.tsadv.modules.personal.dictionary.DicLCArticle;
import kz.uco.tsadv.modules.personal.group.OrderGroup;

import javax.persistence.*;
import java.util.Date;
import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;

@Listeners("tsadv_DismissalListener")
@NamePattern("%s|id")
@Table(name = "TSADV_DISMISSAL")
@Entity(name = "tsadv$Dismissal")
public class Dismissal extends AbstractParentEntity {
    private static final long serialVersionUID = -1470627246730068474L;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ASSIGNMENT_GROUP_ID")
    protected AssignmentGroupExt assignmentGroup;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORD_ASSIGNMENT_ID")
    protected OrdAssignment ordAssignment;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_GROUP_ID")
    protected OrderGroup orderGroup;

    @Temporal(TemporalType.DATE)
    @Column(name = "DISMISSAL_DATE", nullable = false)
    protected Date dismissalDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "REQUEST_DATE", nullable = false)
    protected Date requestDate;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "LC_ARTICLE_ID")
    protected DicLCArticle lcArticle;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DISMISSAL_REASON_ID")
    protected DicDismissalReason dismissalReason;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "STATUS_ID")
    protected DicDismissalStatus status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID")
    protected Order order;

    @Column(name = "ORDER_NUMBER")
    protected String orderNumber;

    @Temporal(TemporalType.DATE)
    @Column(name = "ORDER_DATE")
    protected Date orderDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "FINAL_CALCULATION_DATE")
    protected Date finalCalculationDate;

    public void setAssignmentGroup(AssignmentGroupExt assignmentGroup) {
        this.assignmentGroup = assignmentGroup;
    }

    public AssignmentGroupExt getAssignmentGroup() {
        return assignmentGroup;
    }


    public void setOrder(Order order) {
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setFinalCalculationDate(Date finalCalculationDate) {
        this.finalCalculationDate = finalCalculationDate;
    }

    public Date getFinalCalculationDate() {
        return finalCalculationDate;
    }


    public OrdAssignment getOrdAssignment() {
        return ordAssignment;
    }

    public void setOrdAssignment(OrdAssignment ordAssignment) {
        this.ordAssignment = ordAssignment;
    }



    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }



    public void setOrderGroup(OrderGroup orderGroup) {
        this.orderGroup = orderGroup;
    }

    public OrderGroup getOrderGroup() {
        return orderGroup;
    }

    public void setDismissalReason(DicDismissalReason dismissalReason) {
        this.dismissalReason = dismissalReason;
    }

    public DicDismissalReason getDismissalReason() {
        return dismissalReason;
    }


    public void setStatus(DicDismissalStatus status) {
        this.status = status;
    }

    public DicDismissalStatus getStatus() {
        return status;
    }


    public void setLcArticle(DicLCArticle lcArticle) {
        this.lcArticle = lcArticle;
    }

    public DicLCArticle getLcArticle() {
        return lcArticle;
    }


    public void setDismissalDate(Date dismissalDate) {
        this.dismissalDate = dismissalDate;
    }

    public Date getDismissalDate() {
        return dismissalDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public Date getRequestDate() {
        return requestDate;
    }


}