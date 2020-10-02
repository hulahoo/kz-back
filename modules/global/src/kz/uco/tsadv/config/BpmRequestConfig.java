package kz.uco.tsadv.config;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.DefaultString;

@Source(type = SourceType.DATABASE)
public interface BpmRequestConfig extends Config {

    @Property("tsadv.bpm.position.HrSpecialist")
    @DefaultString(value = "")
    String getPositionHrSpecialist();

    /**
     * used in view TSADV_ACTIVITY_TASK_VIEW
     */
    @Property("tsadv.bpm.transfer.beforeEnding")
    Integer getTransferBeforeEnding();

    /**
     * used in view TSADV_ACTIVITY_TASK_VIEW
     */
    @Property("tsadv.bpm.position.beforeEnding")
    Integer getPositionBeforeEnding();

}
