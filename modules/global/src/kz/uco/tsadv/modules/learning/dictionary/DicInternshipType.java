package kz.uco.tsadv.modules.learning.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_DIC_INTERNSHIP_TYPE")
@Entity(name = "tsadv$DicInternshipType")
public class DicInternshipType extends AbstractDictionary {
    private static final long serialVersionUID = 1054213913976808712L;

}