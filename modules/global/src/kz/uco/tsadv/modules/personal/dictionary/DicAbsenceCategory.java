package kz.uco.tsadv.modules.personal.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_ABSENCE_CATEGORY")
@Entity(name = "tsadv$DicAbsenceCategory")
public class DicAbsenceCategory extends AbstractDictionary {
    private static final long serialVersionUID = -4870742736160365317L;

}