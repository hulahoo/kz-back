package kz.uco.tsadv.modules.personal.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_ASSIGNMENT")
@Entity(name = "tsadv$DicAssignment")
public class DicAssignment extends AbstractDictionary {
    private static final long serialVersionUID = -5570399285248950702L;

}