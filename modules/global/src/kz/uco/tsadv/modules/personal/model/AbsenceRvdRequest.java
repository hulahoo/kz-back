package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.modules.personal.dictionary.DicAbsencePurpose;
import kz.uco.tsadv.modules.personal.dictionary.DicAbsenceType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import java.util.Date;

@Table(name = "TSADV_ABSENCE_RVD_REQUEST")
@Entity(name = "tsadv_AbsenceRvdRequest")
@NamePattern("%s (%s)|id,requestDate")
public class AbsenceRvdRequest extends AbstractBprocRequest {
    protected static final long serialVersionUID = -2522803587477282283L;

    public static final String PROCESS_DEFINITION_KEY = "absenceRvdRequest";

    @JoinColumn(name = "PERSON_GROUP_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    protected PersonGroupExt personGroup;

    @JoinColumn(name = "TYPE_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    protected DicAbsenceType type;

    @JoinColumn(name = "PURPOSE_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    protected DicAbsencePurpose purpose;

    @Column(name = "PURPOSE_TEXT", length = 2000)
    protected String purposeText;

    @Column(name = "TIME_OF_STARTING")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date timeOfStarting;

    @Column(name = "TIME_OF_FINISHING")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date timeOfFinishing;

    @Column(name = "TOTAL_HOURS")
    protected Integer totalHours;

    @Column(name = "COMPENCATION")
    protected Boolean compencation;

    @Column(name = "VACATION_DAY")
    protected Boolean vacationDay;

    @Column(name = "ACQUAINTED")
    protected Boolean acquainted;

    @Column(name = "AGREE")
    protected Boolean agree;

    public Boolean getAgree() {
        return agree;
    }

    public void setAgree(Boolean agree) {
        this.agree = agree;
    }

    public Boolean getAcquainted() {
        return acquainted;
    }

    public void setAcquainted(Boolean acquainted) {
        this.acquainted = acquainted;
    }

    public Boolean getVacationDay() {
        return vacationDay;
    }

    public void setVacationDay(Boolean vacationDay) {
        this.vacationDay = vacationDay;
    }

    public Boolean getCompencation() {
        return compencation;
    }

    public void setCompencation(Boolean compencation) {
        this.compencation = compencation;
    }

    public Integer getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(Integer totalHours) {
        this.totalHours = totalHours;
    }

    public Date getTimeOfFinishing() {
        return timeOfFinishing;
    }

    public void setTimeOfFinishing(Date timeOfFinishing) {
        this.timeOfFinishing = timeOfFinishing;
    }

    public Date getTimeOfStarting() {
        return timeOfStarting;
    }

    public void setTimeOfStarting(Date timeOfStarting) {
        this.timeOfStarting = timeOfStarting;
    }

    public String getPurposeText() {
        return purposeText;
    }

    public void setPurposeText(String purposeText) {
        this.purposeText = purposeText;
    }

    public DicAbsencePurpose getPurpose() {
        return purpose;
    }

    public void setPurpose(DicAbsencePurpose purpose) {
        this.purpose = purpose;
    }

    public DicAbsenceType getType() {
        return type;
    }

    public void setType(DicAbsenceType type) {
        this.type = type;
    }


    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    @Override
    public String getProcessDefinitionKey() {
        return PROCESS_DEFINITION_KEY;
    }
}