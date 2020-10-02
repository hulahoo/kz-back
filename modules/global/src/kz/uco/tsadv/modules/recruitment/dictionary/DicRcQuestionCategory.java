package kz.uco.tsadv.modules.recruitment.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_RC_QUESTION_CATEGORY")
@Entity(name = "tsadv$DicRcQuestionCategory")
public class DicRcQuestionCategory extends AbstractDictionary {
    private static final long serialVersionUID = -2455534072521842660L;

}