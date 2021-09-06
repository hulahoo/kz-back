package kz.uco.tsadv.modules.personal.model;

import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.tsadv.modules.personal.dictionary.DicIncentiveIndicators;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Table(name = "TSADV_DIC_INCENTIVE_INDICATOR_SCORE_SETTING")
@Entity(name = "tsadv_DicIncentiveIndicatorScoreSetting")
public class DicIncentiveIndicatorScoreSetting extends StandardEntity {
    private static final long serialVersionUID = 782932264278100154L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "INDICATOR_ID")
    private DicIncentiveIndicators indicator;

    @NotNull
    @Column(name = "MIN_PERCENT", nullable = false)
    private Double minPercent;

    @Column(name = "MAX_PERCENT", nullable = false)
    @NotNull
    private Double maxPercent;

    @NotNull
    @Column(name = "TOTAL_SCORE", nullable = false)
    private Double totalScore;

    public Double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Double totalScore) {
        this.totalScore = totalScore;
    }

    public Double getMaxPercent() {
        return maxPercent;
    }

    public void setMaxPercent(Double maxPercent) {
        this.maxPercent = maxPercent;
    }

    public Double getMinPercent() {
        return minPercent;
    }

    public void setMinPercent(Double minPercent) {
        this.minPercent = minPercent;
    }

    public DicIncentiveIndicators getIndicator() {
        return indicator;
    }

    public void setIndicator(DicIncentiveIndicators indicator) {
        this.indicator = indicator;
    }
}