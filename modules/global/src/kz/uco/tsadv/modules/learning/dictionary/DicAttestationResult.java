package kz.uco.tsadv.modules.learning.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_ATTESTATION_RESULT")
@Entity(name = "tsadv$DicAttestationResult")
public class DicAttestationResult extends AbstractDictionary {
    private static final long serialVersionUID = -5503571023272093758L;

}