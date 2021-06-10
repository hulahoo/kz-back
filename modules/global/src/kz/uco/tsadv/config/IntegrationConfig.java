package kz.uco.tsadv.config;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.DefaultBoolean;
import com.haulmont.cuba.core.config.defaults.DefaultString;

@Source(type = SourceType.DATABASE)
public interface IntegrationConfig extends Config {

    @Property("tsadv.integration.off")
    @DefaultBoolean(false)
    boolean getIsIntegrationOff();

    @Property("tsadv.integration.activeDirectory.on")
    @DefaultBoolean(true)
    boolean getIsIntegrationActiveDirectory();


    @Property("tsadv.integration.oracle.absenceRequestUrl")
    @DefaultString("http://10.2.200.101:8290/api/ahruco/absence/request")
    String getAbsenceRequestUrl();


    @Property("tsadv.integration.oracle.basicAuth.login")
    @DefaultString("ahruco")
    String getBasicAuthLogin();


    @Property("tsadv.integration.oracle.basicAuth.password")
    @DefaultString("ahruco")
    String getBasicAuthPassword();


}
