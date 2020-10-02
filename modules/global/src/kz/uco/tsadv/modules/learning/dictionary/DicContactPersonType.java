package kz.uco.tsadv.modules.learning.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_CONTACT_PERSON_TYPE")
@Entity(name = "tsadv$DicContactPersonType")
public class DicContactPersonType extends AbstractDictionary {
    private static final long serialVersionUID = 1303150115591764801L;

}