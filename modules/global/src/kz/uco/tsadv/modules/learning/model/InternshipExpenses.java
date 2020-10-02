package kz.uco.tsadv.modules.learning.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import kz.uco.base.entity.dictionary.DicCurrency;
import kz.uco.tsadv.modules.personal.dictionary.DicCostType;
import com.haulmont.cuba.core.entity.StandardEntity;

@Table(name = "TSADV_INTERNSHIP_EXPENSES")
@Entity(name = "tsadv$InternshipExpenses")
public class InternshipExpenses extends StandardEntity {
    private static final long serialVersionUID = 6370707676061004680L;


    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "INTERNSHIP_ID")
    protected Internship internship;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "EXPENSE_TYPE_ID")
    protected DicCostType expenseType;

    @NotNull
    @Column(name = "AMOUNT", nullable = false)
    protected Double amount;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CURRENCY_ID")
    protected DicCurrency currency;





    public void setInternship(Internship internship) {
        this.internship = internship;
    }

    public Internship getInternship() {
        return internship;
    }


    public void setExpenseType(DicCostType expenseType) {
        this.expenseType = expenseType;
    }

    public DicCostType getExpenseType() {
        return expenseType;
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