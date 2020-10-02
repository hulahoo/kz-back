package kz.uco.tsadv.entity.tb.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_ACCIDENT_TYPE")
@Entity(name = "tsadv$AccidentType")
public class AccidentType extends AbstractDictionary {
    private static final long serialVersionUID = 4524032940580905772L;

}