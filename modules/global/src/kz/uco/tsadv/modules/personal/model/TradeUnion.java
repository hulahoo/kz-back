package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.tsadv.modules.personal.dictionary.DicTradeUnion;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import java.util.Date;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

@NamePattern("%s %s|personGroup,dicTradeUnion")
@Table(name = "TSADV_TRADE_UNION")
@Entity(name = "tsadv$TradeUnion")
public class TradeUnion extends AbstractParentEntity {
    private static final long serialVersionUID = 2411067020092238711L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @Temporal(TemporalType.DATE)
    @Column(name = "JOING_TRADE_UNION")
    protected Date joingTradeUnion;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_TO")
    protected Date dateTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DIC_TRADE_UNION_ID")
    protected DicTradeUnion dicTradeUnion;

    public void setDicTradeUnion(DicTradeUnion dicTradeUnion) {
        this.dicTradeUnion = dicTradeUnion;
    }

    public DicTradeUnion getDicTradeUnion() {
        return dicTradeUnion;
    }


    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setJoingTradeUnion(Date joingTradeUnion) {
        this.joingTradeUnion = joingTradeUnion;
    }

    public Date getJoingTradeUnion() {
        return joingTradeUnion;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public Date getDateTo() {
        return dateTo;
    }


}