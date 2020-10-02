package kz.uco.tsadv.entity.tb.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_CONTROL_TYPE")
@Entity(name = "tsadv$ControlType")
public class ControlType extends AbstractDictionary {
    private static final long serialVersionUID = 3459248183391830637L;

}