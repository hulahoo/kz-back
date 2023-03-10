package kz.uco.tsadv.modules.integration.jsonobject;

import java.io.Serializable;

public class AbsenceForRecallDataJson implements Serializable {

    protected String personId;
    protected String requestNumber;
    protected String requestDate;
    protected String parentAbsenceLegacyId;
    protected String startDate;
    protected String endDate;
    protected boolean hasAbsenceAtAnotherPeriod;
    protected boolean hasCompensation;
    protected String newStartDate;
    protected String newEndDate;
    protected String purpose;
    protected boolean isEmployeeAgree;
    protected boolean isEmployeeInformed;
    protected String recallDaysMain;
    protected String recallDaysEcological;
    protected String recallDaysHarmful;
    protected String recallDaysDisability;
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

    public boolean isHasAbsenceAtAnotherPeriod() {
        return hasAbsenceAtAnotherPeriod;
    }

    public void setHasAbsenceAtAnotherPeriod(boolean hasAbsenceAtAnotherPeriod) {
        this.hasAbsenceAtAnotherPeriod = hasAbsenceAtAnotherPeriod;
    }

    public boolean isHasCompensation() {
        return hasCompensation;
    }

    public void setHasCompensation(boolean hasCompensation) {
        this.hasCompensation = hasCompensation;
    }

    public String getNewStartDate() {
        return newStartDate;
    }

    public void setNewStartDate(String newStartDate) {
        this.newStartDate = newStartDate;
    }

    public String getNewEndDate() {
        return newEndDate;
    }

    public void setNewEndDate(String newEndDate) {
        this.newEndDate = newEndDate;
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

    public String getRecallDaysMain() {
        return recallDaysMain;
    }

    public void setRecallDaysMain(String recallDaysMain) {
        this.recallDaysMain = recallDaysMain;
    }

    public String getRecallDaysEcological() {
        return recallDaysEcological;
    }

    public void setRecallDaysEcological(String recallDaysEcological) {
        this.recallDaysEcological = recallDaysEcological;
    }

    public String getRecallDaysHarmful() {
        return recallDaysHarmful;
    }

    public void setRecallDaysHarmful(String recallDaysHarmful) {
        this.recallDaysHarmful = recallDaysHarmful;
    }

    public String getRecallDaysDisability() {
        return recallDaysDisability;
    }

    public void setRecallDaysDisability(String recallDaysDisability) {
        this.recallDaysDisability = recallDaysDisability;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }
}
