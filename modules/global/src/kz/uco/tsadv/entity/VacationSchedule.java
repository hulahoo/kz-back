package kz.uco.tsadv.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Table(name = "TSADV_VACATION_SCHEDULE")
@Entity(name = "tsadv_VacationSchedule")
@NamePattern("%s(%s)|personGroup,absenceDays")
public class VacationSchedule extends AbstractParentEntity {
    private static final long serialVersionUID = -8297168813225443665L;

    @Lookup(type = LookupType.SCREEN, actions = "lookup")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_ID")
    @NotNull
    private PersonGroupExt personGroup;

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

    @Lookup(type = LookupType.SCREEN, actions = "lookup")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "STATUS_ID")
    @NotNull
    private AbsenceRequestStatus status;

    public void setStatus(AbsenceRequestStatus status) {
        this.status = status;
    }

    public AbsenceRequestStatus getStatus() {
        return status;
    }

    public void setAbsenceDays(Integer absenceDays) {
        this.absenceDays = absenceDays;
    }

    public Integer getAbsenceDays() {
        return absenceDays;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }
}