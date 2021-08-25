package kz.uco.tsadv.config;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.DefaultString;

@Source(type = SourceType.DATABASE)
public interface AccessConfig extends Config {

    @Property("tsadv.access.personPayslipAccessRoles")
    @DefaultString("Rolename1;Rolename2;RolenameN;")
    String getPersonPayslipAccessRoles();

}
