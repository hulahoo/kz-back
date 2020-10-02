package kz.uco.tsadv.modules.personal.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Listeners("tsadv_LanguageListener")
@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_LANGUAGE")
@Entity(name = "tsadv$DicLanguage")
public class DicLanguage extends AbstractDictionary {
    private static final long serialVersionUID = 4210946122130586862L;

}