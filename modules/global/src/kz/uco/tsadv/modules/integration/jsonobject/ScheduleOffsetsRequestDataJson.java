package kz.uco.tsadv.modules.integration.jsonobject;

import java.io.Serializable;

public class ScheduleOffsetsRequestDataJson implements Serializable {

    protected String personId;
    protected String requestNumber;
    protected String requestDate;
    protected String purpose;
    protected String currentScheduleId;
    protected String newScheduleId;
    protected String startDate;
    protected String endDate;
    protected String details;
    protected boolean isEmployeeAgree;
    protected boolean isEmployeeInformed;
    protected String earningPolicy;
    protected String companyCode;

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getRequestNumber() {
        return requestNumber;
    }

    public void setRequestNumber(String requestNumber) {
        this.requestNumber = requestNumber;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getCurrentScheduleId() {
        return currentScheduleId;
    }

    public void setCurrentScheduleId(String currentScheduleId) {
        this.currentScheduleId = currentScheduleId;
    }

    public String getNewScheduleId() {
        return newScheduleId;
    }

    public void setNewScheduleId(String newScheduleId) {
        this.newScheduleId = newScheduleId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public boolean isEmployeeAgree() {
        return isEmployeeAgree;
    }

    public void setEmployeeAgree(boolean employeeAgree) {
        isEmployeeAgree = employeeAgree;
    }

    public boolean isEmployeeInformed() {
        return isEmployeeInformed;
    }

    public void setEmployeeInformed(boolean employeeInformed) {
        isEmployeeInformed = employeeInformed;
    }

    public String getEarningPolicy() {
        return earningPolicy;
    }

    public void setEarningPolicy(String earningPolicy) {
        this.earningPolicy = earningPolicy;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }
}
