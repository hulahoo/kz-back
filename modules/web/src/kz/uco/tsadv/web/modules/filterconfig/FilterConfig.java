package kz.uco.tsadv.web.modules.filterconfig;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.DefaultBoolean;

@Source(type = SourceType.DATABASE)
public interface FilterConfig extends Config {

    @Property("tal.hr.organization.enableCubaFilter")
    @DefaultBoolean(false)
    boolean getOrganizationEnableCubaFilter();

    @Property("tal.hr.organization.enableCustomFilter")
    @DefaultBoolean(false)
    boolean getOrganizationEnableCustomFilter();

    @Property("tal.hr.position.enableCubaFilter")
    @DefaultBoolean(false)
    boolean getPositionEnableCubaFilter();

    @Property("tal.hr.position.enableCustomFilter")
    @DefaultBoolean(false)
    boolean getPositionEnableCustomFilter();

    @Property("tal.hr.job.enableCubaFilter")
    @DefaultBoolean(false)
    boolean getJobEnableCubaFilter();

    @Property("tal.hr.job.enableCustomFilter")
    @DefaultBoolean(false)
    boolean getJobEnableCustomFilter();

    @Property("tal.hr.employee.assignment.enableCubaFilter")
    @DefaultBoolean(false)
    boolean getAssignmentEnableCubaFilter();

    @Property("tal.hr.employee.assignment.enableCustomFilter")
    @DefaultBoolean(false)
    boolean getAssignmentEnableCustomFilter();

}
