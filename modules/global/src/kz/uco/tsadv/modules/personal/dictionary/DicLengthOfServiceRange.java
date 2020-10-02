package kz.uco.tsadv.modules.personal.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import kz.uco.base.entity.abstraction.AbstractDictionary;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s %s %s|langValue,min,max")
@Table(name = "TSADV_DIC_LENGTH_OF_SERVICE_RANGE")
@Entity(name = "tsadv$DicLengthOfServiceRange")
public class DicLengthOfServiceRange extends AbstractDictionary {
    private static final long serialVersionUID = 7146645533590001348L;

    @Column(name = "RANGE_ORDER")
    protected Integer rangeOrder;

    @Column(name = "MIN_")
    protected Double min;

    @Column(name = "MAX_")
    protected Double max;

    public void setRangeOrder(Integer rangeOrder) {
        this.rangeOrder = rangeOrder;
    }

    public Integer getRangeOrder() {
        return rangeOrder;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public Double getMin() {
        return min;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    public Double getMax() {
        return max;
    }


}