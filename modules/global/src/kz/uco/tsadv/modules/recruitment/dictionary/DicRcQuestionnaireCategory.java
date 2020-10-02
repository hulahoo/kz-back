package kz.uco.tsadv.modules.recruitment.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_RC_QUESTIONNAIRE_CATEGORY")
@Entity(name = "tsadv$DicRcQuestionnaireCategory")
public class DicRcQuestionnaireCategory extends AbstractDictionary {
    private static final long serialVersionUID = 121818866277785899L;

}