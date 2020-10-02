package kz.uco.tsadv.modules.personal.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_ABSENCE_STATUS")
@Entity(name = "tsadv$DicAbsenceStatus")
public class DicAbsenceStatus extends AbstractDictionary {
    private static final long serialVersionUID = 7255228346824682778L;

}