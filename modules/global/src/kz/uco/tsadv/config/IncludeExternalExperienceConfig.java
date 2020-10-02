package kz.uco.tsadv.config;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.DefaultBoolean;

/**
 * @author Alibek Berdaulet
 */
@Source(type = SourceType.DATABASE)
public interface IncludeExternalExperienceConfig extends Config {

    @Property("tal.hr.experience.includeExternalExperience")
    @DefaultBoolean(true)
    Boolean getIncludeExternalExperience();
}
