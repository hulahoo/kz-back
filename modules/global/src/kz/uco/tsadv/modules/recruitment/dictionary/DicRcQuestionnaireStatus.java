package kz.uco.tsadv.modules.recruitment.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_RC_QUESTIONNAIRE_STATUS")
@Entity(name = "tsadv$DicRcQuestionnaireStatus")
public class DicRcQuestionnaireStatus extends AbstractDictionary {
    private static final long serialVersionUID = 8793362037018261874L;

}