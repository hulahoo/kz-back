package kz.uco.tsadv.entity;

import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TSADV_ABSENCE_REQUEST_STATUS")
@Entity(name = "tsadv_AbsenceRequestStatus")
public class AbsenceRequestStatus extends AbstractDictionary {
    private static final long serialVersionUID = -2926556913948205985L;
}