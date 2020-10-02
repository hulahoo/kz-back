package kz.uco.tsadv.entity.tb.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_STATUS_MSDS")
@Entity(name = "tsadv$StatusMsds")
public class StatusMsds extends AbstractDictionary {
    private static final long serialVersionUID = 8403243908079535250L;

}