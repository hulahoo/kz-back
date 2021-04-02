package kz.uco.tsadv.config;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.DefaultString;

/**
 * @author Alibek Berdaulet
 */
@Source(type = SourceType.APP)
public interface AppConfig extends Config {

    /**
     * @return Front-client connection URL. Used for making external links to the application screens and for other purposes.
     */
    @Property("cuba.frontAppUrl")
    @Source(type = SourceType.DATABASE)
    @DefaultString("http://localhost:8080/app-front")
    String getFrontAppUrl();
}
