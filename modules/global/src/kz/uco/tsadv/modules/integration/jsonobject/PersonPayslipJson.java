package kz.uco.tsadv.modules.integration.jsonobject;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class PersonPayslipJson implements Serializable {
    protected String personId;
    protected String period;
    protected String file;
    protected String extension;
    protected String companyCode;

    @JsonProperty
    public String getPersonId() {
        return personId;
    }

    @JsonProperty
    public void setPersonId(String personId) {
        this.personId = personId;
    }

    @JsonProperty
    public String getPeriod() {
        return period;
    }

    @JsonProperty
    public void setPeriod(String period) {
        this.period = period;
    }

    @JsonProperty
    public String getFile() {
        return file;
    }

    @JsonProperty
    public void setFile(String file) {
        this.file = file;
    }

    @JsonProperty
    public String getExtension() {
        return extension;
    }

    @JsonProperty
    public void setExtension(String extension) {
        this.extension = extension;
    }

    @JsonProperty
    public String getCompanyCode() {
        return companyCode;
    }

    @JsonProperty
    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }
}
