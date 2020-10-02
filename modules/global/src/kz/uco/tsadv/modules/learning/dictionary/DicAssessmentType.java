package kz.uco.tsadv.modules.learning.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.base.entity.abstraction.AbstractDictionary;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_ASSESSMENT_TYPE")
@Entity(name = "tsadv$DicAssessmentType")
public class DicAssessmentType extends AbstractDictionary {
    private static final long serialVersionUID = 7768630836014350149L;

}