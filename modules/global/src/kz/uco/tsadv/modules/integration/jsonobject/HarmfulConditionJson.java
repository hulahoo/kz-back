package kz.uco.tsadv.modules.integration.jsonobject;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class HarmfulConditionJson implements Serializable {
    private String positionId;
    private String legacyId;
    private String startDate;
    private String endDate;
    private int days;
    private String companyCode;

    @JsonProperty("positionId")
    public String getPositionId() {
        return positionId;
    }

    @JsonProperty("positionId")
    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }

    @JsonProperty("legacyId")
    public String getLegacyId() {
        return legacyId;
    }

    @JsonProperty("legacyId")
    public void setLegacyId(String legacyId) {
        this.legacyId = legacyId;
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

    @JsonProperty("days")
    public int getDays() {
        return days;
    }

    @JsonProperty("days")
    public void setDays(int days) {
        this.days = days;
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
