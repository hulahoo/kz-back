package kz.uco.tsadv.app;


import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.Default;

@Source(type = SourceType.DATABASE)
public interface LinkedinApiConfig extends Config {

    @Property("tal.linkedin.clientId")
    @Default("8694k683xz2mbu")
    String getLinkedinClientId();

    @Property("tal.linkedin.clientSecret")
    @Default("dNEMpmX55RfBd5Ii")
    String getLinkedinClientSecret();

    @Property("tal.linkedin.scope")
    @Default("r_basicprofile r_emailaddress")
    String getLinkedinScope();

    @Property("tal.linkedin.callbackUrl")
    @Default("http://apps.uco.kz:8085/tal/dispatch/linkedin")
    String getLinkedinCallbackUrl();

}
