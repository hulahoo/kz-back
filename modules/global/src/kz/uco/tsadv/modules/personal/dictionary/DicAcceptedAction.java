package kz.uco.tsadv.modules.personal.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_DIC_ACCEPTED_ACTION")
@Entity(name = "tsadv$DicAcceptedAction")
public class DicAcceptedAction extends AbstractDictionary {
    private static final long serialVersionUID = -578639247127261538L;

}