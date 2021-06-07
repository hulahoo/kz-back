package kz.uco.tsadv.config;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.*;

@Source(type = SourceType.DATABASE)
public interface ExtAppPropertiesConfig extends Config {
    @Property("tsadv.popup.minimizedView.maxLength")
    @DefaultInt(10)
    int getPopupMinimizedViewMaxLength();

    @Property("tsadv.popup.content.width")
    @DefaultString("200px")
    String getPopupContentWidth();

    @Property("tsadv.popup.content.height")
    @DefaultString("150px")
    String getPopupContentHeight();

    @Property("tsadv.popup.hideOnMouseOut")
    @DefaultBoolean(false)
    boolean getPopupHideOnMouseOut();

    @Property("tsadv.kpi.individualScore")
    @DefaultInteger(20)
    Integer getIndividualScore();

    @Property("tsadv.kpi.cascadeInPerson")
    @DefaultBoolean(true)
    Boolean getCascadeInPerson();

    @Property("tsadv.kpi.includeAbsence")
    @DefaultBoolean(true)
    Boolean getIncludeAbsence();

    @Property("tsadv.kpi.chts")
    @DefaultDouble(165.15)
    Double getChts();

    @Property("tsadv.course.defaultLogo")
    @DefaultString("")
    String getDefaultLogo();
}
