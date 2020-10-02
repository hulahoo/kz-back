package kz.uco.tsadv.modules.personal.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import kz.uco.tsadv.modules.personal.dictionary.*;
import kz.uco.tsadv.modules.personal.dictionary.DicPromotionType;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_DIC_AWARD_TYPE")
@Entity(name = "tsadv$DicAwardType")
public class DicAwardType extends AbstractDictionary {
    private static final long serialVersionUID = -3354372063092016835L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROMOTION_TYPE_ID")
    protected kz.uco.tsadv.modules.personal.dictionary.DicPromotionType promotionType;

    public void setPromotionType(kz.uco.tsadv.modules.personal.dictionary.DicPromotionType promotionType) {
        this.promotionType = promotionType;
    }

    public DicPromotionType getPromotionType() {
        return promotionType;
    }


}