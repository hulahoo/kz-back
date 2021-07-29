package kz.uco.tsadv.modules.integration.jsonobject;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class PersonalDataRequestJson implements Serializable {

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

    @JsonProperty
    public String getPersonId() {
        return personId;
    }

    @JsonProperty
    public void setPersonId(String personId) {
        this.personId = personId;
    }

    @JsonProperty
    public String getRequestNumber() {
        return requestNumber;
    }

    @JsonProperty
    public void setRequestNumber(String requestNumber) {
        this.requestNumber = requestNumber;
    }

    @JsonProperty
    public String getLastName() {
        return lastName;
    }

    @JsonProperty
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @JsonProperty
    public String getFirstName() {
        return firstName;
    }

    @JsonProperty
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @JsonProperty
    public String getMiddleName() {
        return middleName;
    }

    @JsonProperty
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    @JsonProperty
    public String getLastNameLatin() {
        return lastNameLatin;
    }

    @JsonProperty
    public void setLastNameLatin(String lastNameLatin) {
        this.lastNameLatin = lastNameLatin;
    }

    @JsonProperty
    public String getFirstNameLatin() {
        return firstNameLatin;
    }

    @JsonProperty
    public void setFirstNameLatin(String firstNameLatin) {
        this.firstNameLatin = firstNameLatin;
    }

    @JsonProperty
    public String getMaritalStatus() {
        return maritalStatus;
    }

    @JsonProperty
    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    @JsonProperty
    public boolean isHasLoanFromPrevEmployer() {
        return hasLoanFromPrevEmployer;
    }

    @JsonProperty
    public void setHasLoanFromPrevEmployer(boolean hasLoanFromPrevEmployer) {
        this.hasLoanFromPrevEmployer = hasLoanFromPrevEmployer;
    }

    @JsonProperty
    public boolean isHasCreditFromPrevEmployer() {
        return hasCreditFromPrevEmployer;
    }

    @JsonProperty
    public void setHasCreditFromPrevEmployer(boolean hasCreditFromPrevEmployer) {
        this.hasCreditFromPrevEmployer = hasCreditFromPrevEmployer;
    }

    @JsonProperty
    public boolean isHasWealthFromPrevEmployer() {
        return hasWealthFromPrevEmployer;
    }

    @JsonProperty
    public void setHasWealthFromPrevEmployer(boolean hasWealthFromPrevEmployer) {
        this.hasWealthFromPrevEmployer = hasWealthFromPrevEmployer;
    }

    @JsonProperty
    public boolean isHasNdaFromPrevEmployer() {
        return hasNdaFromPrevEmployer;
    }

    @JsonProperty
    public void setHasNdaFromPrevEmployer(boolean hasNdaFromPrevEmployer) {
        this.hasNdaFromPrevEmployer = hasNdaFromPrevEmployer;
    }

    @JsonProperty
    public boolean isHasCriminalRecord() {
        return hasCriminalRecord;
    }

    @JsonProperty
    public void setHasCriminalRecord(boolean hasCriminalRecord) {
        this.hasCriminalRecord = hasCriminalRecord;
    }

    @JsonProperty
    public String getEffectiveDate() {
        return effectiveDate;
    }

    @JsonProperty
    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    @JsonProperty
    public String getCompanyCode() {
        return companyCode;
    }

    @JsonProperty
    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }
}
