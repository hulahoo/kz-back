package kz.uco.tsadv.modules.personal.dictionary;

import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TSADV_DIC_DISMISSAL_STATUS")
@Entity(name = "tsadv$DicDismissalStatus")
public class DicDismissalStatus extends AbstractDictionary {
    private static final long serialVersionUID = -5963556297608314306L;

}