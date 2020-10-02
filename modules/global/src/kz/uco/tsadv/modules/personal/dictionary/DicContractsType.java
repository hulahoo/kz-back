package kz.uco.tsadv.modules.personal.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_DIC_CONTRACTS_TYPE")
@Entity(name = "tsadv$DicContractsType")
public class DicContractsType extends AbstractDictionary {
    private static final long serialVersionUID = -2481874949996708116L;

}