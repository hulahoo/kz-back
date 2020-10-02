package kz.uco.tsadv.global.common;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.DefaultBoolean;

/**
 * Created by Yelaman on 04.06.2018.
 */
@Source(type = SourceType.DATABASE)

public interface EnableDraftStatusInterview extends Config {

    @Property("tal.recruitment.interview.enableDraftStatusInterview")
    @DefaultBoolean(true)
    boolean getEnabled();

    void setEnabled(boolean enable);
}
