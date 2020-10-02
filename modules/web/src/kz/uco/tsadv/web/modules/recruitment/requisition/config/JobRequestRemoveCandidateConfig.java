package kz.uco.tsadv.web.modules.recruitment.requisition.config;


import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.DefaultBoolean;

@Source(type = SourceType.DATABASE)
public interface JobRequestRemoveCandidateConfig extends Config {

    @Property("tal.recruitment.requisition.jobRequest.removeCandidate.enabled")
    @DefaultBoolean(true)
    boolean getEnabled();
    void setEnabled(boolean enable);
}
