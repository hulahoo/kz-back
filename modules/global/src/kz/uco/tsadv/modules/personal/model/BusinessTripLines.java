package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import groovy.transform.Sortable;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.base.entity.dictionary.DicCity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import com.haulmont.cuba.core.entity.annotation.Listeners;

@Listeners("tsadv_BusinessTripLinesListener")
@NamePattern("%s|cityTo")
@Table(name = "TSADV_BUSINESS_TRIP_LINES")
@Entity(name = "tsadv$BusinessTripLines")
public class BusinessTripLines extends AbstractParentEntity {
    private static final long serialVersionUID = -5823926301028645987L;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CITY_TO_ID")
    protected DicCity cityTo;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CITY_FROM_ID")
    protected DicCity cityFrom;

    @Column(name = "NUMBER_")
    protected String number;

    @NotNull
    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_FROM", nullable = false)
    protected Date dateFrom;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_TO", nullable = false)
    protected Date dateTo;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BUSINESS_TRIP_ID")
    protected BusinessTrip businessTrip;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "businessTripLines")
    protected List<BusinessTripCost> businessTripCost;



    public void setCityTo(DicCity cityTo) {
        this.cityTo = cityTo;
    }

    public DicCity getCityTo() {
        return cityTo;
    }

    public void setCityFrom(DicCity cityFrom) {
        this.cityFrom = cityFrom;
    }

    public DicCity getCityFrom() {
        return cityFrom;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getNumber() {
        return number;
    }


    public List<BusinessTripCost> getBusinessTripCost() {
        return businessTripCost;
    }

    public void setBusinessTripCost(List<BusinessTripCost> businessTripCost) {
        this.businessTripCost = businessTripCost;
    }






    public void setBusinessTrip(BusinessTrip businessTrip) {
        this.businessTrip = businessTrip;
    }

    public BusinessTrip getBusinessTrip() {
        return businessTrip;
    }




    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public Date getDateTo() {
        return dateTo;
    }


}