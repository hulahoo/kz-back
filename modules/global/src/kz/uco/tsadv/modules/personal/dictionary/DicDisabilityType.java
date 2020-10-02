package kz.uco.tsadv.modules.personal.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_DISABILITY_TYPE")
@Entity(name = "tsadv$DicDisabilityType")
public class DicDisabilityType extends AbstractDictionary {
    private static final long serialVersionUID = -4007224257396184097L;

}