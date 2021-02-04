package kz.uco.tsadv.modules.integration.jsonobject;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class OrganizationJson implements Serializable {
    private String organizationNameLang1;
    private String organizationNameLang2;
    private String organizationNameLang3;
    private String startDate;
    private String endDate;
    private String location;
    private String organizationType;
    private String legacyId;
    private String companyCode;

    @JsonProperty("organizationNameLang1")
    public String getOrganizationNameLang1() {
        return organizationNameLang1;
    }

    @JsonProperty("organizationNameLang1")
    public void setOrganizationNameLang1(String value) {
        this.organizationNameLang1 = value;
    }

    @JsonProperty("organizationNameLang2")
    public String getOrganizationNameLang2() {
        return organizationNameLang2;
    }

    @JsonProperty("organizationNameLang2")
    public void setOrganizationNameLang2(String value) {
        this.organizationNameLang2 = value;
    }

    @JsonProperty("organizationNameLang3")
    public String getOrganizationNameLang3() {
        return organizationNameLang3;
    }

    @JsonProperty("organizationNameLang3")
    public void setOrganizationNameLang3(String value) {
        this.organizationNameLang3 = value;
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

    @JsonProperty("location")
    public String getLocation() {
        return location;
    }

    @JsonProperty("location")
    public void setLocation(String value) {
        this.location = value;
    }

    @JsonProperty("organizationType")
    public String getOrganizationType() {
        return organizationType;
    }

    @JsonProperty("organizationType")
    public void setOrganizationType(String value) {
        this.organizationType = value;
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
