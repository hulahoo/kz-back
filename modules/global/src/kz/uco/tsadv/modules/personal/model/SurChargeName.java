package kz.uco.tsadv.modules.personal.model;

import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TSADV_SUR_CHARGE_NAME")
@Entity(name = "tsadv$SurChargeName")
public class SurChargeName extends AbstractDictionary {
    private static final long serialVersionUID = -8367702824416829011L;

}