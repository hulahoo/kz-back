package kz.uco.tsadv.entity.tb.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_INSPECTION_TYPE")
@Entity(name = "tsadv$InspectionType")
public class InspectionType extends AbstractDictionary {
    private static final long serialVersionUID = 4214745949476130515L;

}