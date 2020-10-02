package kz.uco.tsadv.modules.learning.model;

import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.base.entity.dictionary.DicCurrency;
import kz.uco.tsadv.modules.personal.dictionary.DicCostType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Table(name = "TSADV_BUDGET_REQUEST_ITEM_DETAIL")
@Entity(name = "tsadv$BudgetRequestItemDetail")
public class BudgetRequestItemDetail extends AbstractParentEntity {
    private static final long serialVersionUID = -1501822376205770976L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BUDGET_REQUEST_DETAIL_ID")
    protected BudgetRequestDetail budgetRequestDetail;

//    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "BUDGET_REQUEST_ITEM_ID")
    protected BudgetRequestItem budgetRequestItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BUDGET_ITEM_ID")
    protected DicCostType budgetItem;

    @NotNull
    @Column(name = "AMOUNT", nullable = false)
    protected Double amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CURRENCY_ID")
    protected DicCurrency currency;

    @Temporal(TemporalType.DATE)
//    @NotNull
    @Column(name = "MONTH_", nullable = false)
    protected Date month;

    public void setBudgetRequestItem(BudgetRequestItem budgetRequestItem) {
        this.budgetRequestItem = budgetRequestItem;
    }

    public BudgetRequestItem getBudgetRequestItem() {
        return budgetRequestItem;
    }


    public void setMonth(Date month) {
        this.month = month;
    }

    public Date getMonth() {
        return month;
    }


    public void setBudgetRequestDetail(BudgetRequestDetail budgetRequestDetail) {
        this.budgetRequestDetail = budgetRequestDetail;
    }

    public BudgetRequestDetail getBudgetRequestDetail() {
        return budgetRequestDetail;
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


}