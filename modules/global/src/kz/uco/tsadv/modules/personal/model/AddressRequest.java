package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.entity.annotation.PublishEntityChangedEvents;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.dictionary.DicCountry;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.dictionary.DicAddressType;
import kz.uco.tsadv.modules.personal.dictionary.DicKato;
import kz.uco.tsadv.modules.personal.dictionary.DicStreetType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import java.util.Date;
import java.util.List;

@PublishEntityChangedEvents
@Table(name = "TSADV_ADDRESS_REQUEST")
@Entity(name = "tsadv$AddressRequest")
@NamePattern("%s|address")
public class AddressRequest extends AbstractBprocRequest {
    private static final long serialVersionUID = 6738699252089383994L;

    public static final String PROCESS_DEFINITION_KEY = "addressRequest";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ADDRESS_TYPE_ID")
    protected DicAddressType addressType;

    @Column(name = "ADDRESS")
    protected String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COUNTRY_ID")
    protected DicCountry country;

    @Column(name = "POSTAL_CODE")
    protected String postalCode;

    @Column(name = "CITY")
    protected String city;

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE")
    protected Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE")
    protected Date endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BASE_ADDRESS_ID")
    protected Address baseAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @JoinTable(name = "TSADV_ADDRESS_REQUEST_FILE_DESCRIPTOR_LINK",
            joinColumns = @JoinColumn(name = "ADDRESS_REQUEST_ID"),
            inverseJoinColumns = @JoinColumn(name = "FILE_DESCRIPTOR_ID"))
    @OnDelete(DeletePolicy.CASCADE)
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

    @Column(name = "ADDRESS_KAZAKH")
    protected String addressKazakh;

    @Column(name = "ADDRESS_ENGLISH")
    protected String addressEnglish;

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getFlat() {
        return flat;
    }

    public void setFlat(String flat) {
        this.flat = flat;
    }

    public String getAddressForExpats() {
        return addressForExpats;
    }

    public void setAddressForExpats(String addressForExpats) {
        this.addressForExpats = addressForExpats;
    }

    public String getAddressKazakh() {
        return addressKazakh;
    }

    public void setAddressKazakh(String addressKazakh) {
        this.addressKazakh = addressKazakh;
    }

    public String getAddressEnglish() {
        return addressEnglish;
    }

    public void setAddressEnglish(String addressEnglish) {
        this.addressEnglish = addressEnglish;
    }

    public DicKato getKato() {
        return kato;
    }

    public void setKato(DicKato kato) {
        this.kato = kato;
    }

    public DicStreetType getStreetType() {
        return streetType;
    }

    public void setStreetType(DicStreetType streetType) {
        this.streetType = streetType;
    }

    public List<FileDescriptor> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<FileDescriptor> attachments) {
        this.attachments = attachments;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setBaseAddress(Address baseAddress) {
        this.baseAddress = baseAddress;
    }

    public Address getBaseAddress() {
        return baseAddress;
    }

    public void setAddressType(DicAddressType addressType) {
        this.addressType = addressType;
    }

    public DicAddressType getAddressType() {
        return addressType;
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

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
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

    @Override
    public String getProcessDefinitionKey() {
        return PROCESS_DEFINITION_KEY;
    }

    @PostConstruct
    public void postConstruct() {
        this.startDate = CommonUtils.getSystemDate();
        this.endDate = CommonUtils.getEndOfTime();
    }
}