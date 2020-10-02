package kz.uco.tsadv.modules.performance.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_UOM")
@Entity(name = "tsadv$DicUOM")
public class DicUOM extends AbstractDictionary {
    // todo - заменить на общий справочник kz.uco.tsadv.global.dictionary.DicUnitOfMeasure
    private static final long serialVersionUID = 1902162900654559313L;

}