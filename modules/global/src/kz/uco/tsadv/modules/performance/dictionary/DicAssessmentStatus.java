package kz.uco.tsadv.modules.performance.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_ASSESSMENT_STATUS")
@Entity(name = "tsadv$DicAssessmentStatus")
public class DicAssessmentStatus extends AbstractDictionary {
    private static final long serialVersionUID = 9140254548370892012L;

}