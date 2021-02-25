package kz.uco.tsadv.modules.integration.jsonobject;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class AssignmentScheduleJson implements Serializable {
    private String assignmentId;
    private String scheduleId;
    private String startDate;
    private String endDate;
    private String endPolicyCode;
    private String companyCode;

    @JsonProperty("assignmentId")
    public String getAssignmentId() {
        return assignmentId;
    }

    @JsonProperty("assignmentId")
    public void setAssignmentId(String assignmentId) {
        this.assignmentId = assignmentId;
    }

    @JsonProperty("scheduleId")
    public String getScheduleId() {
        return scheduleId;
    }

    @JsonProperty("scheduleId")
    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
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

    @JsonProperty("endPolicyCode")
    public String getEndPolicyCode() {
        return endPolicyCode;
    }

    @JsonProperty("endPolicyCode")
    public void setEndPolicyCode(String endPolicyCode) {
        this.endPolicyCode = endPolicyCode;
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
