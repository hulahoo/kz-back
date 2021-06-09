package kz.uco.tsadv.modules.personal.dictionary;

import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TSADV_DIC_KATO")
@Entity(name = "tsadv_DicKato")
public class DicKato extends AbstractDictionary {
    private static final long serialVersionUID = 5336454975663628160L;
}