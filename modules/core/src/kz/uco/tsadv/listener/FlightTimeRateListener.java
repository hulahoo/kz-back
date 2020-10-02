package kz.uco.tsadv.listener;

import com.haulmont.cuba.core.listener.AfterDeleteEntityListener;
import com.haulmont.cuba.core.listener.AfterInsertEntityListener;
import com.haulmont.cuba.core.listener.AfterUpdateEntityListener;
import kz.uco.tsadv.config.IntegrationConfig;
import kz.uco.tsadv.modules.personal.model.FlightTimeRate;
import kz.uco.tsadv.service.IntegrationService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.sql.Connection;

@Component("tsadv_FlightTimeRateListener")
public class FlightTimeRateListener implements AfterInsertEntityListener<FlightTimeRate>, AfterDeleteEntityListener<FlightTimeRate>, AfterUpdateEntityListener<FlightTimeRate> {
    @Inject
    private IntegrationService integrationService;

    @Inject
    private IntegrationConfig integrationConfig;

    @Override
    public void onAfterInsert(FlightTimeRate entity, Connection connection) {
        if (!integrationConfig.getIsIntegrationOff()) {
            try {
                integrationService.createFlightTimeRate(entity, connection);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onAfterDelete(FlightTimeRate entity, Connection connection) {
        if (!integrationConfig.getIsIntegrationOff()) {
            try {
                integrationService.deleteFlightTimeRate(entity, connection);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onAfterUpdate(FlightTimeRate entity, Connection connection) {
        if (!integrationConfig.getIsIntegrationOff()) {
            try {
                integrationService.updateFlightTimeRate(entity, connection);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
