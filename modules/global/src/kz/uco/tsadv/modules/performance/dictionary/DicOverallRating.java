package kz.uco.tsadv.modules.performance.dictionary;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_OVERALL_RATING")
@Entity(name = "tsadv$DicOverallRating")
public class DicOverallRating extends AbstractDictionary {
    private static final long serialVersionUID = 5149995205588996460L;

}