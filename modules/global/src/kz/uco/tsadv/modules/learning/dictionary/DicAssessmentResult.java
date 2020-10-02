package kz.uco.tsadv.modules.learning.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_ASSESSMENT_RESULT")
@Entity(name = "tsadv$DicAssessmentResult")
public class DicAssessmentResult extends AbstractDictionary {
    private static final long serialVersionUID = 5490623213446664940L;

}