package kz.uco.tsadv.config;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.DefaultBoolean;
import com.haulmont.cuba.core.config.defaults.DefaultInt;
import com.haulmont.cuba.core.config.defaults.DefaultString;

@Source(type = SourceType.DATABASE)
public interface EmployeeConfig extends Config{

    @Property("tal.hr.employee.generateEmployeeNumber")
    @DefaultBoolean(value = false)
    Boolean getGenerateEmployeeNumber();

    @Property("tal.hr.employee.durationProbationPeriod")
    @DefaultInt(3)
    int getDurarationProbationPeriod();

    @Property("tal.hr.employee.unitProbationPeriod")
    @DefaultInt(40)
    int getUnitProbationPeriod();

    @Property("tal.hr.person.personTypeCodesSeparatedBySemicolonForListener")
    @DefaultString(value = "CANDIDATE")
    String getPersonTypeCodesSeparatedBySemicolonForListener();

    @Property("tal.hr.employee.enableOrderNumberAutogenerationForAssignments")
    @DefaultBoolean(value = false)
    Boolean getEnableOrderNumberAutogenerationForAssignments();

    @Property("tal.hr.employee.enableOrderNumberAutogenerationForDismissals")
    @DefaultBoolean(value = false)
    Boolean getEnableOrderNumberAutogenerationForDismissals();

    @Property("tal.hr.employee.enableOrderNumberAutogenerationForSalaries")
    @DefaultBoolean(value = false)
    Boolean getEnableOrderNumberAutogenerationForSalaries();

}
