package kz.uco.tsadv.modules.recognition.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;

@Table(name = "TSADV_DIC_QUESTIONNAIRE_TYPE")
@Entity(name = "tsadv$DicQuestionnaireType")
public class DicQuestionnaireType extends AbstractDictionary {
    private static final long serialVersionUID = 7238128117600977589L;

}