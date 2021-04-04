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
public interface FrontConfig extends Config {

    /**
     * @return Front-client connection URL.
     */
    @Property("cuba.frontAppUrl")
    @Source(type = SourceType.DATABASE)
    @DefaultString("http://localhost:8080/app-front")
    String getFrontAppUrl();
}
