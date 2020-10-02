package kz.uco.tsadv.entity.tb.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_TRAUMA_LEVEL")
@Entity(name = "tsadv$TraumaLevel")
public class TraumaLevel extends AbstractDictionary {
    private static final long serialVersionUID = -716129182677655227L;

}