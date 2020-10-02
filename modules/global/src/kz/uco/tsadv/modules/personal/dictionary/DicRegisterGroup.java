package kz.uco.tsadv.modules.personal.dictionary;

import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TSADV_DIC_REGISTER_GROUP")
@Entity(name = "tsadv$DicRegisterGroup")
public class DicRegisterGroup extends AbstractDictionary {
    private static final long serialVersionUID = 3755708467204184652L;

}