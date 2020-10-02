package kz.uco.tsadv.modules.learning.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_DIC_EDUCATIONAL_ESTABLISHMENT_TYPE")
@Entity(name = "tsadv$DicEducationalEstablishmentType")
public class DicEducationalEstablishmentType extends AbstractDictionary {
    private static final long serialVersionUID = 4997393909269096999L;

}