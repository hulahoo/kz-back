package kz.uco.tsadv.modules.learning.dictionary;

import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TSADV_DIC_COURSE_FORMAT")
@Entity(name = "tsadv$DicCourseFormat")
public class DicCourseFormat extends AbstractDictionary {
    private static final long serialVersionUID = 9046398165071282926L;

    public static final String WEBINAR = "webinar";
    public static final String OFFLINE = "offline";
    public static final String ONLINE = "online";

}