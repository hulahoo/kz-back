package kz.uco.tsadv.modules.personal.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue,langValue1,langValue2,langValue3,langValue4,langValue5,code,endDate,startDate")
@Table(name = "TSADV_DIC_SCHEDULE_PURPOSE")
@Entity(name = "tsadv_DicSchedulePurpose")
public class DicSchedulePurpose extends AbstractDictionary {
    private static final long serialVersionUID = 2966422639236478026L;
}