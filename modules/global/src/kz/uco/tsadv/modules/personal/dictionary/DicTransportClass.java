package kz.uco.tsadv.modules.personal.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import kz.uco.tsadv.modules.personal.dictionary.*;
import kz.uco.tsadv.modules.personal.dictionary.DicTransportType;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_DIC_TRANSPORT_CLASS")
@Entity(name = "tsadv$DicTransportClass")
public class DicTransportClass extends AbstractDictionary {
    private static final long serialVersionUID = 1916371340118471078L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRANSPORT_TYPE_ID")
    protected kz.uco.tsadv.modules.personal.dictionary.DicTransportType transportType;

    public void setTransportType(kz.uco.tsadv.modules.personal.dictionary.DicTransportType transportType) {
        this.transportType = transportType;
    }

    public DicTransportType getTransportType() {
        return transportType;
    }


}