package kz.uco.tsadv.modules.personal.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TSADV_DIC_CRIMINAL_LIABILITY_TYPE")
@Entity(name = "tsadv_DicCriminalLiabilityType")
@NamePattern("%s|langValue,langValue1,langValue2,langValue3,langValue4,langValue5")
public class DicCriminalLiabilityType extends AbstractDictionary {
    private static final long serialVersionUID = -4963941741440597502L;
}