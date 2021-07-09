package kz.uco.tsadv.modules.learning.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue,langValue1,langValue2,langValue3,langValue4,langValue5,code")
@Table(name = "TSADV_DIC_PROGRAMM_CODE")
@Entity(name = "tsadv_DicProgrammCode")
public class DicProgrammCode extends AbstractDictionary {
    private static final long serialVersionUID = 7151920195128392468L;
}