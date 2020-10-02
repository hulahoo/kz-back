package kz.uco.tsadv.modules.recognition;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import com.haulmont.cuba.core.entity.StandardEntity;

@Table(name = "TSADV_PERSON_COIN")
@Entity(name = "tsadv$PersonCoin")
public class PersonCoin extends StandardEntity {
    private static final long serialVersionUID = 3663088283213704834L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_GROUP_ID", unique = true)
    protected PersonGroupExt personGroup;

    @NotNull
    @Column(name = "COINS", nullable = false)
    protected Long coins;

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setCoins(Long coins) {
        this.coins = coins;
    }

    public Long getCoins() {
        return coins;
    }


}