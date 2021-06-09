package kz.uco.tsadv.modules.personal.dictionary;

import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TSADV_DIC_STREET_TYPE")
@Entity(name = "tsadv_DicStreetType")
public class DicStreetType extends AbstractDictionary {
    private static final long serialVersionUID = 8995312484985926797L;
}