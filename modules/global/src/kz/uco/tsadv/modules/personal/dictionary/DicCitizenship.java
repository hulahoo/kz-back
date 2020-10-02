package kz.uco.tsadv.modules.personal.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_CITIZENSHIP")
@Entity(name = "tsadv$DicCitizenship")
public class DicCitizenship extends AbstractDictionary {
    private static final long serialVersionUID = 4224126499944424680L;

}