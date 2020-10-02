package kz.uco.tsadv.entity.tb.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_INVESTIGATION_TYPE")
@Entity(name = "tsadv$InvestigationType")
public class InvestigationType extends AbstractDictionary {
    private static final long serialVersionUID = 5409737615089126518L;

}