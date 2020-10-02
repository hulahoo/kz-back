package kz.uco.tsadv.modules.personal.dictionary;

import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TSADV_DIC_MILITARY_TYPE")
@Entity(name = "tsadv$DicMilitaryType")
public class DicMilitaryType extends AbstractDictionary {
    private static final long serialVersionUID = 883305059026179067L;

}