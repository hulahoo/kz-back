package kz.uco.tsadv.modules.personal.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_PAYROLL")
@Entity(name = "tsadv$DicPayroll")
public class DicPayroll extends AbstractDictionary {
    private static final long serialVersionUID = 4954220937467548216L;
}