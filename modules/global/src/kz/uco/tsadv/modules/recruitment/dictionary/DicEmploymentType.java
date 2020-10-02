package kz.uco.tsadv.modules.recruitment.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_EMPLOYMENT_TYPE")
@Entity(name = "tsadv$DicEmploymentType")
public class DicEmploymentType extends AbstractDictionary {
    private static final long serialVersionUID = -3166768731225762884L;

}