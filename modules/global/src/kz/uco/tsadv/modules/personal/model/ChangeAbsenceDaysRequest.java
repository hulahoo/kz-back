package kz.uco.tsadv.modules.personal.model;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.modules.personal.dictionary.DicPurposeAbsence;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.recruitment.dictionary.DicRequisitionType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Listeners("tsadv_ChangeAbsenceDaysRequestListener")
@Table(name = "TSADV_CHANGE_ABSENCE_DAYS_REQUEST")
@Entity(name = "tsadv_ChangeAbsenceDaysRequest")
public class ChangeAbsenceDaysRequest extends AbstractBprocRequest {
    private static final long serialVersionUID = -6664546348307288527L;

    public static final String PROCESS_DEFINITION_KEY = "changeAbsenceDaysRequest";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUEST_TYPE_ID")
    protected DicRequisitionType requestType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMPLOYEE_ID")
    protected PersonGroupExt employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VACATION_ID")
    protected Absence vacation;

    @Temporal(TemporalType.DATE)
    @Column(name = "SCHEDULE_START_DATE")
    protected Date scheduleStartDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "SCHEDULE_END_DATE")
    protected Date scheduleEndDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "NEW_START_DATE")
    protected Date newStartDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "NEW_END_DATE")
    protected Date newEndDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "PERIOD_START_DATE")
    protected Date periodStartDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "PERIOD_END_DATE")
    protected Date periodEndDate;

    @NotNull
    @Column(name = "AGREE", nullable = false)
    protected Boolean agree = false;

    @NotNull
    @Column(name = "FAMILIARIZATION", nullable = false)
    protected Boolean familiarization = false;

    @JoinTable(name = "TSADV_CHANGE_ABSENCE_DAYS_REQUEST_FILE_DESCRIPTOR_LINK",
            joinColumns = @JoinColumn(name = "CHANGE_ABSENCE_DAYS_REQUEST_ID"),
            inverseJoinColumns = @JoinColumn(name = "FILE_DESCRIPTOR_ID"))
    @OnDelete(DeletePolicy.CASCADE)
    @ManyToMany
    protected List<FileDescriptor> file;

    @Lob
    @Column(name = "PURPOSE_TEXT")
    protected String purposeText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PURPOSE_ID")
    protected DicPurposeAbsence purpose;

    public void setPurpose(DicPurposeAbsence purpose) {
        this.purpose = purpose;
    }

    public DicPurposeAbsence getPurpose() {
        return purpose;
    }

    public String getPurposeText() {
        return purposeText;
    }

    public void setPurposeText(String purposeText) {
        this.purposeText = purposeText;
    }

    public List<FileDescriptor> getFile() {
        return file;
    }

    public void setFile(List<FileDescriptor> file) {
        this.file = file;
    }

    public Boolean getFamiliarization() {
        return familiarization;
    }

    public void setFamiliarization(Boolean familiarization) {
        this.familiarization = familiarization;
    }

    public Boolean getAgree() {
        return agree;
    }

    public void setAgree(Boolean agree) {
        this.agree = agree;
    }

    public Date getPeriodEndDate() {
        return periodEndDate;
    }

    public void setPeriodEndDate(Date periodEndDate) {
        this.periodEndDate = periodEndDate;
    }

    public Date getPeriodStartDate() {
        return periodStartDate;
    }

    public void setPeriodStartDate(Date periodStartDate) {
        this.periodStartDate = periodStartDate;
    }

    public Date getNewEndDate() {
        return newEndDate;
    }

    public void setNewEndDate(Date newEndDate) {
        this.newEndDate = newEndDate;
    }

    public Date getNewStartDate() {
        return newStartDate;
    }

    public void setNewStartDate(Date newStartDate) {
        this.newStartDate = newStartDate;
    }

    public Date getScheduleEndDate() {
        return scheduleEndDate;
    }

    public void setScheduleEndDate(Date scheduleEndDate) {
        this.scheduleEndDate = scheduleEndDate;
    }

    public Date getScheduleStartDate() {
        return scheduleStartDate;
    }

    public void setScheduleStartDate(Date scheduleStartDate) {
        this.scheduleStartDate = scheduleStartDate;
    }

    public Absence getVacation() {
        return vacation;
    }

    public void setVacation(Absence vacation) {
        this.vacation = vacation;
    }

    public PersonGroupExt getEmployee() {
        return employee;
    }

    public void setEmployee(PersonGroupExt employee) {
        this.employee = employee;
    }

    public DicRequisitionType getRequestType() {
        return requestType;
    }

    public void setRequestType(DicRequisitionType requestType) {
        this.requestType = requestType;
    }

    @Override
    public String getProcessDefinitionKey() {
        return PROCESS_DEFINITION_KEY;
    }
}