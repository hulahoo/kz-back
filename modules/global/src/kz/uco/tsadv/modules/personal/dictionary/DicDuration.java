package kz.uco.tsadv.modules.personal.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_DURATION")
@Entity(name = "tsadv$DicDuration")
public class DicDuration extends AbstractDictionary {
    private static final long serialVersionUID = -6366896151624620181L;

}