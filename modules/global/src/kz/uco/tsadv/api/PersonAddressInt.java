package kz.uco.tsadv.api;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

import java.util.Date;
import java.util.UUID;

@NamePattern("%s|id")
@MetaClass(name = "tsadv$PersonAddressInt")
public class PersonAddressInt extends BaseUuidEntity {
    private static final long serialVersionUID = -2532924225745159184L;

    @MetaProperty
    protected UUID city;

    @MetaProperty
    protected UUID addressType;

    @MetaProperty
    protected String address;

    @MetaProperty
    protected UUID country;

    @MetaProperty
    protected String postalCode;

    @MetaProperty
    protected String cityName;

    @MetaProperty
    protected Date startDate;

    @MetaProperty
    protected Date endDate;

    public void setAddressType(UUID addressType) {
        this.addressType = addressType;
    }

    public UUID getAddressType() {
        return addressType;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setCountry(UUID country) {
        this.country = country;
    }

    public UUID getCountry() {
        return country;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
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


    public UUID getCity() {
        return city;
    }

    public void setCity(UUID city) {
        this.city = city;
    }




}