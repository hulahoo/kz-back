package kz.uco.tsadv.entity.tb.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_INTOXICATION_TYPE")
@Entity(name = "tsadv$IntoxicationType")
public class IntoxicationType extends AbstractDictionary {
    private static final long serialVersionUID = -731366512525965313L;

}