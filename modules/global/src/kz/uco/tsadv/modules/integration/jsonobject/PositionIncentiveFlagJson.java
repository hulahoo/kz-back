package kz.uco.tsadv.modules.integration.jsonobject;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class PositionIncentiveFlagJson implements Serializable {
    protected String positionId;
    protected String legacyId;
    protected String incentiveFlag;
    protected String startDate;
    protected String endDate;
    protected String companyCode;

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

    @JsonProperty("incentiveFlag")
    public String getIncentiveFlag() {
        return incentiveFlag;
    }

    @JsonProperty("incentiveFlag")
    public void setIncentiveFlag(String incentiveFlag) {
        this.incentiveFlag = incentiveFlag;
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

    @JsonProperty("companyCode")
    public String getCompanyCode() {
        return companyCode;
    }

    @JsonProperty("companyCode")
    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }
}
