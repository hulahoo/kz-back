package kz.uco.tsadv.modules.personal.dictionary;


import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_ORDER_TYPE")
@Entity(name = "tsadv$DicOrderType")
public class DicOrderType extends AbstractDictionary {
    private static final long serialVersionUID = -2271766175415065737L;

}