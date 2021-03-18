package kz.uco.tsadv.modules.integration.jsonobject;

import java.io.Serializable;

public class AbsenceRequestDataJson implements Serializable {

    protected String personId;
    protected String requestNumber;
    protected String absenceTypeId;
    protected String startDate;
    protected String endDate;
    protected int absenceDuration;
    protected String purpose;
    protected boolean isProvideSheetOfTemporary;
    protected boolean isOnRotation;
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

    public String getAbsenceTypeId() {
        return absenceTypeId;
    }

    public void setAbsenceTypeId(String absenceTypeId) {
        this.absenceTypeId = absenceTypeId;
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

    public int getAbsenceDuration() {
        return absenceDuration;
    }

    public void setAbsenceDuration(int absenceDuration) {
        this.absenceDuration = absenceDuration;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public boolean getIsProvideSheetOfTemporary() {
        return isProvideSheetOfTemporary;
    }

    public void setIsProvideSheetOfTemporary(boolean isProvideSheetOfTemporary) {
        this.isProvideSheetOfTemporary = isProvideSheetOfTemporary;
    }

    public boolean getIsOnRotation() {
        return isOnRotation;
    }

    public void setIsOnRotation(boolean isOnRotation) {
        this.isOnRotation = isOnRotation;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }
}
