package kz.uco.tsadv.entity.tb.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_INVESTIGATION_CONDUCTED")
@Entity(name = "tsadv$InvestigationConducted")
public class InvestigationConducted extends AbstractDictionary {
    private static final long serialVersionUID = -2641329683251249937L;

}