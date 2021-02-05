package kz.uco.tsadv.modules.integration.jsonobject;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class PositionJson implements Serializable {

    private String positionNameLang1;
    private String positionNameLang2;
    private String positionNameLang3;
    private String startDate;
    private String endDate;
    private String legacyId;
    private String companyCode;
    private String maxPerson;
    private String fte;
    private String gradeLegacyId;
    private String organizationLegacyId;
    private String jobLegacyId;
    private String employeeCategoryId;
    private String positionStatusId;

    @JsonProperty("positionNameLang1")
    public String getPositionNameLang1() {
        return positionNameLang1;
    }

    @JsonProperty("positionNameLang1")
    public void setPositionNameLang1(String positionNameLang1) {
        this.positionNameLang1 = positionNameLang1;
    }

    @JsonProperty("positionNameLang2")
    public String getPositionNameLang2() {
        return positionNameLang2;
    }

    @JsonProperty("positionNameLang2")
    public void setPositionNameLang2(String positionNameLang2) {
        this.positionNameLang2 = positionNameLang2;
    }

    @JsonProperty("positionNameLang3")
    public String getPositionNameLang3() {
        return positionNameLang3;
    }

    @JsonProperty("positionNameLang3")
    public void setPositionNameLang3(String positionNameLang3) {
        this.positionNameLang3 = positionNameLang3;
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

    @JsonProperty("legacyId")
    public String getLegacyId() {
        return legacyId;
    }

    @JsonProperty("legacyId")
    public void setLegacyId(String legacyId) {
        this.legacyId = legacyId;
    }

    @JsonProperty("companyCode")
    public String getCompanyCode() {
        return companyCode;
    }

    @JsonProperty("companyCode")
    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    @JsonProperty("maxPerson")
    public String getMaxPerson() {
        return maxPerson;
    }

    @JsonProperty("maxPerson")
    public void setMaxPerson(String maxPerson) {
        this.maxPerson = maxPerson;
    }

    @JsonProperty("fte")
    public String getFte() {
        return fte;
    }

    @JsonProperty("fte")
    public void setFte(String fte) {
        this.fte = fte;
    }

    @JsonProperty("gradeLegacyId")
    public String getGradeLegacyId() {
        return gradeLegacyId;
    }

    @JsonProperty("gradeLegacyId")
    public void setGradeLegacyId(String gradeLegacyId) {
        this.gradeLegacyId = gradeLegacyId;
    }

    @JsonProperty("organizationLegacyId")
    public String getOrganizationLegacyId() {
        return organizationLegacyId;
    }

    @JsonProperty("organizationLegacyId")
    public void setOrganizationLegacyId(String organizationLegacyId) {
        this.organizationLegacyId = organizationLegacyId;
    }

    @JsonProperty("jobLegacyId")
    public String getJobLegacyId() {
        return jobLegacyId;
    }

    @JsonProperty("jobLegacyId")
    public void setJobLegacyId(String jobLegacyId) {
        this.jobLegacyId = jobLegacyId;
    }

    @JsonProperty("employeeCategoryId")
    public String getEmployeeCategoryId() {
        return employeeCategoryId;
    }

    @JsonProperty("employeeCategoryId")
    public void setEmployeeCategoryId(String employeeCategoryId) {
        this.employeeCategoryId = employeeCategoryId;
    }

    @JsonProperty("positionStatusId")
    public String getPositionStatusId() {
        return positionStatusId;
    }

    @JsonProperty("positionStatusId")
    public void setPositionStatusId(String positionStatusId) {
        this.positionStatusId = positionStatusId;
    }
}
