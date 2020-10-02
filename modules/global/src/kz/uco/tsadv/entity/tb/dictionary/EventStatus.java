package kz.uco.tsadv.entity.tb.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_EVENT_STATUS")
@Entity(name = "tsadv$EventStatus")
public class EventStatus extends AbstractDictionary {
    private static final long serialVersionUID = -1459988045851347887L;

}