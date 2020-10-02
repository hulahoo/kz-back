package kz.uco.tsadv.modules.personal.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_DIC_ACCOMMODATION_TYPE")
@Entity(name = "tsadv$DicAccommodationType")
public class DicAccommodationType extends AbstractDictionary {
    private static final long serialVersionUID = -4078607133980840931L;

}