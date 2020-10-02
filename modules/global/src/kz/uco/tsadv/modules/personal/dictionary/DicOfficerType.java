package kz.uco.tsadv.modules.personal.dictionary;

import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TSADV_DIC_OFFICER_TYPE")
@Entity(name = "tsadv$DicOfficerType")
public class DicOfficerType extends AbstractDictionary {
    private static final long serialVersionUID = 7319822893260141696L;

}