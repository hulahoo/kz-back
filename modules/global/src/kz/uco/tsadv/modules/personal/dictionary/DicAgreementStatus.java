package kz.uco.tsadv.modules.personal.dictionary;

import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TSADV_DIC_AGREEMENT_STATUS")
@Entity(name = "tsadv$DicAgreementStatus")
public class DicAgreementStatus extends AbstractDictionary {
    private static final long serialVersionUID = 5070502863896587594L;

}