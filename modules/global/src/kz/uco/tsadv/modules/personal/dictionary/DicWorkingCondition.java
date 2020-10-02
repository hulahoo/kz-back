package kz.uco.tsadv.modules.personal.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_WORKING_CONDITION")
@Entity(name = "tsadv$DicWorkingCondition")
public class DicWorkingCondition extends AbstractDictionary {
    private static final long serialVersionUID = 4211935988767647604L;

}