package kz.uco.tsadv.modules.learning.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue,langValue1,langValue2,langValue3,langValue4,langValue5,code")
@Table(name = "TSADV_DIC_TYPE_OF_TRAINING")
@Entity(name = "tsadv_DicTypeOfTraining")
public class DicTypeOfTraining extends AbstractDictionary {
    private static final long serialVersionUID = 3639479127217590933L;
}