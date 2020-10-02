package kz.uco.tsadv.entity.tb.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_TECHNICAL_STATUS_DICTIONARY")
@Entity(name = "tsadv$TechnicalStatusDictionary")
public class TechnicalStatusDictionary extends AbstractDictionary {
    private static final long serialVersionUID = 959556949567202806L;

}