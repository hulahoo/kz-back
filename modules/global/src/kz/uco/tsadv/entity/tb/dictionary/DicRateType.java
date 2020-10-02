package kz.uco.tsadv.entity.tb.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_DIC_RATE_TYPE")
@Entity(name = "tsadv$DicRateType")
public class DicRateType extends AbstractDictionary {
    private static final long serialVersionUID = 3683935403797924392L;

}