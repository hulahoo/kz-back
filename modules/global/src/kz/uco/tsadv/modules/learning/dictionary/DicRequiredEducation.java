package kz.uco.tsadv.modules.learning.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_DIC_REQUIRED_EDUCATION")
@Entity(name = "tsadv$DicRequiredEducation")
public class DicRequiredEducation extends AbstractDictionary {
    private static final long serialVersionUID = -8475937837336763255L;

}