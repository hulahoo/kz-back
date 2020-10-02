package kz.uco.tsadv.modules.recognition.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;
import javax.persistence.Column;

@Table(name = "TSADV_DIC_PERSON_PREFERENCE_TYPE")
@Entity(name = "tsadv$DicPersonPreferenceType")
public class DicPersonPreferenceType extends AbstractDictionary {
    private static final long serialVersionUID = 2398144370373510310L;

    @Column(name = "COINS")
    protected Long coins;

    public void setCoins(Long coins) {
        this.coins = coins;
    }

    public Long getCoins() {
        return coins;
    }


}