package kz.uco.tsadv.modules.integration.jsonobject;

import java.io.Serializable;

public class SalaryJson implements Serializable {
    private String legacyId;
    private String assignmentLegacyId;
    private String personLegacyId;
    private String amount;
    private String currency;
    private String salaryType;
    private String netGross;
    private String startDate;
    private String endDate;
    private String companyCode;

    public String getLegacyId() {
        return legacyId;
    }

    public void setLegacyId(String legacyId) {
        this.legacyId = legacyId;
    }

    public String getAssignmentLegacyId() {
        return assignmentLegacyId;
    }

    public void setAssignmentLegacyId(String assignmentLegacyId) {
        this.assignmentLegacyId = assignmentLegacyId;
    }

    public String getPersonLegacyId() {
        return personLegacyId;
    }

    public void setPersonLegacyId(String personLegacyId) {
        this.personLegacyId = personLegacyId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getSalaryType() {
        return salaryType;
    }

    public void setSalaryType(String salaryType) {
        this.salaryType = salaryType;
    }

    public String getNetGross() {
        return netGross;
    }

    public void setNetGross(String netGross) {
        this.netGross = netGross;
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

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }
}
