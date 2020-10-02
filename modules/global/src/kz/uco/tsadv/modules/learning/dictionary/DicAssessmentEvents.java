package kz.uco.tsadv.modules.learning.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_ASSESSMENT_EVENTS")
@Entity(name = "tsadv$DicAssessmentEvents")
public class DicAssessmentEvents extends AbstractDictionary {
    private static final long serialVersionUID = -8899814518442609589L;

}