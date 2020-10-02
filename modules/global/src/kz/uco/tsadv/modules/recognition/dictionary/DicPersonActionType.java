package kz.uco.tsadv.modules.recognition.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_DIC_PERSON_ACTION_TYPE")
@Entity(name = "tsadv$DicPersonActionType")
public class DicPersonActionType extends AbstractDictionary {
    private static final long serialVersionUID = 8077853497047473694L;

}