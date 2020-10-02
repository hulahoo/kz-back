package kz.uco.tsadv.modules.learning.model;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.global.dictionary.DicMonth;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Table(name = "TSADV_BUDGET_REQUEST_DETAIL")
@Entity(name = "tsadv$BudgetRequestDetail")
public class BudgetRequestDetail extends AbstractParentEntity {
    private static final long serialVersionUID = -5270307655986293969L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "BUDGET_REQUEST_ID")
    protected BudgetRequest budgetRequest;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MONTH_ID")
    protected DicMonth month;

    @NotNull
    @Column(name = "EMPLOYEES_COUNT", nullable = false)
    protected Integer employeesCount;

    @Column(name = "BUSINESS_TRIP_EMPLOYEE")
    protected Integer businessTripEmployee;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "budgetRequestDetail")
    protected List<BudgetRequestItemDetail> budgetRequestItemDetail;

    public void setBudgetRequestItemDetail(List<BudgetRequestItemDetail> budgetRequestItemDetail) {
        this.budgetRequestItemDetail = budgetRequestItemDetail;
    }

    public List<BudgetRequestItemDetail> getBudgetRequestItemDetail() {
        return budgetRequestItemDetail;
    }


    public void setBudgetRequest(BudgetRequest budgetRequest) {
        this.budgetRequest = budgetRequest;
    }

    public BudgetRequest getBudgetRequest() {
        return budgetRequest;
    }

    public void setMonth(DicMonth month) {
        this.month = month;
    }

    public DicMonth getMonth() {
        return month;
    }

    public void setEmployeesCount(Integer employeesCount) {
        this.employeesCount = employeesCount;
    }

    public Integer getEmployeesCount() {
        return employeesCount;
    }

    public void setBusinessTripEmployee(Integer businessTripEmployee) {
        this.businessTripEmployee = businessTripEmployee;
    }

    public Integer getBusinessTripEmployee() {
        return businessTripEmployee;
    }


}