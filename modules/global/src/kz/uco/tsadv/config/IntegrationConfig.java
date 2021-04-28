package kz.uco.tsadv.config;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.DefaultBoolean;

@Source(type = SourceType.DATABASE)
public interface IntegrationConfig extends Config {

    @Property("tsadv.integration.off")
    @DefaultBoolean(false)
    boolean getIsIntegrationOff();

    @Property("tsadv.integration.activeDirectory.on")
    @DefaultBoolean(true)
    boolean getIsIntegrationActiveDirectory();
}
