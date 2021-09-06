package kz.uco.tsadv.modules.integration.jsonobject;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class PersonDocumentRequestDataJson implements Serializable {

    protected String personId;
    protected String requestNumber;
    protected String legacyId;
    protected String documentTypeId;
    protected String documentNumber;
    protected String issueDate;
    protected String issuedBy;
    protected String expiredDate;
    protected String issueAuthorityId;
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
    public String getLegacyId() {
        return legacyId;
    }

    @JsonProperty
    public void setLegacyId(String legacyId) {
        this.legacyId = legacyId;
    }

    @JsonProperty
    public String getDocumentTypeId() {
        return documentTypeId;
    }

    @JsonProperty
    public void setDocumentTypeId(String documentTypeId) {
        this.documentTypeId = documentTypeId;
    }

    @JsonProperty
    public String getDocumentNumber() {
        return documentNumber;
    }

    @JsonProperty
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    @JsonProperty
    public String getIssueDate() {
        return issueDate;
    }

    @JsonProperty
    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    @JsonProperty
    public String getExpiredDate() {
        return expiredDate;
    }

    @JsonProperty
    public void setExpiredDate(String expiredDate) {
        this.expiredDate = expiredDate;
    }

    @JsonProperty
    public String getIssueAuthorityId() {
        return issueAuthorityId;
    }

    @JsonProperty
    public void setIssueAuthorityId(String issueAuthorityId) {
        this.issueAuthorityId = issueAuthorityId;
    }

    @JsonProperty
    public String getCompanyCode() {
        return companyCode;
    }

    @JsonProperty
    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    @JsonProperty
    public String getIssuedBy() {
        return issuedBy;
    }

    @JsonProperty
    public void setIssuedBy(String issuedBy) {
        this.issuedBy = issuedBy;
    }
}
