package kz.uco.tsadv.modules.personal.dictionary;

import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TSADV_DIC_REGISTER_CATEGORY")
@Entity(name = "tsadv$DicRegisterCategory")
public class DicRegisterCategory extends AbstractDictionary {
    private static final long serialVersionUID = 4981660686215662139L;

}