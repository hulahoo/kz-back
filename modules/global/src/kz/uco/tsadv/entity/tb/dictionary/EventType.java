package kz.uco.tsadv.entity.tb.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_EVENT_TYPE")
@Entity(name = "tsadv$EventType")
public class EventType extends AbstractDictionary {
    private static final long serialVersionUID = -5628999422407000356L;

}