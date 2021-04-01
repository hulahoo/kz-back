package kz.uco.tsadv.modules.integration.jsonobject;

import java.io.Serializable;

public class ChangeAbsenceDaysRequestDataJson implements Serializable {

    protected String personId;
    protected String requestNumber;
    protected String requestDate;
    protected String parentAbsenceLegacyId;
    protected String startDate;
    protected String endDate;
    protected String watchStartDate;
    protected String watchEndDate;
    protected String purpose;
    protected boolean isEmployeeAgree;
    protected boolean isEmployeeInformed;
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

    public String getParentAbsenceLegacyId() {
        return parentAbsenceLegacyId;
    }

    public void setParentAbsenceLegacyId(String parentAbsenceLegacyId) {
        this.parentAbsenceLegacyId = parentAbsenceLegacyId;
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

    public String getWatchStartDate() {
        return watchStartDate;
    }

    public void setWatchStartDate(String watchStartDate) {
        this.watchStartDate = watchStartDate;
    }

    public String getWatchEndDate() {
        return watchEndDate;
    }

    public void setWatchEndDate(String watchEndDate) {
        this.watchEndDate = watchEndDate;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
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

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }
}
