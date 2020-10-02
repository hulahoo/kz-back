package kz.uco.tsadv.config;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.DefaultBoolean;
import com.haulmont.cuba.core.config.defaults.DefaultInt;

@Source(type = SourceType.DATABASE)
public interface PunishmentDatesConfig extends Config {

    @Property("tal.hr.punishment.enabled")
    @DefaultBoolean(true)
    boolean getEnabled();

    @Property("tal.hr.punishment.period")
    @DefaultInt(6)
    int getMonthsCount();

}
