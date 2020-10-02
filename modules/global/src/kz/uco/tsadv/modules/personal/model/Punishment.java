package kz.uco.tsadv.modules.personal.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import kz.uco.tsadv.modules.personal.dictionary.DicAcceptedAction;
import kz.uco.tsadv.modules.personal.dictionary.DicAdditionalEducation;
import kz.uco.tsadv.modules.personal.dictionary.DicExamResults;
import kz.uco.tsadv.modules.personal.dictionary.DicOffenceType;
import kz.uco.tsadv.modules.personal.dictionary.DicPunishmentTypes;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;
import kz.uco.tsadv.modules.personal.dictionary.DicLCArticle;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

@Listeners("tsadv_PunishmentListener")
@Table(name = "TSADV_PUNISHMENT")
@Entity(name = "tsadv$Punishment")
public class Punishment extends AbstractParentEntity {
    private static final long serialVersionUID = -4413020613224333088L;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_")
    protected Date date;

    @Column(name = "ACCIDENT_CAUSE")
    protected String accidentCause;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RESPONSIBLE_EMPLOYEE_ID")
    protected PersonGroupExt responsibleEmployee;

    @Column(name = "NOTIFY_EMPLOYEES")
    protected Boolean notifyEmployees;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LAW_ARTICLE_ID")
    protected DicLCArticle lawArticle;

    @Column(name = "EARLY_TERMINATION_REASON")
    protected String earlyTerminationReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TYPE_ID")
    protected DicPunishmentTypes type;

    @Column(name = "ACCIDENT")
    protected String accident;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OFFENCE_TYPE_ID")
    protected DicOffenceType offenceType;

    @Column(name = "REASON")
    protected String reason;

    @Column(name = "IS_SAFETY_ENGINEERING")
    protected Boolean isSafetyEngineering;

    @Temporal(TemporalType.DATE)
    @Column(name = "PERIOD")
    protected Date period;

    @Column(name = "ORDER_NUMBER")
    protected String orderNumber;

    @Temporal(TemporalType.DATE)
    @Column(name = "ORDER_DATE")
    protected Date orderDate;

    @Column(name = "DEPRESSION")
    protected Integer depression;

    @Column(name = "LED_TO_ACCIDENT")
    protected Boolean ledToAccident;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ADDITIONAL_EDUCATION_ID")
    protected DicAdditionalEducation additionalEducation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EXAM_RESULTS_ID")
    protected DicExamResults examResults;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCEPTED_ACTION_ID")
    protected DicAcceptedAction acceptedAction;

    @Temporal(TemporalType.DATE)
    @Column(name = "REMOVING_DATE")
    protected Date removingDate;

    @Column(name = "REMOVING_ORDER_NUM")
    protected String removingOrderNum;

    @Temporal(TemporalType.DATE)
    @Column(name = "REMOVING_ORDER_DATE")
    protected Date removingOrderDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ASSIGNMENT_GROUP_ID")
    protected AssignmentGroupExt assignmentGroup;

    public void setAccidentCause(String accidentCause) {
        this.accidentCause = accidentCause;
    }

    public String getAccidentCause() {
        return accidentCause;
    }


    public void setResponsibleEmployee(PersonGroupExt responsibleEmployee) {
        this.responsibleEmployee = responsibleEmployee;
    }

    public PersonGroupExt getResponsibleEmployee() {
        return responsibleEmployee;
    }


    public void setNotifyEmployees(Boolean notifyEmployees) {
        this.notifyEmployees = notifyEmployees;
    }

    public Boolean getNotifyEmployees() {
        return notifyEmployees;
    }

    public void setLawArticle(DicLCArticle lawArticle) {
        this.lawArticle = lawArticle;
    }

    public DicLCArticle getLawArticle() {
        return lawArticle;
    }

    public void setEarlyTerminationReason(String earlyTerminationReason) {
        this.earlyTerminationReason = earlyTerminationReason;
    }

    public String getEarlyTerminationReason() {
        return earlyTerminationReason;
    }


    public void setAssignmentGroup(AssignmentGroupExt assignmentGroup) {
        this.assignmentGroup = assignmentGroup;
    }

    public AssignmentGroupExt getAssignmentGroup() {
        return assignmentGroup;
    }


    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setType(DicPunishmentTypes type) {
        this.type = type;
    }

    public DicPunishmentTypes getType() {
        return type;
    }

    public void setAccident(String accident) {
        this.accident = accident;
    }

    public String getAccident() {
        return accident;
    }

    public void setOffenceType(DicOffenceType offenceType) {
        this.offenceType = offenceType;
    }

    public DicOffenceType getOffenceType() {
        return offenceType;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setIsSafetyEngineering(Boolean isSafetyEngineering) {
        this.isSafetyEngineering = isSafetyEngineering;
    }

    public Boolean getIsSafetyEngineering() {
        return isSafetyEngineering;
    }

    public void setPeriod(Date period) {
        this.period = period;
    }

    public Date getPeriod() {
        return period;
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

    public void setDepression(Integer depression) {
        this.depression = depression;
    }

    public Integer getDepression() {
        return depression;
    }

    public void setLedToAccident(Boolean ledToAccident) {
        this.ledToAccident = ledToAccident;
    }

    public Boolean getLedToAccident() {
        return ledToAccident;
    }

    public void setAdditionalEducation(DicAdditionalEducation additionalEducation) {
        this.additionalEducation = additionalEducation;
    }

    public DicAdditionalEducation getAdditionalEducation() {
        return additionalEducation;
    }

    public void setExamResults(DicExamResults examResults) {
        this.examResults = examResults;
    }

    public DicExamResults getExamResults() {
        return examResults;
    }

    public void setAcceptedAction(DicAcceptedAction acceptedAction) {
        this.acceptedAction = acceptedAction;
    }

    public DicAcceptedAction getAcceptedAction() {
        return acceptedAction;
    }

    public void setRemovingDate(Date removingDate) {
        this.removingDate = removingDate;
    }

    public Date getRemovingDate() {
        return removingDate;
    }

    public void setRemovingOrderNum(String removingOrderNum) {
        this.removingOrderNum = removingOrderNum;
    }

    public String getRemovingOrderNum() {
        return removingOrderNum;
    }

    public void setRemovingOrderDate(Date removingOrderDate) {
        this.removingOrderDate = removingOrderDate;
    }

    public Date getRemovingOrderDate() {
        return removingOrderDate;
    }


}