package kz.uco.tsadv.modules.performance.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_GOAL_CATEGORY")
@Entity(name = "tsadv$DicGoalCategory")
public class DicGoalCategory extends AbstractDictionary {
    private static final long serialVersionUID = 79269689039125992L;


}