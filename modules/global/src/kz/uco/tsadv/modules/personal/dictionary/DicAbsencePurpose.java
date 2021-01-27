package kz.uco.tsadv.modules.personal.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue,langValue1,langValue2,langValue3,langValue4,langValue5,code,startDate,endDate")
@Table(name = "TSADV_DIC_ABSENCE_PURPOSE")
@Entity(name = "tsadv_DicAbsencePurpose")
public class DicAbsencePurpose extends AbstractDictionary {
    private static final long serialVersionUID = -734391157377061899L;
}