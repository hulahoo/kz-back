package kz.uco.tsadv.modules.personal.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import kz.uco.base.entity.abstraction.AbstractDictionary;
import kz.uco.base.entity.dictionary.DicCity;
import kz.uco.tsadv.modules.personal.model.BusinessTrip;

import javax.persistence.*;
import java.util.Date;

@NamePattern("%s %s %s %s|city,dateFrom,dateTo,langValue")
@Table(name = "TSADV_DIC_BUSINESS_TRIP_LINE")
@Entity(name = "tsadv$DicBusinessTripLine")
public class DicBusinessTripLine extends AbstractDictionary {
    private static final long serialVersionUID = -7804589735364759576L;

    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "clear", "open"})
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TRIP_ID")
    protected BusinessTrip trip;

    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "clear", "open"})
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CITY_ID")
    protected DicCity city;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_FROM", nullable = false)
    protected Date dateFrom;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_TO", nullable = false)
    protected Date dateTo;

    public BusinessTrip getTrip() {
        return trip;
    }

    public void setTrip(BusinessTrip trip) {
        this.trip = trip;
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

    public DicCity getCity() {
        return city;
    }

    public void setCity(DicCity city) {
        this.city = city;
    }


}