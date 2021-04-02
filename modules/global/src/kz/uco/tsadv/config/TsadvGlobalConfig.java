package kz.uco.tsadv.config;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.type.Factory;
import com.haulmont.cuba.core.config.type.UuidTypeFactory;

import java.util.UUID;

/**
 * @author Alibek Berdaulet
 */
@Source(type = SourceType.DATABASE)
public interface TsadvGlobalConfig extends Config {

    @Property("general.company.id")
    @Factory(factory = UuidTypeFactory.class)
    UUID getGeneralCompanyId();

}
