package kz.uco.tsadv.entity.tb.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_OBJECT_TYPE")
@Entity(name = "tsadv$ObjectType")
public class ObjectType extends AbstractDictionary {
    private static final long serialVersionUID = 7449501083937046451L;

}