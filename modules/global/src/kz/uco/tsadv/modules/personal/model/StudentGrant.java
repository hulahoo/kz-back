package kz.uco.tsadv.modules.personal.model;

import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.learning.dictionary.DicLearningForm;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import java.util.Date;

@Table(name = "TSADV_STUDENT_GRANT")
@Entity(name = "tsadv$StudentGrant")
public class StudentGrant extends AbstractParentEntity {
    private static final long serialVersionUID = -6323295829576036817L;

    @Column(name = "NUMBER_CONTRACT")
    protected String numberContract;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_CONTRACT")
    protected Date dateContract;

    @Column(name = "LEARNING_CENTER")
    protected String learningCenter;

    @Column(name = "SPECIALIZATION", length = 500)
    protected String specialization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LEARNING_FORM_ID")
    protected DicLearningForm learningForm;

    @Temporal(TemporalType.DATE)
    @Column(name = "LEARN_END_DATE")
    protected Date learnEndDate;

    @Column(name = "REASON", length = 500)
    protected String reason;

    @Column(name = "ORDER_REQUISITION", length = 1000)
    protected String orderRequisition;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    public void setLearningForm(DicLearningForm learningForm) {
        this.learningForm = learningForm;
    }

    public DicLearningForm getLearningForm() {
        return learningForm;
    }


    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }


    public void setNumberContract(String numberContract) {
        this.numberContract = numberContract;
    }

    public String getNumberContract() {
        return numberContract;
    }

    public void setDateContract(Date dateContract) {
        this.dateContract = dateContract;
    }

    public Date getDateContract() {
        return dateContract;
    }

    public void setLearningCenter(String learningCenter) {
        this.learningCenter = learningCenter;
    }

    public String getLearningCenter() {
        return learningCenter;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setLearnEndDate(Date learnEndDate) {
        this.learnEndDate = learnEndDate;
    }

    public Date getLearnEndDate() {
        return learnEndDate;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setOrderRequisition(String orderRequisition) {
        this.orderRequisition = orderRequisition;
    }

    public String getOrderRequisition() {
        return orderRequisition;
    }


}