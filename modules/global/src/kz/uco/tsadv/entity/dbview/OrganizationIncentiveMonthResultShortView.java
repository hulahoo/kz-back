package kz.uco.tsadv.entity.dbview;

import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.global.DbView;
import kz.uco.base.entity.dictionary.DicCompany;
import kz.uco.tsadv.modules.personal.dictionary.DicIncentiveIndicators;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.model.OrganizationIncentiveMonthResult;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@DbView
@Table(name = "TSADV_ORGANIZATION_INCENTIVE_MONTH_RESULT_SHORT_VIEW")
@Entity(name = "tsadv_OrganizationIncentiveMonthResultShortView")
public class OrganizationIncentiveMonthResultShortView extends StandardEntity {
    private static final long serialVersionUID = -7648022183697758325L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "INCENTIVE_MONTH_RESULT_ID")
    private OrganizationIncentiveMonthResult incentiveMonthResult;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COMPANY_ID")
    private DicCompany company;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "PERIOD_", nullable = false)
    private Date period;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "DEPARTMENT_ID")
    private OrganizationGroupExt department;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "INDICATOR_ID")
    private DicIncentiveIndicators indicator;

    @Column(name = "RESULT_")
    private Double result;

    public Double getResult() {
        return result;
    }

    public void setResult(Double result) {
        this.result = result;
    }

    public DicIncentiveIndicators getIndicator() {
        return indicator;
    }

    public void setIndicator(DicIncentiveIndicators indicator) {
        this.indicator = indicator;
    }

    public OrganizationGroupExt getDepartment() {
        return department;
    }

    public void setDepartment(OrganizationGroupExt department) {
        this.department = department;
    }

    public Date getPeriod() {
        return period;
    }

    public void setPeriod(Date period) {
        this.period = period;
    }

    public DicCompany getCompany() {
        return company;
    }

    public void setCompany(DicCompany company) {
        this.company = company;
    }

    public OrganizationIncentiveMonthResult getIncentiveMonthResult() {
        return incentiveMonthResult;
    }

    public void setIncentiveMonthResult(OrganizationIncentiveMonthResult incentiveMonthResult) {
        this.incentiveMonthResult = incentiveMonthResult;
    }
}