package kz.uco.tsadv.modules.personal.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_DIC_ADDITIONAL_EDUCATION")
@Entity(name = "tsadv$DicAdditionalEducation")
public class DicAdditionalEducation extends AbstractDictionary {
    private static final long serialVersionUID = -6976127297704373005L;

}