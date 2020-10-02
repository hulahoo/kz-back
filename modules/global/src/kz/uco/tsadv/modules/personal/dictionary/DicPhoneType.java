package kz.uco.tsadv.modules.personal.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_PHONE_TYPE")
@Entity(name = "tsadv$DicPhoneType")
public class DicPhoneType extends AbstractDictionary {
    private static final long serialVersionUID = 2059440670226431279L;

}