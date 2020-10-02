package kz.uco.tsadv.modules.recognition;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import com.haulmont.cuba.core.entity.StandardEntity;

@Table(name = "TSADV_COIN_DISTRIBUTION_PERSON")
@Entity(name = "tsadv$CoinDistributionPerson")
public class CoinDistributionPerson extends StandardEntity {
    private static final long serialVersionUID = 858115478047062099L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COIN_DISTRIBUTION_RULE_ID")
    protected CoinDistributionRule coinDistributionRule;

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setCoinDistributionRule(CoinDistributionRule coinDistributionRule) {
        this.coinDistributionRule = coinDistributionRule;
    }

    public CoinDistributionRule getCoinDistributionRule() {
        return coinDistributionRule;
    }


}