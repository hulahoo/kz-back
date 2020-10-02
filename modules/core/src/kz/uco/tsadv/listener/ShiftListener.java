package kz.uco.tsadv.listener;

import kz.uco.tsadv.IntegrationException;
import kz.uco.tsadv.config.IntegrationConfig;
import kz.uco.tsadv.service.IntegrationService;
import org.springframework.stereotype.Component;
import com.haulmont.cuba.core.listener.AfterDeleteEntityListener;
import java.sql.Connection;
import kz.uco.tsadv.modules.timesheet.model.Shift;
import com.haulmont.cuba.core.listener.AfterInsertEntityListener;
import com.haulmont.cuba.core.listener.AfterUpdateEntityListener;

import javax.inject.Inject;

@Component("tsadv_ShiftListener")
public class ShiftListener implements AfterDeleteEntityListener<Shift>, AfterInsertEntityListener<Shift>, AfterUpdateEntityListener<Shift> {

    @Inject
    private IntegrationConfig integrationConfig;

    @Inject
    private IntegrationService integrationService;

    @Override
    public void onAfterDelete(Shift entity, Connection connection) {
        if (!this.integrationConfig.getIsIntegrationOff()) {
            try {
                this.integrationService.deleteShift(entity, connection);
            } catch (Exception e) {
                if (e.getCause() != null) {
                    throw new IntegrationException(e.getCause().getMessage());
                } else {
                    throw new IntegrationException(e.getMessage());
                }
            }
        }
    }


    @Override
    public void onAfterInsert(Shift entity, Connection connection) {
        if (!this.integrationConfig.getIsIntegrationOff()) {
            try {
                this.integrationService.createShift(entity, connection);
            } catch (Exception e) {
                if (e.getCause() != null) {
                    throw new IntegrationException(e.getCause().getMessage());
                } else {
                    throw new IntegrationException(e.getMessage());
                }
            }
        }
    }


    @Override
    public void onAfterUpdate(Shift entity, Connection connection) {
        if (!this.integrationConfig.getIsIntegrationOff()) {
            try {
                this.integrationService.updateShift(entity, connection);
            } catch (Exception e) {
                if (e.getCause() != null) {
                    throw new IntegrationException(e.getCause().getMessage());
                } else {
                    throw new IntegrationException(e.getMessage());
                }
            }
        }
    }


}