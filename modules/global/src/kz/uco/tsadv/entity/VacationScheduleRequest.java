package kz.uco.tsadv.entity;

import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Table(name = "TSADV_VACATION_SCHEDULE_REQUEST")
@Entity(name = "tsadv_VacationScheduleRequest")
public class VacationScheduleRequest extends AbstractParentEntity {
    private static final long serialVersionUID = 1975378160965313966L;

    @Column(name = "REQUEST_NUMBER", nullable = false)
    @NotNull
    private Long requestNumber;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "REQUEST_DATE", nullable = false)
    private Date requestDate;

    @Lookup(type = LookupType.SCREEN, actions = "lookup")
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_GROUP_ID")
    private PersonGroupExt personGroup;


    @Lookup(type = LookupType.SCREEN, actions = "lookup")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "STATUS_ID")
    @NotNull
    private AbsenceRequestStatus status;

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE")
    @NotNull
    private Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE")
    @NotNull
    private Date endDate;

    @Column(name = "ABSENCE_DAYS")
    @NotNull
    private Integer absenceDays;

    public void setRequestNumber(Long requestNumber) {
        this.requestNumber = requestNumber;
    }

    public Long getRequestNumber() {
        return requestNumber;
    }

    public void setStatus(AbsenceRequestStatus status) {
        this.status = status;
    }

    public AbsenceRequestStatus getStatus() {
        return status;
    }

    public Date getStartDate() {        return startDate;    }

    public void setStartDate(Date startDate) {        this.startDate = startDate;    }

    public Date getEndDate() {        return endDate;    }

    public void setEndDate(Date endDate) {        this.endDate = endDate;    }

    public Integer getAbsenceDays() {        return absenceDays;    }

    public void setAbsenceDays(Integer absenceDays) {        this.absenceDays = absenceDays;    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

}