package kz.uco.tsadv.modules.integration.jsonobject;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class AssignmentJson implements Serializable {

    private String legacyId;
    private String personGroup;
    private String organizationGroup;
    private String jobGroup;
    private String positionGroup;
    private String grade;
    private String primaryFlag;
    private String fte;
    private String startDate;
    private String endDate;
    private String probationPeriodEndDate;
    private String companyCode;
    private String assignDate;

    public String getAssignDate() {
        return assignDate;
    }

    public void setAssignDate(String assignDate) {
        this.assignDate = assignDate;
    }

    public String getAssignmentNumber() {
        return assignmentNumber;
    }

    public void setAssignmentNumber(String assignmentNumber) {
        this.assignmentNumber = assignmentNumber;
    }

    private String status;
    private String assignmentNumber;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("legacyId")
    public String getLegacyId() {
        return legacyId;
    }

    @JsonProperty("legacyId")
    public void setLegacyId(String legacyId) {
        this.legacyId = legacyId;
    }

    @JsonProperty("personGroup")
    public String getPersonGroup() {
        return personGroup;
    }

    @JsonProperty("personGroup")
    public void setPersonGroup(String personGroup) {
        this.personGroup = personGroup;
    }

    @JsonProperty("organizationGroup")
    public String getOrganizationGroup() {
        return organizationGroup;
    }

    @JsonProperty("organizationGroup")
    public void setOrganizationGroup(String organizationGroup) {
        this.organizationGroup = organizationGroup;
    }

    @JsonProperty("jobGroup")
    public String getJobGroup() {
        return jobGroup;
    }

    @JsonProperty("jobGroup")
    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    @JsonProperty("positionGroup")
    public String getPositionGroup() {
        return positionGroup;
    }

    @JsonProperty("positionGroup")
    public void setPositionGroup(String positionGroup) {
        this.positionGroup = positionGroup;
    }

    @JsonProperty("grade")
    public String getGrade() {
        return grade;
    }

    @JsonProperty("grade")
    public void setGrade(String grade) {
        this.grade = grade;
    }

    @JsonProperty("primaryFlag")
    public String getPrimaryFlag() {
        return primaryFlag;
    }

    @JsonProperty("primaryFlag")
    public void setPrimaryFlag(String primaryFlag) {
        this.primaryFlag = primaryFlag;
    }

    @JsonProperty("fte")
    public String getFte() {
        return fte;
    }

    @JsonProperty("fte")
    public void setFte(String fte) {
        this.fte = fte;
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

    @JsonProperty("probationPeriodEndDate")
    public String getProbationPeriodEndDate() {
        return probationPeriodEndDate;
    }

    @JsonProperty("probationPeriodEndDate")
    public void setProbationPeriodEndDate(String probationPeriodEndDate) {
        this.probationPeriodEndDate = probationPeriodEndDate;
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
