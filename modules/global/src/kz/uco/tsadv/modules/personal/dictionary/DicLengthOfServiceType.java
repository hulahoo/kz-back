package kz.uco.tsadv.modules.personal.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.base.entity.abstraction.AbstractDictionary;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_LENGTH_OF_SERVICE_TYPE")
@Entity(name = "tsadv$DicLengthOfServiceType")
public class DicLengthOfServiceType extends AbstractDictionary {
    private static final long serialVersionUID = 6877216321689255125L;

}