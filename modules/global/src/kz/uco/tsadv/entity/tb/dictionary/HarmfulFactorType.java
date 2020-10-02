package kz.uco.tsadv.entity.tb.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_HARMFUL_FACTOR_TYPE")
@Entity(name = "tsadv$HarmfulFactorType")
public class HarmfulFactorType extends AbstractDictionary {
    private static final long serialVersionUID = -1746754173949038186L;

}