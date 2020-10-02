package kz.uco.tsadv.modules.recognition.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_DIC_PERSON_AWARD_TYPE")
@Entity(name = "tsadv$DicPersonAwardType")
public class DicPersonAwardType extends AbstractDictionary {
    private static final long serialVersionUID = -8220270722855162796L;

}