package kz.uco.tsadv.modules.personal.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_DIC_LEARNING_HISTORY_STATUS")
@Entity(name = "tsadv$DicLearningHistoryStatus")
public class DicLearningHistoryStatus extends AbstractDictionary {
    private static final long serialVersionUID = 897789845249387371L;

}