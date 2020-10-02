package kz.uco.tsadv.modules.performance.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_QUESTIONNAIRE_STATUS")
@Entity(name = "tsadv$DicQuestionnaireStatus")
public class DicQuestionnaireStatus extends AbstractDictionary {
    private static final long serialVersionUID = -9219328418113503429L;

}