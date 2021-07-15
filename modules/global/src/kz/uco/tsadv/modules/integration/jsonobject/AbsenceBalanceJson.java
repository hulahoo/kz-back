package kz.uco.tsadv.modules.integration.jsonobject;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class AbsenceBalanceJson implements Serializable {
    private String legacyId;
    private String personId;
    private String date;
    private String annualDueDays;
    private String additionalDueDays;
    private String ecologicalDueDays;
    private String disabilityDueDays;
    private String annualBalanceDays;
    private String additionalBalanceDays;
    private String ecologicalBalanceDays;
    private String disabilityBalanceDays;
    private String companyCode;

    @JsonProperty("legacyId")
    public String getLegacyId() {
        return legacyId;
    }

    @JsonProperty("legacyId")
    public void setLegacyId(String legacyId) {
        this.legacyId = legacyId;
    }

    @JsonProperty("personId")
    public String getPersonId() {
        return personId;
    }

    @JsonProperty("personId")
    public void setPersonId(String personId) {
        this.personId = personId;
    }

    @JsonProperty("date")
    public String getDate() {
        return date;
    }

    @JsonProperty("date")
    public void setDate(String date) {
        this.date = date;
    }

    @JsonProperty("annualDueDays")
    public String getAnnualDueDays() {
        return annualDueDays;
    }

    @JsonProperty("annualDueDays")
    public void setAnnualDueDays(String annualDueDays) {
        this.annualDueDays = annualDueDays;
    }

    @JsonProperty("additionalDueDays")
    public String getAdditionalDueDays() {
        return additionalDueDays;
    }

    @JsonProperty("additionalDueDays")
    public void setAdditionalDueDays(String additionalDueDays) {
        this.additionalDueDays = additionalDueDays;
    }

    @JsonProperty("ecologicalDueDays")
    public String getEcologicalDueDays() {
        return ecologicalDueDays;
    }

    @JsonProperty("ecologicalDueDays")
    public void setEcologicalDueDays(String ecologicalDueDays) {
        this.ecologicalDueDays = ecologicalDueDays;
    }

    @JsonProperty("disabilityDueDays")
    public String getDisabilityDueDays() {
        return disabilityDueDays;
    }

    @JsonProperty("disabilityDueDays")
    public void setDisabilityDueDays(String disabilityDueDays) {
        this.disabilityDueDays = disabilityDueDays;
    }

    @JsonProperty("annualBalanceDays")
    public String getAnnualBalanceDays() {
        return annualBalanceDays;
    }

    @JsonProperty("annualBalanceDays")
    public void setAnnualBalanceDays(String annualBalanceDays) {
        this.annualBalanceDays = annualBalanceDays;
    }

    @JsonProperty("additionalBalanceDays")
    public String getAdditionalBalanceDays() {
        return additionalBalanceDays;
    }

    @JsonProperty("additionalBalanceDays")
    public void setAdditionalBalanceDays(String additionalBalanceDays) {
        this.additionalBalanceDays = additionalBalanceDays;
    }

    @JsonProperty("ecologicalBalanceDays")
    public String getEcologicalBalanceDays() {
        return ecologicalBalanceDays;
    }

    @JsonProperty("ecologicalBalanceDays")
    public void setEcologicalBalanceDays(String ecologicalBalanceDays) {
        this.ecologicalBalanceDays = ecologicalBalanceDays;
    }

    @JsonProperty("disabilityBalanceDays")
    public String getDisabilityBalanceDays() {
        return disabilityBalanceDays;
    }

    @JsonProperty("disabilityBalanceDays")
    public void setDisabilityBalanceDays(String disabilityBalanceDays) {
        this.disabilityBalanceDays = disabilityBalanceDays;
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