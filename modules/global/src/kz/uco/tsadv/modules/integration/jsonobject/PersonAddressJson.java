package kz.uco.tsadv.modules.integration.jsonobject;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class PersonAddressJson implements Serializable {
    protected String personId;
    protected String legacyId;
    protected String addressType;
    protected String postal;
    protected String countryId;
    protected String addressKATOCode;
    protected String streetTypeId;
    protected String streetName;
    protected String building;
    protected String block;
    protected String flat;
    protected String addressForExpats;
    protected String notes;
    protected String addressKazakh;
    protected String addressEnglish;
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
    public String getLegacyId() {
        return legacyId;
    }

    @JsonProperty
    public void setLegacyId(String legacyId) {
        this.legacyId = legacyId;
    }

    @JsonProperty
    public String getAddressType() {
        return addressType;
    }

    @JsonProperty
    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    @JsonProperty
    public String getPostal() {
        return postal;
    }

    @JsonProperty
    public void setPostal(String postal) {
        this.postal = postal;
    }

    @JsonProperty
    public String getCountryId() {
        return countryId;
    }

    @JsonProperty
    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    @JsonProperty
    public String getAddressKATOCode() {
        return addressKATOCode;
    }

    @JsonProperty
    public void setAddressKATOCode(String addressKATOCode) {
        this.addressKATOCode = addressKATOCode;
    }

    @JsonProperty
    public String getStreetTypeId() {
        return streetTypeId;
    }

    @JsonProperty
    public void setStreetTypeId(String streetTypeId) {
        this.streetTypeId = streetTypeId;
    }

    @JsonProperty
    public String getStreetName() {
        return streetName;
    }

    @JsonProperty
    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    @JsonProperty
    public String getBuilding() {
        return building;
    }

    @JsonProperty
    public void setBuilding(String building) {
        this.building = building;
    }

    @JsonProperty
    public String getBlock() {
        return block;
    }

    @JsonProperty
    public void setBlock(String block) {
        this.block = block;
    }

    @JsonProperty
    public String getFlat() {
        return flat;
    }

    @JsonProperty
    public void setFlat(String flat) {
        this.flat = flat;
    }

    @JsonProperty
    public String getAddressForExpats() {
        return addressForExpats;
    }

    @JsonProperty
    public void setAddressForExpats(String addressForExpats) {
        this.addressForExpats = addressForExpats;
    }

    @JsonProperty
    public String getNotes() {
        return notes;
    }

    @JsonProperty
    public void setNotes(String notes) {
        this.notes = notes;
    }

    @JsonProperty
    public String getAddressKazakh() {
        return addressKazakh;
    }

    @JsonProperty
    public void setAddressKazakh(String addressKazakh) {
        this.addressKazakh = addressKazakh;
    }

    @JsonProperty
    public String getAddressEnglish() {
        return addressEnglish;
    }

    @JsonProperty
    public void setAddressEnglish(String addressEnglish) {
        this.addressEnglish = addressEnglish;
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
