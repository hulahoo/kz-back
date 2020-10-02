package kz.uco.tsadv.modules.learning.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_DIC_TRAINING_METHOD")
@Entity(name = "tsadv$DicTrainingMethod")
public class DicTrainingMethod extends AbstractDictionary {
    private static final long serialVersionUID = -2826736281215593403L;

}