package kz.uco.tsadv.modules.performance.model;

import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.base.entity.dictionary.DicCompany;

import javax.persistence.*;

@Table(name = "TSADV_CORRECTION_COEFFICIENT")
@Entity(name = "tsadv_CorrectionCoefficient")
public class CorrectionCoefficient extends StandardEntity {
    private static final long serialVersionUID = 6900902804868946972L;

    @Column(name = "GROUP_EFFICIENCY_PERCENT")
    protected Double groupEfficiencyPercent;

    @Column(name = "COMPANY_RESULT")
    protected Double companyResult;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERFORMANCE_PLAN_ID")
    protected PerformancePlan performancePlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID")
    protected DicCompany company;

    public DicCompany getCompany() {
        return company;
    }

    public void setCompany(DicCompany company) {
        this.company = company;
    }

    public PerformancePlan getPerformancePlan() {
        return performancePlan;
    }

    public void setPerformancePlan(PerformancePlan performancePlan) {
        this.performancePlan = performancePlan;
    }

    public Double getCompanyResult() {
        return companyResult;
    }

    public void setCompanyResult(Double companyResult) {
        this.companyResult = companyResult;
    }

    public Double getGroupEfficiencyPercent() {
        return groupEfficiencyPercent;
    }

    public void setGroupEfficiencyPercent(Double groupEfficiencyPercent) {
        this.groupEfficiencyPercent = groupEfficiencyPercent;
    }
}