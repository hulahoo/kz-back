package kz.uco.tsadv.modules.personal.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue,langValue1,langValue2,langValue3,langValue4,langValue5,startDate,endDate,code")
@Table(name = "TSADV_DIC_TYPE_OF_WORK")
@Entity(name = "tsadv_DicTypeOfWork")
public class DicTypeOfWork extends AbstractDictionary {
    private static final long serialVersionUID = 5080033209718056048L;
}