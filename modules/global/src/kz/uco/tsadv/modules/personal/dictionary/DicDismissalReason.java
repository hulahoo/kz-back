package kz.uco.tsadv.modules.personal.dictionary;

import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TSADV_DIC_DISMISSAL_REASON")
@Entity(name = "tsadv$DicDismissalReason")
public class DicDismissalReason extends AbstractDictionary {
    private static final long serialVersionUID = 2184983800567185741L;

}