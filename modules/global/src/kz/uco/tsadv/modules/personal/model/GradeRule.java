package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.dictionary.DicCurrency;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.enums.GrossNet;

import javax.persistence.*;

@NamePattern("%s|ruleName")
@Table(name = "TSADV_GRADE_RULE")
@Entity(name = "tsadv$GradeRule")
public class GradeRule extends AbstractParentEntity {
    private static final long serialVersionUID = -1789257914663474980L;

    @Column(name = "RULE_NAME", nullable = false, length = 1000)
    protected String ruleName;



    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CURRENCY_ID")
    protected DicCurrency currency;


    @Column(name = "GROSS_NET")
    protected String grossNet;




    public void setGrossNet(GrossNet grossNet) {
        this.grossNet = grossNet == null ? null : grossNet.getId();
    }

    public GrossNet getGrossNet() {
        return grossNet == null ? null : GrossNet.fromId(grossNet);
    }


    public DicCurrency getCurrency() {
        return currency;
    }

    public void setCurrency(DicCurrency currency) {
        this.currency = currency;
    }


    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleName() {
        return ruleName;
    }


}