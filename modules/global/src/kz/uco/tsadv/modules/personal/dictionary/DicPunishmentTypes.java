package kz.uco.tsadv.modules.personal.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_DIC_PUNISHMENT_TYPES")
@Entity(name = "tsadv$DicPunishmentTypes")
public class DicPunishmentTypes extends AbstractDictionary {
    private static final long serialVersionUID = -6848877074475394078L;

}