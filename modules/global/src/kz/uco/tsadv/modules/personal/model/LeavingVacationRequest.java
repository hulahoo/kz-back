package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.FileDescriptor;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Table(name = "TSADV_LEAVING_VACATION_REQUEST")
@Entity(name = "tsadv$LeavingVacationRequest")
@NamePattern("%s %s %s|id,startDate,endDate")
public class LeavingVacationRequest extends AbstractBprocRequest {
    private static final long serialVersionUID = -3518404858438386443L;

    public static final String PROCESS_DEFINITION_KEY = "leavingVacationRequest";

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VACATION_ID")
    private Absence vacation;

    @Column(name = "START_DATE")
    @NotNull
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Column(name = "END_DATE")
    @NotNull
    @Temporal(TemporalType.DATE)
    private Date endDate;

    @Column(name = "PLANNED_START_DATE")
    @NotNull
    @Temporal(TemporalType.DATE)
    private Date plannedStartDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ATTACHMENT_ID")
    private FileDescriptor attachment;

    public FileDescriptor getAttachment() {
        return attachment;
    }

    public void setAttachment(FileDescriptor attachment) {
        this.attachment = attachment;
    }

    @Override
    public String getProcessDefinitionKey() {
        return PROCESS_DEFINITION_KEY;
    }

    public Absence getVacation() {
        return vacation;
    }

    public void setVacation(Absence vacation) {
        this.vacation = vacation;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getPlannedStartDate() {
        return plannedStartDate;
    }

    public void setPlannedStartDate(Date plannedStartDate) {
        this.plannedStartDate = plannedStartDate;
    }
}