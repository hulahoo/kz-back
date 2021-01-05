package kz.uco.tsadv.modules.personal.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TSADV_DIC_PREV_JOB_OBLIGATION")
@Entity(name = "tsadv_DicPrevJobObligation")
@NamePattern("%s|langValue,langValue1,langValue2,langValue3,langValue4,langValue5")
public class DicPrevJobObligation extends AbstractDictionary {
    private static final long serialVersionUID = 4767295717893995931L;
}