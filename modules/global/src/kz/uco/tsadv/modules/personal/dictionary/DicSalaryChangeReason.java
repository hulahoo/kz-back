package kz.uco.tsadv.modules.personal.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_SALARY_CHANGE_REASON")
@Entity(name = "tsadv$DicSalaryChangeReason")
public class DicSalaryChangeReason extends AbstractDictionary {
    private static final long serialVersionUID = -1210057706255556161L;

}