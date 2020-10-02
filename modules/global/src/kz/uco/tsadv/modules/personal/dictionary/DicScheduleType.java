package kz.uco.tsadv.modules.personal.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_SCHEDULE_TYPE")
@Entity(name = "tsadv$DicScheduleType")
public class DicScheduleType extends AbstractDictionary {
    private static final long serialVersionUID = 6086640749323035236L;

}