package kz.uco.tsadv.modules.integration.jsonobject;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class PersonContactJson implements Serializable {
    private String personId;
    private String legacyId;
    private String type;
    private String value;
    private String companyCode;
    private String startDate;
    private String endDate;

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

    @JsonProperty("personId")
    public String getPersonId() {
        return personId;
    }

    @JsonProperty("personId")
    public void setPersonId(String personId) {
        this.personId = personId;
    }

    @JsonProperty("legacyId")
    public String getLegacyId() {
        return legacyId;
    }

    @JsonProperty("legacyId")
    public void setLegacyId(String legacyId) {
        this.legacyId = legacyId;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("value")
    public String getValue() {
        return value;
    }

    @JsonProperty("value")
    public void setValue(String value) {
        this.value = value;
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
