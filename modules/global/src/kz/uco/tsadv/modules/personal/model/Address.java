package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.base.entity.dictionary.DicCity;
import kz.uco.base.entity.dictionary.DicCountry;
import kz.uco.base.entity.dictionary.DicLanguage;
import kz.uco.tsadv.modules.personal.dictionary.DicAddressType;
import kz.uco.tsadv.modules.personal.dictionary.DicKato;
import kz.uco.tsadv.modules.personal.dictionary.DicStreetType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@NamePattern("%s|address")
@Table(name = "TSADV_ADDRESS")
@Entity(name = "tsadv$Address")
public class Address extends AbstractParentEntity {
    private static final long serialVersionUID = 8946193589446067816L;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "open", "clear"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ADDRESS_TYPE_ID")
    protected DicAddressType addressType;

    @Column(name = "ADDRESS", length = 500)
    protected String address;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COUNTRY_ID")
    protected DicCountry country;

    @Column(name = "POSTAL_CODE")
    protected String postalCode;

    @Column(name = "CITY_NAME")
    protected String cityName;

    @NotNull
    @Column(name = "FACT_ADDRESS")
    protected String factAddress;

    @NotNull
    @Column(name = "REGISTRATION_ADDRESS")
    protected String registrationAddress;

    @NotNull
    @Column(name = "FACT_ADDRESS_KATO_CODE")
    protected String factAddressKATOCode;

    @NotNull
    @Column(name = "REGISTRATION_ADDRESS_KATO_CODE")
    protected String registrationAddressKATOCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CITY_ID")
    private DicCity city;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LANGUAGE_ID")
    private DicLanguage language;

    @NotNull
    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE", nullable = false)
    protected Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE", nullable = false)
    @NotNull
    protected Date endDate;

    @OrderBy("name")
    @JoinTable(name = "TSADV_ADDRESS_FILE_DESCRIPTOR_LINK",
            joinColumns = @JoinColumn(name = "ADDRESS_ID"),
            inverseJoinColumns = @JoinColumn(name = "FILE_DESCRIPTOR_ID"))
    @ManyToMany
    private List<FileDescriptor> attachments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KATO_ID")
    protected DicKato kato;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STREET_TYPE_ID")
    protected DicStreetType streetType;

    @Column(name = "STREET_NAME")
    protected String streetName;

    @Column(name = "BUILDING")
    protected String building;

    @Column(name = "BLOCK")
    protected String block;

    @Column(name = "FLAT")
    protected String flat;

    @Column(name = "ADDRESS_FOR_EXPATS")
    protected String addressForExpats;

    @Column(name = "NOTES")
    protected String notes;

    @Column(name = "ADDRESS_KAZAKH")
    protected String addressKazakh;

    @Column(name = "ADDRESS_ENGLISH")
    protected String addressEnglish;

    public String getAddressEnglish() {
        return addressEnglish;
    }

    public void setAddressEnglish(String addressEnglish) {
        this.addressEnglish = addressEnglish;
    }

    public String getAddressKazakh() {
        return addressKazakh;
    }

    public void setAddressKazakh(String addressKazakh) {
        this.addressKazakh = addressKazakh;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getAddressForExpats() {
        return addressForExpats;
    }

    public void setAddressForExpats(String addressForExpats) {
        this.addressForExpats = addressForExpats;
    }

    public String getFlat() {
        return flat;
    }

    public void setFlat(String flat) {
        this.flat = flat;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public DicStreetType getStreetType() {
        return streetType;
    }

    public void setStreetType(DicStreetType streetType) {
        this.streetType = streetType;
    }

    public DicKato getKato() {
        return kato;
    }

    public void setKato(DicKato kato) {
        this.kato = kato;
    }

    public List<FileDescriptor> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<FileDescriptor> attachments) {
        this.attachments = attachments;
    }

    public DicLanguage getLanguage() {
        return language;
    }

    public void setLanguage(DicLanguage language) {
        this.language = language;
    }

    public DicCity getCity() {
        return city;
    }

    public void setCity(DicCity city) {
        this.city = city;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setCountry(DicCountry country) {
        this.country = country;
    }

    public DicCountry getCountry() {
        return country;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setCityName(String city) {
        this.cityName = city;
    }

    public String getCityName() {
        return cityName;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setAddressType(DicAddressType addressType) {
        this.addressType = addressType;
    }

    public DicAddressType getAddressType() {
        return addressType;
    }

    public String getFactAddress() {
        return factAddress;
    }

    public void setFactAddress(String factAddress) {
        this.factAddress = factAddress;
    }

    public String getRegistrationAddress() {
        return registrationAddress;
    }

    public void setRegistrationAddress(String registrationAddress) {
        this.registrationAddress = registrationAddress;
    }

    public String getFactAddressKATOCode() {
        return factAddressKATOCode;
    }

    public void setFactAddressKATOCode(String factAddressKATOCode) {
        this.factAddressKATOCode = factAddressKATOCode;
    }

    public String getRegistrationAddressKATOCode() {
        return registrationAddressKATOCode;
    }

    public void setRegistrationAddressKATOCode(String registrationAddressKATOCode) {
        this.registrationAddressKATOCode = registrationAddressKATOCode;
    }
}