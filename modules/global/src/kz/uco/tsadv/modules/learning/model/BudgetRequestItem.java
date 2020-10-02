package kz.uco.tsadv.modules.learning.model;

import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.base.entity.dictionary.DicCurrency;
import kz.uco.tsadv.modules.personal.dictionary.DicCostType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Table(name = "TSADV_BUDGET_REQUEST_ITEM")
@Entity(name = "tsadv$BudgetRequestItem")
public class BudgetRequestItem extends AbstractParentEntity {
    private static final long serialVersionUID = -8005423934133560054L;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "BUDGET_REQUEST_ID")
    protected BudgetRequest budgetRequest;


    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "FIRST_DAY_OF_MONTH", nullable = false)
    protected Date firstDayOfMonth;

    @NotNull
    @Column(name = "NAME", nullable = false)
    protected String name;

    //    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BUDGET_ITEM_ID")
    protected DicCostType budgetItem;

    //    @NotNull
    @Column(name = "AMOUNT")
    protected Double amount;

    //    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CURRENCY_ID")
    protected DicCurrency currency;








































    public void setFirstDayOfMonth(Date firstDayOfMonth) {
        this.firstDayOfMonth = firstDayOfMonth;
    }

    public Date getFirstDayOfMonth() {
        return firstDayOfMonth;
    }


    public void setBudgetRequest(BudgetRequest budgetRequest) {
        this.budgetRequest = budgetRequest;
    }

    public BudgetRequest getBudgetRequest() {
        return budgetRequest;
    }

    public void setBudgetItem(DicCostType budgetItem) {
        this.budgetItem = budgetItem;
    }

    public DicCostType getBudgetItem() {
        return budgetItem;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getAmount() {
        return amount;
    }

    public void setCurrency(DicCurrency currency) {
        this.currency = currency;
    }

    public DicCurrency getCurrency() {
        return currency;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


}