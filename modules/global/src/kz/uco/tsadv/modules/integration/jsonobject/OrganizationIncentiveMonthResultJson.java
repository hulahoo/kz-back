package kz.uco.tsadv.modules.integration.jsonobject;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class OrganizationIncentiveMonthResultJson implements Serializable {

    protected String organizationId;
    protected String period;
    protected String incentiveType;
    protected String planPercent = "";
    protected double factPercent;
    protected String companyCode;

    @JsonProperty("factPercent")
    public double getFactPercent() {
        return factPercent;
    }

    @JsonProperty("factPercent")
    public void setFactPercent(double factPercent) {
        this.factPercent = factPercent;
    }

    @JsonProperty("organizationId")
    public String getOrganizationId() {
        return organizationId;
    }

    @JsonProperty("organizationId")
    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    @JsonProperty("period")
    public String getPeriod() {
        return period;
    }

    @JsonProperty("period")
    public void setPeriod(String period) {
        this.period = period;
    }

    @JsonProperty("incentiveType")
    public String getIncentiveType() {
        return incentiveType;
    }

    @JsonProperty("incentiveType")
    public void setIncentiveType(String incentiveType) {
        this.incentiveType = incentiveType;
    }

    @JsonProperty("result")
    public String getPlanPercent() {
        return planPercent;
    }

    @JsonProperty("planPercent")
    public void setPlanPercent(String planPercent) {
        this.planPercent = planPercent;
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
