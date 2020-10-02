package kz.uco.tsadv.modules.learning.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_EDUCATION_TYPE")
@Entity(name = "tsadv$DicEducationType")
public class DicEducationType extends AbstractDictionary {
    private static final long serialVersionUID = -6439567792676723081L;

}