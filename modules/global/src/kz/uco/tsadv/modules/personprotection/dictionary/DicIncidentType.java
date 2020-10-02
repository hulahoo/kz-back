package kz.uco.tsadv.modules.personprotection.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_DIC_INCIDENT_TYPE")
@Entity(name = "tsadv$DicIncidentType")
public class DicIncidentType extends AbstractDictionary {
    private static final long serialVersionUID = -7999039683053578828L;

}