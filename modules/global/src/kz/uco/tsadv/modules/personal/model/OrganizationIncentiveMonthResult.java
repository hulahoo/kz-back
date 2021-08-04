package kz.uco.tsadv.modules.personal.model;

import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.tsadv.modules.personal.dictionary.DicIncentiveIndicators;
import kz.uco.tsadv.modules.personal.dictionary.DicIncentiveResultStatus;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "TSADV_ORGANIZATION_INCENTIVE_MONTH_RESULT")
@Entity(name = "tsadv_OrganizationIncentiveMonthResult")
public class OrganizationIncentiveMonthResult extends StandardEntity {
    private static final long serialVersionUID = -6327941622311600685L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COMPANY_ID")
    private OrganizationGroupExt company;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "PERIOD_", nullable = false)
    private Date period;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "DEPARTMENT_ID")
    @NotNull
    private OrganizationGroupExt department;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "INDICATOR_ID")
    @NotNull
    private DicIncentiveIndicators indicator;

    @NotNull
    @Column(name = "WEIGHT", nullable = false)
    private Double weight;

    @NotNull
    @Column(name = "PLAN_", nullable = false)
    private BigDecimal plan;

    @NotNull
    @Column(name = "FACT", nullable = false)
    private BigDecimal fact;

    @NotNull
    @Column(name = "PREMIUM_PERCENT", nullable = false)
    private Double premiumPercent;

    @NotNull
    @Column(name = "TOTAL_PREMIUM_PERCENT", nullable = false)
    private Double totalPremiumPercent;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "STATUS_ID")
    private DicIncentiveResultStatus status;

    @NotNull
    @Column(name = "COMMENT_", nullable = false, length = 2500)
    private String comment;

    public void setIndicator(DicIncentiveIndicators indicator) {
        this.indicator = indicator;
    }

    public DicIncentiveIndicators getIndicator() {
        return indicator;
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

    public Double getTotalPremiumPercent() {
        return totalPremiumPercent;
    }

    public void setTotalPremiumPercent(Double totalPremiumPercent) {
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

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
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

    public OrganizationGroupExt getCompany() {
        return company;
    }

    public void setCompany(OrganizationGroupExt company) {
        this.company = company;
    }
}