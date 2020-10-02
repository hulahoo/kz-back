package kz.uco.tsadv.entity.tb.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_EXCAVATION")
@Entity(name = "tsadv$Excavation")
public class Excavation extends AbstractDictionary {
    private static final long serialVersionUID = 4477509666188279337L;

}