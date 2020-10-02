package kz.uco.tsadv.modules.timesheet.model;

import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.persistence.Lob;

@Table(name = "TSADV_TIMECARD_LOG")
@Entity(name = "tsadv$TimecardLog")
public class TimecardLog extends AbstractParentEntity {
    private static final long serialVersionUID = 2731335172808995649L;

    @NotNull
    @Column(name = "INITIATOR_LOGIN", nullable = false)
    protected String initiatorLogin;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    @Column(name = "START_DATE", nullable = false)
    protected Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    @Column(name = "END_DATE", nullable = false)
    protected Date endDate;

    @NotNull
    @Column(name = "SUCCESS", nullable = false)
    protected Boolean success = false;

    @Lob
    @Column(name = "ERROR_TEXT")
    protected String errorText;

    @Column(name = "DURATION_IN_SECONDS")
    protected Long durationInSeconds;

    @Column(name = "TIMECARDS_COUNT")
    protected Integer timecardsCount;

    public void setTimecardsCount(Integer timecardsCount) {
        this.timecardsCount = timecardsCount;
    }

    public Integer getTimecardsCount() {
        return timecardsCount;
    }


    public void setDurationInSeconds(Long durationInSeconds) {
        this.durationInSeconds = durationInSeconds;
    }

    public Long getDurationInSeconds() {
        return durationInSeconds;
    }


    public void setInitiatorLogin(String initiatorLogin) {
        this.initiatorLogin = initiatorLogin;
    }

    public String getInitiatorLogin() {
        return initiatorLogin;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    public String getErrorText() {
        return errorText;
    }



}