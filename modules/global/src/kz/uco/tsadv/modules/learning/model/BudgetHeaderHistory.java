package kz.uco.tsadv.modules.learning.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.performance.enums.BudgetHeaderStatus;

@Table(name = "TSADV_BUDGET_HEADER_HISTORY")
@Entity(name = "tsadv$BudgetHeaderHistory")
public class BudgetHeaderHistory extends AbstractParentEntity {
    private static final long serialVersionUID = 6840515595722633059L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "BUDGET_HEADER_ID")
    protected BudgetHeader budgetHeader;

    @NotNull
    @Column(name = "BUDGET_HEADER_STATUS", nullable = false)
    protected String budgetHeaderStatus;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CHANGE_PERSON_ID")
    protected PersonGroupExt changePerson;

    @Column(name = "COMMENT_", length = 1000)
    protected String comment;

    public void setBudgetHeaderStatus(BudgetHeaderStatus budgetHeaderStatus) {
        this.budgetHeaderStatus = budgetHeaderStatus == null ? null : budgetHeaderStatus.getId();
    }

    public BudgetHeaderStatus getBudgetHeaderStatus() {
        return budgetHeaderStatus == null ? null : BudgetHeaderStatus.fromId(budgetHeaderStatus);
    }


    public void setBudgetHeader(BudgetHeader budgetHeader) {
        this.budgetHeader = budgetHeader;
    }

    public BudgetHeader getBudgetHeader() {
        return budgetHeader;
    }

    public void setChangePerson(PersonGroupExt changePerson) {
        this.changePerson = changePerson;
    }

    public PersonGroupExt getChangePerson() {
        return changePerson;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }


}