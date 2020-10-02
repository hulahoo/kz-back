package kz.uco.tsadv.modules.personal.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_NONRESIDENT_TYPE")
@Entity(name = "tsadv$DicNonresidentType")
public class DicNonresidentType extends AbstractDictionary {
    private static final long serialVersionUID = -9189327234622085164L;

}