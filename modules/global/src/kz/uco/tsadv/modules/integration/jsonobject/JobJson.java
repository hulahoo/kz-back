package kz.uco.tsadv.modules.integration.jsonobject;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class JobJson implements Serializable {

    private String jobNameLang1;
    private String jobNameLang2;
    private String jobNameLang3;
    private String startDate;
    private String endDate;
    private String legacyId;
    private String companyCode;

    @JsonProperty("jobNameLang1")
    public String getJobNameLang1() {
        return jobNameLang1;
    }

    @JsonProperty("jobNameLang1")
    public void setJobNameLang1(String jobNameLang1) {
        this.jobNameLang1 = jobNameLang1;
    }

    @JsonProperty("jobNameLang2")
    public String getJobNameLang2() {
        return jobNameLang2;
    }

    @JsonProperty("jobNameLang2")
    public void setJobNameLang2(String jobNameLang2) {
        this.jobNameLang2 = jobNameLang2;
    }

    @JsonProperty("jobNameLang3")
    public String getJobNameLang3() {
        return jobNameLang3;
    }

    @JsonProperty("jobNameLang3")
    public void setJobNameLang3(String jobNameLang3) {
        this.jobNameLang3 = jobNameLang3;
    }

    @JsonProperty("startDate")
    public String getStartDate() {
        return startDate;
    }

    @JsonProperty("startDate")
    public void setStartDate(String value) {
        this.startDate = value;
    }

    @JsonProperty("endDate")
    public String getEndDate() {
        return endDate;
    }

    @JsonProperty("endDate")
    public void setEndDate(String value) {
        this.endDate = value;
    }

    @JsonProperty("legacyId")
    public String getLegacyId() {
        return legacyId;
    }

    @JsonProperty("legacyId")
    public void setLegacyId(String value) {
        this.legacyId = value;
    }

    @JsonProperty("companyCode")
    public String getCompanyCode() {
        return companyCode;
    }

    @JsonProperty("companyCode")
    public void setCompanyCode(String value) {
        this.companyCode = value;
    }
}
