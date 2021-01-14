package kz.uco.tsadv.modules.performance.model;

import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.*;

@Table(name = "TSADV_SCORE_SETTING")
@Entity(name = "tsadv_ScoreSetting")
public class ScoreSetting extends StandardEntity {
    private static final long serialVersionUID = -2079980113080738632L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERFORMANCE_PLAN_ID")
    protected PerformancePlan performancePlan;

    @Column(name = "MIN_PERCENT")
    protected Double minPercent;

    @Column(name = "MAX_PERCENT")
    protected Double maxPercent;

    @Column(name = "FINAL_SCORE")
    protected Integer finalScore;

    public Integer getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(Integer finalScore) {
        this.finalScore = finalScore;
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

    public PerformancePlan getPerformancePlan() {
        return performancePlan;
    }

    public void setPerformancePlan(PerformancePlan performancePlan) {
        this.performancePlan = performancePlan;
    }
}