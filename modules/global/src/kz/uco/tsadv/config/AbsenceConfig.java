package kz.uco.tsadv.config;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.DefaultBoolean;
import com.haulmont.cuba.core.config.type.Factory;
import com.haulmont.cuba.core.config.type.UuidTypeFactory;

import java.util.UUID;

/**
 * @author Alibek Berdaulet
 */
@Source(type = SourceType.DATABASE)
public interface AbsenceConfig extends Config {

    @Property("tal.hr.absence.DefaultCalendar")
    @Factory(factory = UuidTypeFactory.class)
    UUID getDefaultCalendar();

    @Property("tal.hr.absence.request.start.PopupNotificationEnable")
    @DefaultBoolean(value = true)
    Boolean getPopupNotificationEnable();
}
