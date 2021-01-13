package kz.uco.tsadv.modules.recruitment.dictionary;

import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TSADV_DIC_INDUSTRY")
@Entity(name = "tsadv_DicIndustry")
public class DicIndustry extends AbstractDictionary {
    private static final long serialVersionUID = -465135524781855860L;
}