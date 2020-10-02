package kz.uco.tsadv.entity.tb.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_RESULT")
@Entity(name = "tsadv$Result")
public class Result extends AbstractDictionary {
    private static final long serialVersionUID = 4597542049549177487L;

}