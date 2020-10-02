package kz.uco.tsadv.modules.recognition;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import kz.uco.tsadv.modules.personal.group.GradeGroup;
import com.haulmont.cuba.core.entity.StandardEntity;

@Table(name = "TSADV_COIN_DISTRIBUTION_GRADE")
@Entity(name = "tsadv$CoinDistributionGrade")
public class CoinDistributionGrade extends StandardEntity {
    private static final long serialVersionUID = -3908511223431113718L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "GRADE_GROUP_ID")
    protected GradeGroup gradeGroup;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COIN_DISTRIBUTION_RULE_ID")
    protected CoinDistributionRule coinDistributionRule;

    public void setCoinDistributionRule(CoinDistributionRule coinDistributionRule) {
        this.coinDistributionRule = coinDistributionRule;
    }

    public CoinDistributionRule getCoinDistributionRule() {
        return coinDistributionRule;
    }


    public void setGradeGroup(GradeGroup gradeGroup) {
        this.gradeGroup = gradeGroup;
    }

    public GradeGroup getGradeGroup() {
        return gradeGroup;
    }


}