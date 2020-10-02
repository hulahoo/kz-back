package kz.uco.tsadv.config;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.DefaultString;
import com.haulmont.cuba.core.config.type.Factory;
import com.haulmont.cuba.core.config.type.UuidTypeFactory;

import java.util.UUID;

@Source(type = SourceType.DATABASE)
public interface OrganizationStructureConfig extends Config {

    /**
     * Stores the UUID of organizations hierarchy
     */
    @Property("tal.hr.orgStructure.orgStructureId")
    @Factory(factory = UuidTypeFactory.class)
    UUID getOrganizationStructureId();

    @Property("tal.hr.orgStructure.fillPayrollOnOrgStructure")
    @DefaultString(value="")
    String getFillPayrollOnOrgStructure();
}
