package kz.uco.tsadv.modules.personal.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_ORDER_REASON")
@Entity(name = "tsadv$DicOrderReason")
public class DicOrderReason extends AbstractDictionary {
    private static final long serialVersionUID = -3595386934808570088L;

}