package kz.uco.tsadv.modules.personal.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_TRADE_UNION")
@Entity(name = "tsadv$DicTradeUnion")
public class DicTradeUnion extends AbstractDictionary {
    private static final long serialVersionUID = 1337555198029240175L;

}