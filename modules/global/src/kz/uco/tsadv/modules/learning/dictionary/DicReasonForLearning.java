package kz.uco.tsadv.modules.learning.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_DIC_REASON_FOR_LEARNING")
@Entity(name = "tsadv$DicReasonForLearning")
public class DicReasonForLearning extends AbstractDictionary {
    private static final long serialVersionUID = -4926632200697283606L;

}