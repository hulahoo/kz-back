package kz.uco.tsadv.web.modules.recruitment.requisition.config;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.DefaultBoolean;

/**
 * Created by Yelaman on 10.08.2018.
 */
@Source(type = SourceType.DATABASE)
public interface CheckRequisitionEndDateConfig extends Config {

    @Property("tal.recruitment.requisition.checkRequisitionEndDateConfig")
    @DefaultBoolean(true)
    boolean getEnabled();
    void setEnabled(boolean enabled);
}
