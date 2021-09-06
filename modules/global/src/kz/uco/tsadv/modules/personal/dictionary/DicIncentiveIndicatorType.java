package kz.uco.tsadv.modules.personal.dictionary;

import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TSADV_DIC_INCENTIVE_INDICATOR_TYPE")
@Entity(name = "tsadv_DicIncentiveIndicatorType")
public class DicIncentiveIndicatorType extends AbstractDictionary {
    private static final long serialVersionUID = -7109284677226997474L;
}