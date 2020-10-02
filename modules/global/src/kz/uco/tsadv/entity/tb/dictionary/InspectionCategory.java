package kz.uco.tsadv.entity.tb.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_INSPECTION_CATEGORY")
@Entity(name = "tsadv$InspectionCategory")
public class InspectionCategory extends AbstractDictionary {
    private static final long serialVersionUID = -6282886019454783445L;

}