package kz.uco.tsadv.modules.personal.dictionary;

import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TSADV_DIC_REQUEST_STATUS")
@Entity(name = "tsadv$DicRequestStatus")
public class DicRequestStatus extends AbstractDictionary {
    private static final long serialVersionUID = 989846701473647172L;

}