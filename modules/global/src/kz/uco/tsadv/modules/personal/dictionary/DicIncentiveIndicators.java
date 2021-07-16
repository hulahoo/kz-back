package kz.uco.tsadv.modules.personal.dictionary;

import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TSADV_DIC_INCENTIVE_INDICATORS")
@Entity(name = "tsadv_DicIncentiveIndicators")
public class DicIncentiveIndicators extends AbstractDictionary {
    private static final long serialVersionUID = -6323124983682771731L;
}