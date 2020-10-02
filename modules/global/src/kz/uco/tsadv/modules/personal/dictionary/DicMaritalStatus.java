package kz.uco.tsadv.modules.personal.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_MARITAL_STATUS")
@Entity(name = "tsadv$DicMaritalStatus")
public class DicMaritalStatus extends AbstractDictionary {
    private static final long serialVersionUID = 500589578731911144L;

}