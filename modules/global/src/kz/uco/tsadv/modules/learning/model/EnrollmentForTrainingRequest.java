package kz.uco.tsadv.modules.learning.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import kz.uco.tsadv.modules.learning.enums.EnrollmentStatus;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import com.haulmont.cuba.core.entity.StandardEntity;

@Table(name = "TSADV_ENROLLMENT_FOR_TRAINING_REQUEST")
@Entity(name = "tsadv$EnrollmentForTrainingRequest")
public class EnrollmentForTrainingRequest extends StandardEntity {
    private static final long serialVersionUID = -2564301305788879295L;

    @Column(name = "REASON", length = 1000)
    protected String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @Column(name = "STATUS")
    protected Integer status;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_")
    protected Date date;

    @Column(name = "MONEY_IN_BUDGET")
    protected Boolean moneyInBudget;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRAINING_REQUEST_ID")
    protected TrainingRequest trainingRequest;

    public void setTrainingRequest(TrainingRequest trainingRequest) {
        this.trainingRequest = trainingRequest;
    }

    public TrainingRequest getTrainingRequest() {
        return trainingRequest;
    }


    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setStatus(EnrollmentStatus status) {
        this.status = status == null ? null : status.getId();
    }

    public EnrollmentStatus getStatus() {
        return status == null ? null : EnrollmentStatus.fromId(status);
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setMoneyInBudget(Boolean moneyInBudget) {
        this.moneyInBudget = moneyInBudget;
    }

    public Boolean getMoneyInBudget() {
        return moneyInBudget;
    }


}