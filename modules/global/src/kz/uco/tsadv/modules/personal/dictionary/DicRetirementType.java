package kz.uco.tsadv.modules.personal.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_RETIREMENT_TYPE")
@Entity(name = "tsadv$DicRetirementType")
public class DicRetirementType extends AbstractDictionary {
    private static final long serialVersionUID = -4766017267198075146L;

}