package kz.uco.tsadv.modules.personal.dictionary;

import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TSADV_DIC_EARNING_POLICY")
@Entity(name = "tsadv_DicEarningPolicy")
public class DicEarningPolicy extends AbstractDictionary {
    private static final long serialVersionUID = -5013125136330154367L;
}