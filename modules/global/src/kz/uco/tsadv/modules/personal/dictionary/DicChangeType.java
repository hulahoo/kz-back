package kz.uco.tsadv.modules.personal.dictionary;

import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TSADV_DIC_CHANGE_TYPE")
@Entity(name = "tsadv_DicChangeType")
public class DicChangeType extends AbstractDictionary {
    private static final long serialVersionUID = 3605581520022045640L;
}