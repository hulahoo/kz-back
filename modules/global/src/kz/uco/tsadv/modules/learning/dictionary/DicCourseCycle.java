package kz.uco.tsadv.modules.learning.dictionary;

import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TSADV_DIC_COURSE_CYCLE")
@Entity(name = "tsadv$DicCourseCycle")
public class DicCourseCycle extends AbstractDictionary {
    private static final long serialVersionUID = 5564217173397773485L;

}