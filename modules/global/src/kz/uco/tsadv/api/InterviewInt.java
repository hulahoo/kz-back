package kz.uco.tsadv.api;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.tsadv.api.AbstractEntityInt;

import java.util.UUID;

@NamePattern("%s|id")
@MetaClass(name = "tsadv$InterviewInt")
public class InterviewInt extends AbstractEntityInt {
    private static final long serialVersionUID = -13435190413687312L;

    @MetaProperty
    protected String interviewDate;

    @MetaProperty
    protected String timeFrom;

    @MetaProperty
    protected String timeTo;

    @MetaProperty
    protected String place;

    @MetaProperty
    protected String hiringStep;

    @MetaProperty
    protected UUID requisition;

    @MetaProperty
    protected String requisitionCode;

    @MetaProperty
    protected String requisitionJob;

    @MetaProperty
    protected String interviewStatus;

    @MetaProperty
    protected String reason;

    public void setInterviewDate(String interviewDate) {
        this.interviewDate = interviewDate;
    }

    public String getInterviewDate() {
        return interviewDate;
    }

    public void setTimeFrom(String timeFrom) {
        this.timeFrom = timeFrom;
    }

    public String getTimeFrom() {
        return timeFrom;
    }

    public void setTimeTo(String timeTo) {
        this.timeTo = timeTo;
    }

    public String getTimeTo() {
        return timeTo;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getPlace() {
        return place;
    }

    public void setHiringStep(String hiringStep) {
        this.hiringStep = hiringStep;
    }

    public String getHiringStep() {
        return hiringStep;
    }

    public void setRequisition(UUID requisition) {
        this.requisition = requisition;
    }

    public UUID getRequisition() {
        return requisition;
    }

    public void setRequisitionCode(String requisitionCode) {
        this.requisitionCode = requisitionCode;
    }

    public String getRequisitionCode() {
        return requisitionCode;
    }

    public void setRequisitionJob(String requisitionJob) {
        this.requisitionJob = requisitionJob;
    }

    public String getRequisitionJob() {
        return requisitionJob;
    }

    public void setInterviewStatus(String interviewStatus) {
        this.interviewStatus = interviewStatus;
    }

    public String getInterviewStatus() {
        return interviewStatus;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}