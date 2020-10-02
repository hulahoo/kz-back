package kz.uco.tsadv.listener;

import kz.uco.tsadv.IntegrationException;
import kz.uco.tsadv.config.IntegrationConfig;
import kz.uco.tsadv.service.IntegrationService;
import org.springframework.stereotype.Component;
import com.haulmont.cuba.core.listener.AfterDeleteEntityListener;
import java.sql.Connection;
import kz.uco.tsadv.modules.timesheet.model.StandardSchedule;
import com.haulmont.cuba.core.listener.AfterInsertEntityListener;
import com.haulmont.cuba.core.listener.AfterUpdateEntityListener;

import javax.inject.Inject;

@Component("tsadv_StandardScheduleListener")
public class StandardScheduleListener implements AfterDeleteEntityListener<StandardSchedule>, AfterInsertEntityListener<StandardSchedule>, AfterUpdateEntityListener<StandardSchedule> {

    @Inject
    private IntegrationConfig integrationConfig;

    @Inject
    private IntegrationService integrationService;

    @Override
    public void onAfterDelete(StandardSchedule entity, Connection connection) {
        if (!this.integrationConfig.getIsIntegrationOff()) {
            try {
                this.integrationService.deleteStandardSchedule(entity, connection);
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
    public void onAfterInsert(StandardSchedule entity, Connection connection) {
        if (!this.integrationConfig.getIsIntegrationOff()) {
            try {
                this.integrationService.createStandardSchedule(entity, connection);
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
    public void onAfterUpdate(StandardSchedule entity, Connection connection) {
        if (!this.integrationConfig.getIsIntegrationOff()) {
            try {
                this.integrationService.updateStandardSchedule(entity, connection);
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