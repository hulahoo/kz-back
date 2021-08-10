package kz.uco.tsadv.entity.dbview;

import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.global.DbView;
import kz.uco.base.entity.dictionary.DicCompany;
import kz.uco.tsadv.modules.personal.dictionary.DicIncentiveIndicators;
import kz.uco.tsadv.modules.personal.dictionary.DicIncentiveResultStatus;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@DbView
@Table(name = "TSADV_ORGANIZATION_INCENTIVE_MONTH_RESULT_VIEW")
@Entity(name = "tsadv_OrganizationIncentiveMonthResultView")
public class OrganizationIncentiveMonthResultView extends StandardEntity {
    private static final long serialVersionUID = 6512933878265314256L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID")
    private DicCompany company;

    @Temporal(TemporalType.DATE)
    @Column(name = "PERIOD_")
    private Date period;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEPARTMENT_ID")
    private OrganizationGroupExt department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INDICATOR_ID")
    private DicIncentiveIndicators indicator;

    @Column(name = "WEIGHT")
    private Double weight;

    @Column(name = "PLAN_")
    private BigDecimal plan;

    @Column(name = "FACT")
    private BigDecimal fact;

    @Column(name = "PREMIUM_PERCENT")
    private Double premiumPercent;

    @Column(name = "TOTAL_PREMIUM_PERCENT")
    private BigDecimal totalPremiumPercent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STATUS_ID")
    private DicIncentiveResultStatus status;

    @Column(name = "COMMENT_", length = 2500)
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ID")
    private OrganizationIncentiveMonthResultView parent;

    public OrganizationIncentiveMonthResultView getParent() {
        return parent;
    }

    public void setParent(OrganizationIncentiveMonthResultView parent) {
        this.parent = parent;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getWeight() {
        return weight;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public DicIncentiveResultStatus getStatus() {
        return status;
    }

    public void setStatus(DicIncentiveResultStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalPremiumPercent() {
        return totalPremiumPercent;
    }

    public void setTotalPremiumPercent(BigDecimal totalPremiumPercent) {
        this.totalPremiumPercent = totalPremiumPercent;
    }

    public Double getPremiumPercent() {
        return premiumPercent;
    }

    public void setPremiumPercent(Double premiumPercent) {
        this.premiumPercent = premiumPercent;
    }

    public BigDecimal getFact() {
        return fact;
    }

    public void setFact(BigDecimal fact) {
        this.fact = fact;
    }

    public BigDecimal getPlan() {
        return plan;
    }

    public void setPlan(BigDecimal plan) {
        this.plan = plan;
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
}