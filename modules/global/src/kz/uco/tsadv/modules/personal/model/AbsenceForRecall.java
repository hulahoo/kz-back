package kz.uco.tsadv.modules.personal.model;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.modules.personal.dictionary.DicAbsencePurpose;
import kz.uco.tsadv.modules.personal.dictionary.DicAbsenceType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Listeners("tsadv_AbsenceForRecallListener")
@Table(name = "TSADV_ABSENCE_FOR_RECALL")
@Entity(name = "tsadv_AbsenceForRecall")
public class AbsenceForRecall extends AbstractBprocRequest {
    private static final long serialVersionUID = 1213611202573439060L;

    public static final String PROCESS_DEFINITION_KEY = "absenceForRecallRequest";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ABSENCE_TYPE_ID")
    protected DicAbsenceType absenceType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMPLOYEE_ID")
    protected PersonGroupExt employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VACATION_ID")
    protected Absence vacation;

    @Temporal(TemporalType.DATE)
    @Column(name = "RECALL_DATE_FROM")
    protected Date recallDateFrom;

    @Temporal(TemporalType.DATE)
    @Column(name = "RECALL_DATE_TO")
    protected Date recallDateTo;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_FROM")
    protected Date dateFrom;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_TO")
    protected Date dateTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PURPOSE_ID")
    protected DicAbsencePurpose purpose;

    @Column(name = "PURPOSE_TEXT")
    protected String purposeText;

    @NotNull
    @Column(name = "IS_AGREE", nullable = false)
    protected Boolean isAgree = false;

    @NotNull
    @Column(name = "IS_FAMILIARIZATION", nullable = false)
    protected Boolean isFamiliarization = false;

    @NotNull
    @Column(name = "LEAVE_OTHER_TIME", nullable = false)
    protected Boolean leaveOtherTime = false;

    @NotNull
    @Column(name = "COMPENSATION_PAYMENT", nullable = false)
    protected Boolean compensationPayment = false;

    @JoinTable(name = "TSADV_ABSENCE_FOR_RECALL_FILE_DESCRIPTOR_LINK",
            joinColumns = @JoinColumn(name = "ABSENCE_FOR_RECALL_ID"),
            inverseJoinColumns = @JoinColumn(name = "FILE_DESCRIPTOR_ID"))
    @OnDelete(DeletePolicy.CASCADE)
    @ManyToMany
    protected List<FileDescriptor> file;

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

    public Boolean getCompensationPayment() {
        return compensationPayment;
    }

    public void setCompensationPayment(Boolean compensationPayment) {
        this.compensationPayment = compensationPayment;
    }

    public Boolean getLeaveOtherTime() {
        return leaveOtherTime;
    }

    public void setLeaveOtherTime(Boolean leaveOtherTime) {
        this.leaveOtherTime = leaveOtherTime;
    }

    public Boolean getIsFamiliarization() {
        return isFamiliarization;
    }

    public void setIsFamiliarization(Boolean isFamiliarization) {
        this.isFamiliarization = isFamiliarization;
    }

    public Boolean getIsAgree() {
        return isAgree;
    }

    public void setIsAgree(Boolean isAgree) {
        this.isAgree = isAgree;
    }

    public DicAbsencePurpose getPurpose() {
        return purpose;
    }

    public void setPurpose(DicAbsencePurpose purpose) {
        this.purpose = purpose;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getRecallDateTo() {
        return recallDateTo;
    }

    public void setRecallDateTo(Date recallDateTo) {
        this.recallDateTo = recallDateTo;
    }

    public Date getRecallDateFrom() {
        return recallDateFrom;
    }

    public void setRecallDateFrom(Date recallDateFrom) {
        this.recallDateFrom = recallDateFrom;
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

    public DicAbsenceType getAbsenceType() {
        return absenceType;
    }

    public void setAbsenceType(DicAbsenceType absenceType) {
        this.absenceType = absenceType;
    }

    @Override
    public String getProcessDefinitionKey() {
        return PROCESS_DEFINITION_KEY;
    }

    @PostConstruct
    public void postConstruct() {
        super.postConstruct();
        leaveOtherTime = true;
    }
}