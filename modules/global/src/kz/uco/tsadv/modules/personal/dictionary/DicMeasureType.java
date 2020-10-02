package kz.uco.tsadv.modules.personal.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_MEASURE_TYPE")
@Entity(name = "tsadv$DicMeasureType")
public class DicMeasureType extends AbstractDictionary {
    private static final long serialVersionUID = 6287713555626460663L;

}