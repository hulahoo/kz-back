package kz.uco.tsadv.modules.recruitment.config;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.defaults.DefaultBoolean;

/**
 * Created by Timur Tashmatov on 04.07.2018.
 */
public interface CalcEndTrialPeriod extends Config {

    @Property("tal.hr.countProbationPeriod")
    @DefaultBoolean(true)
    boolean getCalcCountProbationPeriod();

}
