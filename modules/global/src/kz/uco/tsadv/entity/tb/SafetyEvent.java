package kz.uco.tsadv.entity.tb;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import kz.uco.tsadv.entity.tb.dictionary.DicEventType;
import kz.uco.tsadv.entity.tb.dictionary.UOM;

@NamePattern("%s|name")
@Table(name = "TSADV_SAFETY_EVENT")
@Entity(name = "tsadv$SafetyEvent")
public class SafetyEvent extends StandardEntity {
    private static final long serialVersionUID = 1926125193991660471L;

    @Column(name = "CODE", nullable = false, length = 30)
    protected String code;

    @Column(name = "NAME", nullable = false, length = 1000)
    protected String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TYPE_ID")
    protected DicEventType type;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "UOM_ID")
    protected UOM uom;

    public UOM getUom() {
        return uom;
    }

    public void setUom(UOM uom) {
        this.uom = uom;
    }


    public DicEventType getType() {
        return type;
    }

    public void setType(DicEventType type) {
        this.type = type;
    }


    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


}