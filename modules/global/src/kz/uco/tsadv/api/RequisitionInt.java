package kz.uco.tsadv.api;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.tsadv.api.AbstractEntityInt;

import java.util.UUID;

@NamePattern("%s|code")
@MetaClass(name = "tsadv$RequisitionInt")
public class RequisitionInt extends AbstractEntityInt {
    private static final long serialVersionUID = -7268404186795429591L;

    @MetaProperty
    protected String code;

    @MetaProperty(mandatory = true)
    protected Boolean requiredTest = false;

    @MetaProperty(mandatory = true)
    protected Boolean videoInterviewRequired = false;

    @MetaProperty
    protected String job;

    @MetaProperty
    protected Integer posCount;

    @MetaProperty
    protected String cityName;

    @MetaProperty
    protected UUID city;

    @MetaProperty
    protected String categoryName;

    @MetaProperty
    protected UUID category;

    @MetaProperty
    protected String startDate;

    @MetaProperty
    protected String endDate;

    @MetaProperty
    protected String finalDate;

    @MetaProperty
    protected String description;

    @MetaProperty
    protected UUID country;

    @MetaProperty
    protected String countryName;

    @MetaProperty
    protected Double latitude;

    @MetaProperty
    protected Double longitude;

    public void setRequiredTest(Boolean requiredTest) {
        this.requiredTest = requiredTest;
    }

    public Boolean getRequiredTest() {
        return requiredTest;
    }


    public Boolean getVideoInterviewRequired() {
        return videoInterviewRequired;
    }

    public void setVideoInterviewRequired(Boolean videoInterviewRequired) {
        this.videoInterviewRequired = videoInterviewRequired;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLongitude() {
        return longitude;
    }


    public UUID getCity() {
        return city;
    }

    public void setCity(UUID city) {
        this.city = city;
    }


    public UUID getCategory() {
        return category;
    }

    public void setCategory(UUID category) {
        this.category = category;
    }


    public UUID getCountry() {
        return country;
    }

    public void setCountry(UUID country) {
        this.country = country;
    }


    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryName() {
        return countryName;
    }


    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }


    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }


    public Integer getPosCount() {
        return posCount;
    }

    public void setPosCount(Integer posCount) {
        this.posCount = posCount;
    }




    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getJob() {
        return job;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setFinalDate(String finalDate) {
        this.finalDate = finalDate;
    }

    public String getFinalDate() {
        return finalDate;
    }


}