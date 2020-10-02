package kz.uco.tsadv.modules.performance.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import com.haulmont.chile.core.annotations.MetaProperty;
import kz.uco.tsadv.modules.learning.model.PartyExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import java.util.List;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s|trainerFullName")
@Table(name = "TSADV_TRAINER")
@Entity(name = "tsadv$Trainer")
public class Trainer extends AbstractParentEntity {
    private static final long serialVersionUID = -6155074646105418146L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "EMPLOYEE_ID")
    protected PersonGroupExt employee;

    @Column(name = "ADD_PAYMENT_AMOUNT")
    protected Integer addPaymentAmount;

    @Column(name = "ORDER_NUMBER")
    protected String orderNumber;

    @Temporal(TemporalType.DATE)
    @Column(name = "ORDER_DATE")
    protected Date orderDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARTY_ID")
    protected PartyExt party;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "trainer")
    protected List<CourseTrainer> courseTrainer;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "trainer")
    protected List<CourseTrainerAssessment> courseTrainerAssessment;

    @Transient
    @MetaProperty
    protected String trainerFullName;

    public String getTrainerFullName() {
        return employee.getPerson().getFullName();
    }


    public void setCourseTrainer(List<CourseTrainer> courseTrainer) {
        this.courseTrainer = courseTrainer;
    }

    public List<CourseTrainer> getCourseTrainer() {
        return courseTrainer;
    }

    public void setCourseTrainerAssessment(List<CourseTrainerAssessment> courseTrainerAssessment) {
        this.courseTrainerAssessment = courseTrainerAssessment;
    }

    public List<CourseTrainerAssessment> getCourseTrainerAssessment() {
        return courseTrainerAssessment;
    }


    public void setEmployee(PersonGroupExt employee) {
        this.employee = employee;
    }

    public PersonGroupExt getEmployee() {
        return employee;
    }

    public void setAddPaymentAmount(Integer addPaymentAmount) {
        this.addPaymentAmount = addPaymentAmount;
    }

    public Integer getAddPaymentAmount() {
        return addPaymentAmount;
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

    public void setParty(PartyExt party) {
        this.party = party;
    }

    public PartyExt getParty() {
        return party;
    }

    }