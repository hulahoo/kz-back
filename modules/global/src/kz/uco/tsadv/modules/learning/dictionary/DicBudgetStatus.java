package kz.uco.tsadv.modules.learning.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_BUDGET_STATUS")
@Entity(name = "tsadv$DicBudgetStatus")
public class DicBudgetStatus extends AbstractDictionary {
    private static final long serialVersionUID = 7046066865601309645L;

}