package kz.uco.tsadv.entity.tb.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_DISABILITY_GROUP")
@Entity(name = "tsadv$DisabilityGroup")
public class DisabilityGroup extends AbstractDictionary {
    private static final long serialVersionUID = -8035078518172166965L;

}