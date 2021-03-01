package kz.uco.tsadv.modules.personal.dictionary;

import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TSADV_DIC_PURPOSE_ABSENCE")
@Entity(name = "tsadv_DicPurposeAbsence")
public class DicPurposeAbsence extends AbstractDictionary {
    private static final long serialVersionUID = 3525090047029626255L;
}