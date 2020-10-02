package kz.uco.tsadv.modules.personal.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_RELATIONSHIP_TYPE")
@Entity(name = "tsadv$DicRelationshipType")
public class DicRelationshipType extends AbstractDictionary {
    private static final long serialVersionUID = -1539143496120131799L;

}