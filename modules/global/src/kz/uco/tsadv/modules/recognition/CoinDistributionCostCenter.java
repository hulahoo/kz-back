package kz.uco.tsadv.modules.recognition;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import kz.uco.tsadv.modules.personal.dictionary.DicCostCenter;
import com.haulmont.cuba.core.entity.StandardEntity;

@Table(name = "TSADV_COIN_DISTRIBUTION_COST_CENTER")
@Entity(name = "tsadv$CoinDistributionCostCenter")
public class CoinDistributionCostCenter extends StandardEntity {
    private static final long serialVersionUID = -9153308088179987255L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COST_CENTER_ID")
    protected DicCostCenter costCenter;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COIN_DISTRIBUTION_RULE_ID")
    protected CoinDistributionRule coinDistributionRule;

    public void setCoinDistributionRule(CoinDistributionRule coinDistributionRule) {
        this.coinDistributionRule = coinDistributionRule;
    }

    public CoinDistributionRule getCoinDistributionRule() {
        return coinDistributionRule;
    }


    public void setCostCenter(DicCostCenter costCenter) {
        this.costCenter = costCenter;
    }

    public DicCostCenter getCostCenter() {
        return costCenter;
    }


}