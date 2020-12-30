package kz.uco.tsadv.modules.personal.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TSADV_DIC_REASON_BENIFIT")
@Entity(name = "tsadv_DicReasonBenifit")
@NamePattern("%s|langValue,langValue1,langValue2,langValue3,langValue4,langValue5")
public class DicReasonBenifit extends AbstractDictionary {
    private static final long serialVersionUID = -6790795590244290698L;
}