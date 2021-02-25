package kz.uco.tsadv.modules.integration.jsonobject;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class PersonAddressJson implements Serializable {
    private String personId;
    private String legacyId;
    private String factAddress;
    private String registrationAddress;
    private String factAddressKATOCode;
    private String registrationAddressKATOCode;

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

    @JsonProperty("factAddress")
    public String getFactAddress() {
        return factAddress;
    }

    @JsonProperty("factAddress")
    public void setFactAddress(String factAddress) {
        this.factAddress = factAddress;
    }

    @JsonProperty("registrationAddress")
    public String getRegistrationAddress() {
        return registrationAddress;
    }

    @JsonProperty("registrationAddress")
    public void setRegistrationAddress(String registrationAddress) {
        this.registrationAddress = registrationAddress;
    }

    @JsonProperty("factAddressKATOCode")
    public String getFactAddressKATOCode() {
        return factAddressKATOCode;
    }

    @JsonProperty("factAddressKATOCode")
    public void setFactAddressKATOCode(String factAddressKATOCode) {
        this.factAddressKATOCode = factAddressKATOCode;
    }

    @JsonProperty("registrationAddressKATOCode")
    public String getRegistrationAddressKATOCode() {
        return registrationAddressKATOCode;
    }

    @JsonProperty("registrationAddressKATOCode")
    public void setRegistrationAddressKATOCode(String registrationAddressKATOCode) {
        this.registrationAddressKATOCode = registrationAddressKATOCode;
    }
}
