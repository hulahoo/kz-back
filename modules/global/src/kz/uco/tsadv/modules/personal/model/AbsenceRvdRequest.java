package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.entity.annotation.PublishEntityChangedEvents;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.modules.personal.dictionary.DicAbsencePurpose;
import kz.uco.tsadv.modules.personal.dictionary.DicAbsenceType;
import kz.uco.tsadv.modules.personal.enums.YesNoEnum;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@PublishEntityChangedEvents
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

    @Column(name = "COMPENSATION", nullable = false)
    @NotNull
    protected Boolean compensation = false;

    @Column(name = "VACATION_DAY", nullable = false)
    @NotNull
    protected Boolean vacationDay = false;

    @Column(name = "ACQUAINTED", nullable = false)
    @NotNull
    protected Boolean acquainted = false;

    @Column(name = "AGREE", nullable = false)
    @NotNull
    protected Boolean agree = false;

    @Column(name = "SHIFT_CODE")
    private String shiftCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SHIFT_ID")
    private DicShift shift;

    @Column(name = "OVERRIDE_ALL_HOURS_BY_DAY")
    private String overrideAllHoursByDay;

    @JoinTable(name = "TSADV_ABSENCE_RVD_REQUEST_FILE_DESCRIPTOR_LINK",
            joinColumns = @JoinColumn(name = "ABSENCE_RVD_REQUEST_ID"),
            inverseJoinColumns = @JoinColumn(name = "FILE_DESCRIPTOR_ID"))
    @OnDelete(DeletePolicy.CASCADE)
    @ManyToMany
    @Composition
    private List<FileDescriptor> files;

    public YesNoEnum getOverrideAllHoursByDay() {
        return overrideAllHoursByDay == null ? null : YesNoEnum.fromId(overrideAllHoursByDay);
    }

    public void setOverrideAllHoursByDay(YesNoEnum overrideAllHoursByDay) {
        this.overrideAllHoursByDay = overrideAllHoursByDay == null ? null : overrideAllHoursByDay.getId();
    }

    public DicShift getShift() {
        return shift;
    }

    public void setShift(DicShift shift) {
        this.shift = shift;
    }

    public String getShiftCode() {
        return shiftCode;
    }

    public void setShiftCode(String shiftCode) {
        this.shiftCode = shiftCode;
    }

    public List<FileDescriptor> getFiles() {
        return files;
    }

    public void setFiles(List<FileDescriptor> files) {
        this.files = files;
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

    public Boolean getCompensation() {
        return compensation;
    }

    public void setCompensation(Boolean compensation) {
        this.compensation = compensation;
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

    @PostConstruct
    public void postConstruct() {
        super.postConstruct();
        this.compensation = true;
    }
}