package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import kz.uco.base.entity.dictionary.DicCurrency;
import kz.uco.tsadv.modules.personal.model.BusinessTripLines;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.dictionary.DicCostType;

import javax.persistence.*;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import java.math.BigDecimal;
import com.haulmont.chile.core.annotations.NumberFormat;

@Listeners("tsadv_BusinessTripCostListener")
@NamePattern("%s %s %s |amount,costType,currency")
@Table(name = "TSADV_BUSINESS_TRIP_COST")
@Entity(name = "tsadv$BusinessTripCost")
public class BusinessTripCost extends AbstractParentEntity {
    private static final long serialVersionUID = -4380980347259081890L;


    @Column(name = "AMOUNT", nullable = false)
    @NumberFormat(pattern = "0.00")
    protected BigDecimal amount;



    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "open", "clear"})
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COST_TYPE_ID")
    protected DicCostType costType;

    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "open", "clear"})
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CURRENCY_ID")
    protected DicCurrency currency;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BUSINESS_TRIP_LINES_ID")
    protected BusinessTripLines businessTripLines;
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }






    public void setBusinessTripLines(BusinessTripLines businessTripLines) {
        this.businessTripLines = businessTripLines;
    }

    public BusinessTripLines getBusinessTripLines() {
        return businessTripLines;
    }


    public void setCostType(DicCostType costType) {
        this.costType = costType;
    }

    public DicCostType getCostType() {
        return costType;
    }


    public DicCurrency getCurrency() {
        return currency;
    }

    public void setCurrency(DicCurrency currency) {
        this.currency = currency;
    }





}