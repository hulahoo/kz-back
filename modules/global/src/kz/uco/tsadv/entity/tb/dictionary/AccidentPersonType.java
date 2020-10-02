package kz.uco.tsadv.entity.tb.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_ACCIDENT_PERSON_TYPE")
@Entity(name = "tsadv$AccidentPersonType")
public class AccidentPersonType extends AbstractDictionary {
    private static final long serialVersionUID = 7533360970200221195L;

}