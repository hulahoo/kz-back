package kz.uco.tsadv.config;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.DefaultBoolean;

@Source(type = SourceType.DATABASE)
public interface AbsenceNotificationConfig extends Config {
    @Property(value = "tal.hr.absence.notification.assets.Email")
    String getAbsenceEmailGroup();

    @Property(value = "tal.hr.absence.notification.assets.EmployeeNotifyEnable")
    @DefaultBoolean(true)
    Boolean getEmployeeNotifyEnable();

    @Property(value = "tal.hr.absence.notification.assets.Lang")
    String getAbsenceLangGroup();
}
