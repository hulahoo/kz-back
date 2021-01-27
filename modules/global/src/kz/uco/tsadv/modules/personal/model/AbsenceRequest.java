package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.modules.personal.dictionary.DicAbsencePurpose;
import kz.uco.tsadv.modules.personal.dictionary.DicAbsenceType;
import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@NamePattern("%s (%s)|id,requestDate")
@Table(name = "TSADV_ABSENCE_REQUEST")
@Entity(name = "tsadv$AbsenceRequest")
public class AbsenceRequest extends AbstractBprocRequest {
    private static final long serialVersionUID = 5087051995273747332L;

    public static final String PROCESS_DEFINITION_KEY = "absenceRequest";

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ASSIGNMENT_GROUP_ID")
    protected AssignmentGroupExt assignmentGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ATTACHMENT_ID")
    protected FileDescriptor attachment;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_FROM")
    protected Date dateFrom;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_TO")
    protected Date dateTo;

    @Column(name = "ABSENCE_DAYS")
    protected Integer absenceDays;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TYPE_ID")
    protected DicAbsenceType type;

    @NotNull
    @Column(name = "DISTANCE_WORKING_CONFIRM", nullable = false)
    protected Boolean distanceWorkingConfirm = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PURPOSE_ID")
    protected DicAbsencePurpose purpose;

    @Column(name = "PURPOSE_TEXT", length = 2000)
    private String purposeText;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TIME_OF_STARTING")
    protected Date timeOfStarting;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TIME_OF_FINISHING")
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

    public String getPurposeText() {
        return purposeText;
    }

    public void setPurposeText(String purposeText) {
        this.purposeText = purposeText;
    }

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

    public DicAbsencePurpose getPurpose() {
        return purpose;
    }

    public void setPurpose(DicAbsencePurpose purpose) {
        this.purpose = purpose;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public void setDistanceWorkingConfirm(Boolean distanceWorkingConfirm) {
        this.distanceWorkingConfirm = distanceWorkingConfirm;
    }

    public Boolean getDistanceWorkingConfirm() {
        return distanceWorkingConfirm;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setAbsenceDays(Integer absenceDays) {
        this.absenceDays = absenceDays;
    }

    public Integer getAbsenceDays() {
        return absenceDays;
    }

    public void setType(DicAbsenceType type) {
        this.type = type;
    }

    public DicAbsenceType getType() {
        return type;
    }

    public void setAssignmentGroup(AssignmentGroupExt assignmentGroup) {
        this.assignmentGroup = assignmentGroup;
    }

    public AssignmentGroupExt getAssignmentGroup() {
        return assignmentGroup;
    }

    public void setAttachment(FileDescriptor attachment) {
        this.attachment = attachment;
    }

    public FileDescriptor getAttachment() {
        return attachment;
    }

    @Override
    public String getProcessDefinitionKey() {
        return PROCESS_DEFINITION_KEY;
    }
}