package kz.uco.tsadv.modules.personal.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TSADV_DIC_FORM_STUDY")
@Entity(name = "tsadv_DicFormStudy")
@NamePattern("%s|langValue,langValue1,langValue2,langValue3,langValue4,langValue5")
public class DicFormStudy extends AbstractDictionary {
    private static final long serialVersionUID = 2087809033981897238L;
}