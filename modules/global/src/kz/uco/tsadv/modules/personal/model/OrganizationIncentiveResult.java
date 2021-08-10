package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.PublishEntityChangedEvents;
import com.haulmont.cuba.core.global.PersistenceHelper;
import kz.uco.tsadv.modules.personal.dictionary.DicIncentiveIndicators;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@PublishEntityChangedEvents
@Table(name = "TSADV_ORGANIZATION_INCENTIVE_RESULT")
@Entity(name = "tsadv_OrganizationIncentiveResult")
public class OrganizationIncentiveResult extends StandardEntity {
    private static final long serialVersionUID = -2137159206566707357L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ORGANIZATION_GROUP_ID")
    protected OrganizationGroupExt organizationGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_INCENTIVE_MONTH_RESULT_ID")
    private OrganizationIncentiveMonthResult organizationIncentiveMonthResult;

    @Temporal(TemporalType.DATE)
    @Column(name = "PERIOD_DATE")
    protected Date periodDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INDICATOR_ID")
    protected DicIncentiveIndicators indicator;

    @Column(name = "PLAN_")
    protected BigDecimal plan;

    @Column(name = "FACT")
    protected BigDecimal fact;

    @Column(name = "WEIGHT")
    protected Double weight;

    @Column(name = "PREMIUM_PERCENT")
    private Double premiumPercent;

    @Column(name = "RESULT_")
    protected Double result;

    @MetaProperty
    public Double getScore() {
        if (PersistenceHelper.isLoaded(this, "indicator")
                && PersistenceHelper.isLoaded(indicator, "scoreSettings")
                && indicator != null) {
            List<DicIncentiveIndicatorScoreSetting> scoreSettings = indicator.getScoreSettings();
            if (scoreSettings != null)
                return scoreSettings.stream()
                        .filter(setting -> setting.getMinPercent() < result && setting.getMaxPercent() > result)
                        .findFirst()
                        .map(DicIncentiveIndicatorScoreSetting::getTotalScore)
                        .orElse(null);
        }
        return null;
    }

    public OrganizationIncentiveMonthResult getOrganizationIncentiveMonthResult() {
        return organizationIncentiveMonthResult;
    }

    public void setOrganizationIncentiveMonthResult(OrganizationIncentiveMonthResult organizationIncentiveMonthResult) {
        this.organizationIncentiveMonthResult = organizationIncentiveMonthResult;
    }

    public Double getPremiumPercent() {
        return premiumPercent;
    }

    public void setPremiumPercent(Double premiumPercent) {
        this.premiumPercent = premiumPercent;
    }

    public Double getResult() {
        return result;
    }

    public void setResult(Double result) {
        this.result = result;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
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

    public Date getPeriodDate() {
        return periodDate;
    }

    public void setPeriodDate(Date periodDate) {
        this.periodDate = periodDate;
    }

    public OrganizationGroupExt getOrganizationGroup() {
        return organizationGroup;
    }

    public void setOrganizationGroup(OrganizationGroupExt organizationGroup) {
        this.organizationGroup = organizationGroup;
    }
}