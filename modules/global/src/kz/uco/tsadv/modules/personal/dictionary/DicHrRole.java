package kz.uco.tsadv.modules.personal.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_HR_ROLE")
@Entity(name = "tsadv$DicHrRole")
public class DicHrRole extends AbstractDictionary {
    private static final long serialVersionUID = -7401200303290958260L;


}