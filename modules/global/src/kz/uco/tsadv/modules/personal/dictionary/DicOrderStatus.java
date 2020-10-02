package kz.uco.tsadv.modules.personal.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_ORDER_STATUS")
@Entity(name = "tsadv$DicOrderStatus")
public class DicOrderStatus extends AbstractDictionary {
    private static final long serialVersionUID = -8088487264851526969L;

}