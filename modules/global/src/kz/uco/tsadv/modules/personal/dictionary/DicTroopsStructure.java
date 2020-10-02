package kz.uco.tsadv.modules.personal.dictionary;

import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TSADV_DIC_TROOPS_STRUCTURE")
@Entity(name = "tsadv$DicTroopsStructure")
public class DicTroopsStructure extends AbstractDictionary {
    private static final long serialVersionUID = 4154169762111070040L;

}