package kz.uco.tsadv.entity.tb.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_DIRECT_REASON")
@Entity(name = "tsadv$DirectReason")
public class DirectReason extends AbstractDictionary {
    private static final long serialVersionUID = 6578630426903897449L;

}