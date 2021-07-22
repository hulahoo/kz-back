package kz.uco.tsadv.modules.integration.jsonobject;

import java.io.Serializable;

public class PersonalDataRequestDataJson implements Serializable {

    protected String personId;
    protected String requestNumber;
    protected String lastName;
    protected String firstName;
    protected String middleName;
    protected String lastNameLatin;
    protected String firstNameLatin;
    protected String maritalStatus;
    protected boolean hasLoanFromPrevEmployer;
    protected boolean hasCreditFromPrevEmployer;
    protected boolean hasWealthFromPrevEmployer;
    protected boolean hasNdaFromPrevEmployer;
    protected boolean hasCriminalRecord;
    protected String effectiveDate;
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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastNameLatin() {
        return lastNameLatin;
    }

    public void setLastNameLatin(String lastNameLatin) {
        this.lastNameLatin = lastNameLatin;
    }

    public String getFirstNameLatin() {
        return firstNameLatin;
    }

    public void setFirstNameLatin(String firstNameLatin) {
        this.firstNameLatin = firstNameLatin;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public boolean isHasLoanFromPrevEmployer() {
        return hasLoanFromPrevEmployer;
    }

    public void setHasLoanFromPrevEmployer(boolean hasLoanFromPrevEmployer) {
        this.hasLoanFromPrevEmployer = hasLoanFromPrevEmployer;
    }

    public boolean isHasCreditFromPrevEmployer() {
        return hasCreditFromPrevEmployer;
    }

    public void setHasCreditFromPrevEmployer(boolean hasCreditFromPrevEmployer) {
        this.hasCreditFromPrevEmployer = hasCreditFromPrevEmployer;
    }

    public boolean isHasWealthFromPrevEmployer() {
        return hasWealthFromPrevEmployer;
    }

    public void setHasWealthFromPrevEmployer(boolean hasWealthFromPrevEmployer) {
        this.hasWealthFromPrevEmployer = hasWealthFromPrevEmployer;
    }

    public boolean isHasNdaFromPrevEmployer() {
        return hasNdaFromPrevEmployer;
    }

    public void setHasNdaFromPrevEmployer(boolean hasNdaFromPrevEmployer) {
        this.hasNdaFromPrevEmployer = hasNdaFromPrevEmployer;
    }

    public boolean isHasCriminalRecord() {
        return hasCriminalRecord;
    }

    public void setHasCriminalRecord(boolean hasCriminalRecord) {
        this.hasCriminalRecord = hasCriminalRecord;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }
}
