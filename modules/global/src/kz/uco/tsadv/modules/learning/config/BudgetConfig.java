package kz.uco.tsadv.modules.learning.config;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;

@Source(type = SourceType.DATABASE)
public interface BudgetConfig extends Config {

    @Property("tal.learning.budgetOrgStructureId")
    String getBudgetOrgStructureId();
}
