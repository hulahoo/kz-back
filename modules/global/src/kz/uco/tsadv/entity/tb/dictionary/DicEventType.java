package kz.uco.tsadv.entity.tb.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_DIC_EVENT_TYPE")
@Entity(name = "tsadv$DicEventType")
public class DicEventType extends AbstractDictionary {
    private static final long serialVersionUID = 6555779848611076960L;

}