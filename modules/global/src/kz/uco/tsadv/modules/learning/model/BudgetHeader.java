package kz.uco.tsadv.modules.learning.model;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.performance.enums.BudgetHeaderStatus;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Listeners("tsadv_BudgetHeaderListener")
@Table(name = "TSADV_BUDGET_HEADER")
@Entity(name = "tsadv$BudgetHeader")
public class BudgetHeader extends AbstractParentEntity {
    private static final long serialVersionUID = -1563918796047982177L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "BUDGET_ID")
    protected Budget budget;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RESPONSIBLE_PERSON_ID")
    protected PersonGroupExt responsiblePerson;

    @NotNull
    @Column(name = "HEADER_NAME", nullable = false)
    protected String headerName;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ORGANIZATION_GROUP_ID")
    protected OrganizationGroupExt organizationGroup;

    @NotNull
    @Column(name = "STATUS", nullable = false)
    protected String status;


    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "budgetHeader")
    protected List<BudgetRequest> budgetRequests;

    public void setBudgetRequests(List<BudgetRequest> budgetRequests) {
        this.budgetRequests = budgetRequests;
    }

    public List<BudgetRequest> getBudgetRequests() {
        return budgetRequests;
    }


    public void setResponsiblePerson(PersonGroupExt responsiblePerson) {
        this.responsiblePerson = responsiblePerson;
    }

    public PersonGroupExt getResponsiblePerson() {
        return responsiblePerson;
    }





    public void setStatus(BudgetHeaderStatus status) {
        this.status = status == null ? null : status.getId();
    }

    public BudgetHeaderStatus getStatus() {
        return status == null ? null : BudgetHeaderStatus.fromId(status);
    }


    public void setBudget(Budget budget) {
        this.budget = budget;
    }

    public Budget getBudget() {
        return budget;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    public String getHeaderName() {
        return headerName;
    }

    public void setOrganizationGroup(OrganizationGroupExt organizationGroup) {
        this.organizationGroup = organizationGroup;
    }

    public OrganizationGroupExt getOrganizationGroup() {
        return organizationGroup;
    }


}