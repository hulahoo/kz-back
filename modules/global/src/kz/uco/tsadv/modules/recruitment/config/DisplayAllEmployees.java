package kz.uco.tsadv.modules.recruitment.config;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.DefaultBoolean;

/**
 * @author Timur Tashmatov
 */
@Source(type = SourceType.DATABASE)
public interface DisplayAllEmployees extends Config {
    @Property("tal.requisition.replaceableperson.DisplayAllEmployees")
    @DefaultBoolean(false)
    boolean getDisplayAllEmployees();
}
