package kz.uco.tsadv.modules.personal.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_DIC_EXAM_RESULTS")
@Entity(name = "tsadv$DicExamResults")
public class DicExamResults extends AbstractDictionary {
    private static final long serialVersionUID = 6125411918163601157L;

}