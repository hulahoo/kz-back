package kz.uco.tsadv.modules.personal.model;

import com.haulmont.cuba.core.entity.annotation.Listeners;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.modules.personal.dictionary.DicEarningPolicy;
import kz.uco.tsadv.modules.personal.dictionary.DicSchedulePurpose;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.timesheet.model.StandardSchedule;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Listeners("tsadv_ScheduleOffsetsRequestChangedListener")
@Table(name = "TSADV_SCHEDULE_OFFSETS_REQUEST")
@Entity(name = "tsadv_ScheduleOffsetsRequest")
public class ScheduleOffsetsRequest extends AbstractBprocRequest {
    private static final long serialVersionUID = -3632866466350404079L;

    public static final String PROCESS_DEFINITION_KEY = "scheduleOffsetsRequest";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @OnDelete(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PURPOSE_ID")
    protected DicSchedulePurpose purpose;

    @Column(name = "PURPOSE_TEXT", length = 2000)
    protected String purposeText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CURRENT_SCHEDULE_ID")
    protected StandardSchedule currentSchedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NEW_SCHEDULE_ID")
    protected StandardSchedule newSchedule;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_OF_NEW_SCHEDULE")
    protected Date dateOfNewSchedule;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_OF_START_NEW_SCHEDULE")
    protected Date dateOfStartNewSchedule;

    @Column(name = "DETAILS_OF_ACTUAL_WORK", length = 2000)
    protected String detailsOfActualWork;

    @NotNull
    @Column(name = "AGREE", nullable = false)
    protected Boolean agree = false;

    @NotNull
    @Column(name = "ACQUAINTED", nullable = false)
    protected Boolean acquainted = false;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "EARNING_POLICY_ID")
    protected DicEarningPolicy earningPolicy;

    public DicEarningPolicy getEarningPolicy() {
        return earningPolicy;
    }

    public void setEarningPolicy(DicEarningPolicy earningPolicy) {
        this.earningPolicy = earningPolicy;
    }

    public String getPurposeText() {
        return purposeText;
    }

    public void setPurposeText(String purposeText) {
        this.purposeText = purposeText;
    }

    public Boolean getAcquainted() {
        return acquainted;
    }

    public void setAcquainted(Boolean acquainted) {
        this.acquainted = acquainted;
    }

    public Boolean getAgree() {
        return agree;
    }

    public void setAgree(Boolean agree) {
        this.agree = agree;
    }

    public String getDetailsOfActualWork() {
        return detailsOfActualWork;
    }

    public void setDetailsOfActualWork(String detailsOfActualWork) {
        this.detailsOfActualWork = detailsOfActualWork;
    }

    public Date getDateOfStartNewSchedule() {
        return dateOfStartNewSchedule;
    }

    public void setDateOfStartNewSchedule(Date dateOfStartNewSchedule) {
        this.dateOfStartNewSchedule = dateOfStartNewSchedule;
    }

    public Date getDateOfNewSchedule() {
        return dateOfNewSchedule;
    }

    public void setDateOfNewSchedule(Date dateOfNewSchedule) {
        this.dateOfNewSchedule = dateOfNewSchedule;
    }

    public StandardSchedule getNewSchedule() {
        return newSchedule;
    }

    public void setNewSchedule(StandardSchedule newSchedule) {
        this.newSchedule = newSchedule;
    }

    public StandardSchedule getCurrentSchedule() {
        return currentSchedule;
    }

    public void setCurrentSchedule(StandardSchedule currentSchedule) {
        this.currentSchedule = currentSchedule;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public DicSchedulePurpose getPurpose() {
        return purpose;
    }

    public void setPurpose(DicSchedulePurpose purpose) {
        this.purpose = purpose;
    }

    @PostConstruct
    public void postConstruct() {
        super.postConstruct();
    }

    @Override
    public String getProcessDefinitionKey() {
        return PROCESS_DEFINITION_KEY;
    }
}