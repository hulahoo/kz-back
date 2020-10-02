package kz.uco.tsadv.modules.personal.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_DIC_TRANSPORT_TYPE")
@Entity(name = "tsadv$DicTransportType")
public class DicTransportType extends AbstractDictionary {
    private static final long serialVersionUID = -1491971668565430236L;

}