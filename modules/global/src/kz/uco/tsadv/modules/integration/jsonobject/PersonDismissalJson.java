package kz.uco.tsadv.modules.integration.jsonobject;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class PersonDismissalJson implements Serializable {
    private String personId;
    private String legacyId;
    private String dismissalReasonCode;
    private String dismissalArticle;
    private String dismissalDate;
    private String companyCode;

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

    @JsonProperty("dismissalReasonCode")
    public String getDismissalReasonCode() {
        return dismissalReasonCode;
    }

    @JsonProperty("dismissalReasonCode")
    public void setDismissalReasonCode(String dismissalReasonCode) {
        this.dismissalReasonCode = dismissalReasonCode;
    }

    @JsonProperty("dismissalDate")
    public String getDismissalDate() {
        return dismissalDate;
    }

    @JsonProperty("dismissalDate")
    public void setDismissalDate(String dismissalDate) {
        this.dismissalDate = dismissalDate;
    }

    @JsonProperty("companyCode")
    public String getCompanyCode() {
        return companyCode;
    }

    @JsonProperty("companyCode")
    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    @JsonProperty("dismissalArticle")
    public String getDismissalArticle() {
        return dismissalArticle;
    }

    @JsonProperty("dismissalArticle")
    public void setDismissalArticle(String dismissalArticle) {
        this.dismissalArticle = dismissalArticle;
    }
}
