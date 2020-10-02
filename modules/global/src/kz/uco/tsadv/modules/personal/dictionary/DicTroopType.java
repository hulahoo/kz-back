package kz.uco.tsadv.modules.personal.dictionary;

import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TSADV_DIC_TROOP_TYPE")
@Entity(name = "tsadv$DicTroopType")
public class DicTroopType extends AbstractDictionary {
    private static final long serialVersionUID = 4188644646619481584L;

}