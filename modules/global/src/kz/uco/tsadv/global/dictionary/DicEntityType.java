package kz.uco.tsadv.global.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_ENTITY_TYPE")
@Entity(name = "tsadv$DicEntityType")
public class DicEntityType extends AbstractDictionary {
    private static final long serialVersionUID = 2637752966481362710L;

}