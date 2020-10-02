package kz.uco.tsadv.modules.performance.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_QUESTION_TYPE")
@Entity(name = "tsadv$DicQuestionType")
public class DicQuestionType extends AbstractDictionary {
    private static final long serialVersionUID = -2566200580208831504L;

}