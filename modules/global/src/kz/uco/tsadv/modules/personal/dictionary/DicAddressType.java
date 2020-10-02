package kz.uco.tsadv.modules.personal.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_ADDRESS_TYPE")
@Entity(name = "tsadv$DicAddressType")
public class DicAddressType extends AbstractDictionary {
    private static final long serialVersionUID = 6195819841198723200L;

}