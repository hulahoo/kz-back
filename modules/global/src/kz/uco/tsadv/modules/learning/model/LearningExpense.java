package kz.uco.tsadv.modules.learning.model;

import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.base.entity.dictionary.DicCurrency;
import kz.uco.tsadv.modules.personal.dictionary.DicCostType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import kz.uco.tsadv.global.dictionary.DicMonth;
import kz.uco.tsadv.modules.personal.model.PersonLearningHistory;

@Table(name = "TSADV_LEARNING_EXPENSE")
@Entity(name = "tsadv$LearningExpense")
public class LearningExpense extends AbstractParentEntity {
    private static final long serialVersionUID = 512109102551122956L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "EXPENSE_TYPE_ID")
    protected DicCostType expenseType;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CURRENCY_ID")
    protected DicCurrency currency;

    @NotNull
    @Column(name = "AMOUNT", nullable = false)
    protected Integer amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_LEARNING_CONTRACT_ID")
    protected PersonLearningContract personLearningContract;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_ENROLLMENT_ID")
    protected Enrollment personEnrollment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DIC_MONTH_ID")
    protected DicMonth dicMonth;

    @Column(name = "YEAR_")
    protected Integer year;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_LEARNING_HISTORY_ID")
    protected PersonLearningHistory personLearningHistory;

    public void setPersonLearningHistory(PersonLearningHistory personLearningHistory) {
        this.personLearningHistory = personLearningHistory;
    }

    public PersonLearningHistory getPersonLearningHistory() {
        return personLearningHistory;
    }


    public void setDicMonth(DicMonth dicMonth) {
        this.dicMonth = dicMonth;
    }

    public DicMonth getDicMonth() {
        return dicMonth;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getYear() {
        return year;
    }


    public void setExpenseType(DicCostType expenseType) {
        this.expenseType = expenseType;
    }

    public DicCostType getExpenseType() {
        return expenseType;
    }

    public void setCurrency(DicCurrency currency) {
        this.currency = currency;
    }

    public DicCurrency getCurrency() {
        return currency;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setPersonLearningContract(PersonLearningContract personLearningContract) {
        this.personLearningContract = personLearningContract;
    }

    public PersonLearningContract getPersonLearningContract() {
        return personLearningContract;
    }

    public void setPersonEnrollment(Enrollment personEnrollment) {
        this.personEnrollment = personEnrollment;
    }

    public Enrollment getPersonEnrollment() {
        return personEnrollment;
    }


}