package kz.uco.tsadv.config;


import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.DefaultBoolean;
import com.haulmont.cuba.core.config.type.Factory;
import com.haulmont.cuba.core.config.type.UuidTypeFactory;

import java.util.UUID;


/**
 * Position structure settings
 * @author Daniil Ivantsov
 */

@Source(type = SourceType.DATABASE)
public interface PositionStructureConfig extends Config {

    /**
     * Stores the UUID of positions hierarchy
     */
    @Property("tal.selfService.positionStructureId")
    @Factory(factory = UuidTypeFactory.class)
    UUID getPositionStructureId();

    /**
     * Indicates whether to show or not show "Open menu" link on position structure Screen (Beauty Tree)
     */
    @Property("tal.selfService.showPositionStructureMenu")
    @DefaultBoolean(false)
    boolean getShowPositionStructureMenu();
}