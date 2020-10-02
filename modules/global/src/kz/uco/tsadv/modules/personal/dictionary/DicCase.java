package kz.uco.tsadv.modules.personal.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_CASE")
@Entity(name = "tsadv$DicCase")
public class DicCase extends AbstractDictionary {
    private static final long serialVersionUID = 1272499661083062219L;

}