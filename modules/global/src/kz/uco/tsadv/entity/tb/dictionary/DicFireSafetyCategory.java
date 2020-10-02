package kz.uco.tsadv.entity.tb.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_DIC_FIRE_SAFETY_CATEGORY")
@Entity(name = "tsadv$DicFireSafetyCategory")
public class DicFireSafetyCategory extends AbstractDictionary {
    private static final long serialVersionUID = 4767435512045232344L;

}