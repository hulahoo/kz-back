package kz.uco.tsadv.modules.performance.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_NINE_BOX_LEVEL")
@Entity(name = "tsadv$DicNineBoxLevel")
public class DicNineBoxLevel extends AbstractDictionary {
    private static final long serialVersionUID = -1867904657896202310L;

}