package kz.uco.tsadv.modules.personal.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_DIC_UDO")
@Entity(name = "tsadv$DicUdo")
public class DicUdo extends AbstractDictionary {
    private static final long serialVersionUID = -2296023650338571262L;

}