package kz.uco.tsadv.modules.integration.jsonobject;

import java.io.Serializable;

public class AbsenceRvdRequestDataJson implements Serializable {

    protected String personId;
    protected String requestNumber;
    protected String requestDate;
    protected String purpose;
    protected String startDate;
    protected String endDate;
    protected String hours;
    protected String isVacationProvidedOrCompensationProvided;
    protected boolean isEmployeeAgree;
    protected boolean isEmployeeInformed;
    protected String shiftCode;
    protected String startTime;
    protected String endTime;
    protected String companyCode;
    protected String shift;
    protected boolean overridebyallhours;

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

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getIsVacationProvidedOrCompensationProvided() {
        return isVacationProvidedOrCompensationProvided;
    }

    public void setIsVacationProvidedOrCompensationProvided(String isVacationProvidedOrCompensationProvided) {
        this.isVacationProvidedOrCompensationProvided = isVacationProvidedOrCompensationProvided;
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

    public String getShiftCode() {
        return shiftCode;
    }

    public void setShiftCode(String shiftCode) {
        this.shiftCode = shiftCode;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public boolean isOverridebyallhours() {
        return overridebyallhours;
    }

    public void setOverridebyallhours(boolean overridebyallhours) {
        this.overridebyallhours = overridebyallhours;
    }
}
