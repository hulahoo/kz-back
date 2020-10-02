package kz.uco.tsadv.entity.tb.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_INCIDENT_TYPE")
@Entity(name = "tsadv$IncidentType")
public class IncidentType extends AbstractDictionary {
    private static final long serialVersionUID = -8303140755893808990L;

}