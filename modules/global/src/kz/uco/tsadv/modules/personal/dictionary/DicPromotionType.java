package kz.uco.tsadv.modules.personal.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_DIC_PROMOTION_TYPE")
@Entity(name = "tsadv$DicPromotionType")
public class DicPromotionType extends AbstractDictionary {
    private static final long serialVersionUID = -6387038130371600595L;

}