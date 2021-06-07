package kz.uco.tsadv.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Table(name = "TSADV_VACATION_SCHEDULE_REQUEST")
@Entity(name = "tsadv_VacationScheduleRequest")
@NamePattern("%s - %s|startDate,endDate")
public class VacationScheduleRequest extends AbstractBprocRequest {
    private static final long serialVersionUID = 1975378160965313966L;

    @Lookup(type = LookupType.SCREEN, actions = "lookup")
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_GROUP_ID")
    private PersonGroupExt personGroup;

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE")
    @NotNull
    private Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE")
    @NotNull
    @Future
    private Date endDate;

    @Column(name = "ABSENCE_DAYS")
    @NotNull
    private Integer absenceDays;

    @Column(name = "BALANCE")
    private Integer balance;

    @NotNull
    @Column(name = "SENT_TO_ORACLE", nullable = false)
    private Boolean sentToOracle = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ATTACHMENT_ID")
    private FileDescriptor attachment;

    public FileDescriptor getAttachment() {
        return attachment;
    }

    public void setAttachment(FileDescriptor attachment) {
        this.attachment = attachment;
    }

    public Boolean getSentToOracle() {
        return sentToOracle;
    }

    public void setSentToOracle(Boolean sentToOracle) {
        this.sentToOracle = sentToOracle;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
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

    public Integer getAbsenceDays() {
        return absenceDays;
    }

    public void setAbsenceDays(Integer absenceDays) {
        this.absenceDays = absenceDays;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    @Override
    public String getProcessDefinitionKey() {
        throw new UnsupportedOperationException("VacationScheduleRequest does not have ProcessDefinitionKey ");
    }
}