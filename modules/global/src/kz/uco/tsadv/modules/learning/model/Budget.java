package kz.uco.tsadv.modules.learning.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.tsadv.modules.learning.dictionary.DicBudgetStatus;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.*;
import java.util.Date;

@Listeners("tsadv_BudgetListener")
@NamePattern("%s|name")
@Table(name = "TSADV_BUDGET")
@Entity(name = "tsadv$Budget")
public class Budget extends AbstractParentEntity {
    private static final long serialVersionUID = 17769378355263500L;

    @Column(name = "NAME", nullable = false)
    protected String name;


    @Column(name = "DESCRIPTION", length = 2000)
    protected String description;

    @Temporal(TemporalType.DATE)
    @Column(name = "BUDGET_START_DATE", nullable = false)
    protected Date budgetStartDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "BUDGET_END_DATE", nullable = false)
    protected Date budgetEndDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "REQUEST_START_DATE", nullable = false)
    protected Date requestStartDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "REQUEST_END_DATE", nullable = false)
    protected Date requestEndDate;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "STATUS_ID")
    protected DicBudgetStatus status;

    @OnDeleteInverse(DeletePolicy.UNLINK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PREVIOUS_BUDGET_ID")
    protected kz.uco.tsadv.modules.learning.model.Budget previousBudget;

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setRequestStartDate(Date requestStartDate) {
        this.requestStartDate = requestStartDate;
    }

    public Date getRequestStartDate() {
        return requestStartDate;
    }

    public void setRequestEndDate(Date requestEndDate) {
        this.requestEndDate = requestEndDate;
    }

    public Date getRequestEndDate() {
        return requestEndDate;
    }

    public void setStatus(DicBudgetStatus status) {
        this.status = status;
    }

    public DicBudgetStatus getStatus() {
        return status;
    }

    public void setPreviousBudget(kz.uco.tsadv.modules.learning.model.Budget previousBudget) {
        this.previousBudget = previousBudget;
    }

    public kz.uco.tsadv.modules.learning.model.Budget getPreviousBudget() {
        return previousBudget;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public void setBudgetStartDate(Date budgetStartDate) {
        this.budgetStartDate = budgetStartDate;
    }

    public Date getBudgetStartDate() {
        return budgetStartDate;
    }

    public void setBudgetEndDate(Date budgetEndDate) {
        this.budgetEndDate = budgetEndDate;
    }

    public Date getBudgetEndDate() {
        return budgetEndDate;
    }


}