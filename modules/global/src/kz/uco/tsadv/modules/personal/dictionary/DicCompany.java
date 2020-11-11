package kz.uco.tsadv.modules.personal.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_COMPANY")
@Entity(name = "tsadv$DicCompany")
public class DicCompany extends AbstractDictionary {
    private static final long serialVersionUID = -2160784818541139976L;

}