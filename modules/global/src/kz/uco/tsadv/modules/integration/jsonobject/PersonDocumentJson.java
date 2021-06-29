package kz.uco.tsadv.modules.integration.jsonobject;

import java.io.Serializable;

public class PersonDocumentJson implements Serializable {
    protected String personId;
    protected String legacyId;
    protected String documentTypeId;
    protected String documentNumber;
    protected String issueDate;
    protected String expiredDate;
    protected String issueAuthorityId;
    protected String issueByForExpat;
    protected String nationalIdentifier;
    protected String companyCode;
    protected String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getLegacyId() {
        return legacyId;
    }

    public void setLegacyId(String legacyId) {
        this.legacyId = legacyId;
    }

    public String getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(String documentTypeId) {
        this.documentTypeId = documentTypeId;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(String expiredDate) {
        this.expiredDate = expiredDate;
    }

    public String getIssueAuthorityId() {
        return issueAuthorityId;
    }

    public void setIssueAuthorityId(String issueAuthorityId) {
        this.issueAuthorityId = issueAuthorityId;
    }

    public String getNationalIdentifier() {
        return nationalIdentifier;
    }

    public void setNationalIdentifier(String nationalIdentifier) {
        this.nationalIdentifier = nationalIdentifier;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getIssueByForExpat() {
        return issueByForExpat;
    }

    public void setIssueByForExpat(String issueByForExpat) {
        this.issueByForExpat = issueByForExpat;
    }
}
