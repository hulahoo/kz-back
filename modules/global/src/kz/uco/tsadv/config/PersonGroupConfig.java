package kz.uco.tsadv.config;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.DefaultBoolean;


@Source(type = SourceType.DATABASE)
public interface PersonGroupConfig extends Config {

    @Property("tal.hr.personGroup.enableCustomFilter")
    @DefaultBoolean(value = true)
    boolean getEnabledCustomFilter();

    @Property("tal.hr.personGroup.enableCubaFilter")
    @DefaultBoolean(value = false)
    boolean getEnabledCubaFilter();

}
