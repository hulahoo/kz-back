package kz.uco.tsadv.modules.recognition;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import kz.uco.tsadv.modules.personal.group.JobGroup;
import com.haulmont.cuba.core.entity.StandardEntity;
import javax.validation.constraints.NotNull;

@Table(name = "TSADV_COIN_DISTRIBUTION_JOB")
@Entity(name = "tsadv$CoinDistributionJob")
public class CoinDistributionJob extends StandardEntity {
    private static final long serialVersionUID = 6756605111584174775L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "JOB_GROUP_ID")
    protected JobGroup jobGroup;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COIN_DISTRIBUTION_RULE_ID")
    protected CoinDistributionRule coinDistributionRule;

    public void setCoinDistributionRule(CoinDistributionRule coinDistributionRule) {
        this.coinDistributionRule = coinDistributionRule;
    }

    public CoinDistributionRule getCoinDistributionRule() {
        return coinDistributionRule;
    }


    public void setJobGroup(JobGroup jobGroup) {
        this.jobGroup = jobGroup;
    }

    public JobGroup getJobGroup() {
        return jobGroup;
    }


}