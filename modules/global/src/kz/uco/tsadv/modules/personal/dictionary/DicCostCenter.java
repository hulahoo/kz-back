package kz.uco.tsadv.modules.personal.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_COST_CENTER")
@Entity(name = "tsadv$DicCostCenter")
public class DicCostCenter extends AbstractDictionary {
    private static final long serialVersionUID = 3511403677162283788L;
}