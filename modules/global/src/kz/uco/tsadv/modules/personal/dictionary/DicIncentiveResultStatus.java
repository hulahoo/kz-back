package kz.uco.tsadv.modules.personal.dictionary;

import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TSADV_DIC_INCENTIVE_RESULT_STATUS")
@Entity(name = "tsadv_DicIncentiveResultStatus")
public class DicIncentiveResultStatus extends AbstractDictionary {
    private static final long serialVersionUID = -5729319145323678974L;
}