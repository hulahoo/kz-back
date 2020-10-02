package kz.uco.tsadv.modules.personal.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import kz.uco.tsadv.modules.personal.dictionary.*;
import kz.uco.tsadv.modules.personal.dictionary.DicAccommodationType;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_DIC_ACCOMMODATION_CLASS")
@Entity(name = "tsadv$DicAccommodationClass")
public class DicAccommodationClass extends AbstractDictionary {
    private static final long serialVersionUID = -708836552665967466L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DIC_ACCOMMODATION_TYPE_ID")
    protected kz.uco.tsadv.modules.personal.dictionary.DicAccommodationType dicAccommodationType;

    public void setDicAccommodationType(kz.uco.tsadv.modules.personal.dictionary.DicAccommodationType dicAccommodationType) {
        this.dicAccommodationType = dicAccommodationType;
    }

    public DicAccommodationType getDicAccommodationType() {
        return dicAccommodationType;
    }


}