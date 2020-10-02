package kz.uco.tsadv.config;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.DefaultString;

@Source(type = SourceType.DATABASE)
public interface PositionConfig extends Config {

    @Property("tal.hr.position.fillPayrollOnPosition")
    @DefaultString(value = "")
    String getFillPayrollOnPosition();

}
