package kz.uco.tsadv.modules.learning.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_ATTESTATION_TYPE")
@Entity(name = "tsadv$DicAttestationType")
public class DicAttestationType extends AbstractDictionary {
    private static final long serialVersionUID = -489612763086774796L;

}