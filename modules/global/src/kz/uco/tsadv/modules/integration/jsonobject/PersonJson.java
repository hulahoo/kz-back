package kz.uco.tsadv.modules.integration.jsonobject;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class PersonJson implements Serializable {

    protected String lastName;
    protected String firstName;
    protected String middleName;
    protected String lastNameLatin;
    protected String firstNameLatin;
    protected String legacyId;
    protected String employeeNumber;
    protected String nationalIdentifier;
    protected String personTypeId;
    protected String startDate;
    protected String endDate;
    protected String dateOfBirth;
    protected String hireDate;
    protected String dateOfDeath;
    protected String nationalityId;
    protected String citizenshipId;
    protected String sexId;
    protected String placeOfBirth;
    protected String hasLoanFromPrevEmployer;
    protected String hasCreditFromPrevEmployer;
    protected String hasWealthFromPrevEmployer;
    protected String hasNdaFromPrevEmployer;
    protected String hasCriminalRecord;
    protected String companyCode;
    protected String maritalStatus;

    @JsonProperty("maritalStatus")
    public String getMaritalStatus() {
        return maritalStatus;
    }

    @JsonProperty("maritalStatus")
    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    @JsonProperty("lastName")
    public String getLastName() {
        return lastName;
    }

    @JsonProperty("lastName")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @JsonProperty("firstName")
    public String getFirstName() {
        return firstName;
    }

    @JsonProperty("firstName")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @JsonProperty("middleName")
    public String getMiddleName() {
        return middleName;
    }

    @JsonProperty("middleName")
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    @JsonProperty("lastNameLatin")
    public String getLastNameLatin() {
        return lastNameLatin;
    }

    @JsonProperty("lastNameLatin")
    public void setLastNameLatin(String lastNameLatin) {
        this.lastNameLatin = lastNameLatin;
    }

    @JsonProperty("firstNameLatin")
    public String getFirstNameLatin() {
        return firstNameLatin;
    }

    @JsonProperty("firstNameLatin")
    public void setFirstNameLatin(String firstNameLatin) {
        this.firstNameLatin = firstNameLatin;
    }

    @JsonProperty("legacyId")
    public String getLegacyId() {
        return legacyId;
    }

    @JsonProperty("legacyId")
    public void setLegacyId(String legacyId) {
        this.legacyId = legacyId;
    }

    @JsonProperty("employeeNumber")
    public String getEmployeeNumber() {
        return employeeNumber;
    }

    @JsonProperty("employeeNumber")
    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    @JsonProperty("nationalIdentifier")
    public String getNationalIdentifier() {
        return nationalIdentifier;
    }

    @JsonProperty("nationalIdentifier")
    public void setNationalIdentifier(String nationalIdentifier) {
        this.nationalIdentifier = nationalIdentifier;
    }

    @JsonProperty("personTypeId")
    public String getPersonTypeId() {
        return personTypeId;
    }

    @JsonProperty("personTypeId")
    public void setPersonTypeId(String personTypeId) {
        this.personTypeId = personTypeId;
    }

    @JsonProperty("startDate")
    public String getStartDate() {
        return startDate;
    }

    @JsonProperty("startDate")
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    @JsonProperty("endDate")
    public String getEndDate() {
        return endDate;
    }

    @JsonProperty("endDate")
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @JsonProperty("dateOfBirth")
    public String getDateOfBirth() {
        return dateOfBirth;
    }

    @JsonProperty("dateOfBirth")
    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @JsonProperty("hireDate")
    public String getHireDate() {
        return hireDate;
    }

    @JsonProperty("hireDate")
    public void setHireDate(String hireDate) {
        this.hireDate = hireDate;
    }

    @JsonProperty("dateOfDeath")
    public String getDateOfDeath() {
        return dateOfDeath;
    }

    @JsonProperty("dateOfDeath")
    public void setDateOfDeath(String dateOfDeath) {
        this.dateOfDeath = dateOfDeath;
    }

    @JsonProperty("nationalityId")
    public String getNationalityId() {
        return nationalityId;
    }

    @JsonProperty("nationalityId")
    public void setNationalityId(String nationalityId) {
        this.nationalityId = nationalityId;
    }

    @JsonProperty("citizenshipId")
    public String getCitizenshipId() {
        return citizenshipId;
    }

    @JsonProperty("citizenshipId")
    public void setCitizenshipId(String citizenshipId) {
        this.citizenshipId = citizenshipId;
    }

    @JsonProperty("sexId")
    public String getSexId() {
        return sexId;
    }

    @JsonProperty("sexId")
    public void setSexId(String sexId) {
        this.sexId = sexId;
    }

    @JsonProperty("placeOfBirth")
    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    @JsonProperty("placeOfBirth")
    public void setPlaceOfBirth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    @JsonProperty("hasLoanFromPrevEmployer")
    public String getHasLoanFromPrevEmployer() {
        return hasLoanFromPrevEmployer;
    }

    @JsonProperty("hasLoanFromPrevEmployer")
    public void setHasLoanFromPrevEmployer(String hasLoanFromPrevEmployer) {
        this.hasLoanFromPrevEmployer = hasLoanFromPrevEmployer;
    }

    @JsonProperty("hasCreditFromPrevEmployer")
    public String getHasCreditFromPrevEmployer() {
        return hasCreditFromPrevEmployer;
    }

    @JsonProperty("hasCreditFromPrevEmployer")
    public void setHasCreditFromPrevEmployer(String hasCreditFromPrevEmployer) {
        this.hasCreditFromPrevEmployer = hasCreditFromPrevEmployer;
    }

    @JsonProperty("hasWealthFromPrevEmployer")
    public String getHasWealthFromPrevEmployer() {
        return hasWealthFromPrevEmployer;
    }

    @JsonProperty("hasWealthFromPrevEmployer")
    public void setHasWealthFromPrevEmployer(String hasWealthFromPrevEmployer) {
        this.hasWealthFromPrevEmployer = hasWealthFromPrevEmployer;
    }

    @JsonProperty("hasNdaFromPrevEmployer")
    public String getHasNdaFromPrevEmployer() {
        return hasNdaFromPrevEmployer;
    }

    @JsonProperty("hasNdaFromPrevEmployer")
    public void setHasNdaFromPrevEmployer(String hasNdaFromPrevEmployer) {
        this.hasNdaFromPrevEmployer = hasNdaFromPrevEmployer;
    }

    @JsonProperty("hasCriminalRecord")
    public String getHasCriminalRecord() {
        return hasCriminalRecord;
    }

    @JsonProperty("hasCriminalRecord")
    public void setHasCriminalRecord(String hasCriminalRecord) {
        this.hasCriminalRecord = hasCriminalRecord;
    }

    @JsonProperty("companyCode")
    public String getCompanyCode() {
        return companyCode;
    }

    @JsonProperty("companyCode")
    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }
}
