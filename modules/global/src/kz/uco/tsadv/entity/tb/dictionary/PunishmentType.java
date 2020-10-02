package kz.uco.tsadv.entity.tb.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_PUNISHMENT_TYPE")
@Entity(name = "tsadv$PunishmentType")
public class PunishmentType extends AbstractDictionary {
    private static final long serialVersionUID = 3682578134259892454L;

}