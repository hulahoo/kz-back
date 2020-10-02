package kz.uco.tsadv.global.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_NATIONALITY")
@Entity(name = "tsadv$DicNationality")
public class DicNationality extends AbstractDictionary {
    private static final long serialVersionUID = -247578673348121065L;

}