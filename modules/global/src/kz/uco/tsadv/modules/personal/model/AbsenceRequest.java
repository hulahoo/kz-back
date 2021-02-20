package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.core.global.UserSessionSource;
import kz.uco.tsadv.entity.VacationSchedule;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.dictionary.DicAbsencePurpose;
import kz.uco.tsadv.modules.personal.dictionary.DicAbsenceType;
import kz.uco.tsadv.modules.personal.enums.VacationDurationType;
import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.service.AssignmentService;
import kz.uco.tsadv.service.EmployeeService;

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

    @Column(name = "ORIGINAL_SHEET")
    private Boolean originalSheet;

    @Temporal(TemporalType.DATE)
    @Column(name = "SCHEDULE_START_DATE")
    private Date scheduleStartDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "SCHEDULE_END_DATE")
    private Date scheduleEndDate;

    @Column(name = "ADD_NEXT_YEAR")
    private Boolean addNextYear;

    @Temporal(TemporalType.DATE)
    @Column(name = "NEW_START_DATE")
    private Date newStartDate;

    @Column(name = "NEW_END_DATE")
    @Temporal(TemporalType.DATE)
    private Date newEndDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "PERIOD_DATE_FROM")
    private Date periodDateFrom;

    @Temporal(TemporalType.DATE)
    @Column(name = "PERIOD_DATE_TO")
    private Date periodDateTo;

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

    @JoinColumn(name = "VACATION_SCHEDULE_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private VacationSchedule vacationSchedule;

    @MetaProperty
    @Transient
    private String vacationDurationType;

    public VacationDurationType getVacationDurationType() {
        return vacationDurationType == null ? null : VacationDurationType.fromId(vacationDurationType);
    }

    public void setVacationDurationType(VacationDurationType vacationDurationType) {
        this.vacationDurationType = vacationDurationType == null ? null : vacationDurationType.getId();
    }

    public void setVacationSchedule(VacationSchedule vacationSchedule) {
        this.vacationSchedule = vacationSchedule;
    }

    public VacationSchedule getVacationSchedule() {
        return vacationSchedule;
    }

    public void setNewEndDate(Date newEndDate) {
        this.newEndDate = newEndDate;
    }

    public Date getNewEndDate() {
        return newEndDate;
    }

    public Boolean getAddNextYear() {
        return addNextYear;
    }

    public void setAddNextYear(Boolean addNextYear) {
        this.addNextYear = addNextYear;
    }

    public Date getNewStartDate() {
        return newStartDate;
    }

    public void setNewStartDate(Date newStartDate) {
        this.newStartDate = newStartDate;
    }

    public Date getPeriodDateTo() {
        return periodDateTo;
    }

    public void setPeriodDateTo(Date periodDateTo) {
        this.periodDateTo = periodDateTo;
    }

    public Date getPeriodDateFrom() {
        return periodDateFrom;
    }

    public void setPeriodDateFrom(Date periodDateFrom) {
        this.periodDateFrom = periodDateFrom;
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

    public void setOriginalSheet(Boolean originalSheet) {
        this.originalSheet = originalSheet;
    }

    public Boolean getOriginalSheet() {
        return originalSheet;
    }

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

    @PrePersist
    public void prePersist() {
        if (this.assignmentGroup == null && this.personGroup == null) {
            AssignmentService assignmentService = AppBeans.get(AssignmentService.class);
            UserSessionSource userSessionSource = AppBeans.get(UserSessionSource.class);
            this.assignmentGroup = assignmentService.getAssignmentGroup(userSessionSource.getUserSession().getUser().getLogin());
        } else if (this.personGroup != null) {
            AssignmentExt assignmentExt = AppBeans.get(EmployeeService.class)
                    .getAssignmentExt(
                            this.personGroup.getId(),
                            CommonUtils.getSystemDate(),
                            "portal-assignment-group");
            if (assignmentExt != null && assignmentExt.getGroup() != null)
                this.assignmentGroup = assignmentExt.getGroup();
        }

        if (this.assignmentGroup != null && this.personGroup == null) {
            EmployeeService employeeService = AppBeans.get(EmployeeService.class);
            this.personGroup = employeeService.getPersonGroupByAssignmentGroupId(this.assignmentGroup.getId());
        }
    }
}