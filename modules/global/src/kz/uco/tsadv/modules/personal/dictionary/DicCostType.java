package kz.uco.tsadv.modules.personal.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_COST_TYPE")
@Entity(name = "tsadv$DicCostType")
public class DicCostType extends AbstractDictionary {
    private static final long serialVersionUID = -5760261519891811770L;
    @Column(name = "IS_BUSINESS_TRIP")
    protected Boolean isBusinessTrip;

    public void setIsBusinessTrip(Boolean isBusinessTrip) {
        this.isBusinessTrip = isBusinessTrip;
    }

    public Boolean getIsBusinessTrip() {
        return isBusinessTrip;
    }



}