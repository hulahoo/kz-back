package kz.uco.tsadv.listener;

import com.haulmont.cuba.core.listener.AfterDeleteEntityListener;
import com.haulmont.cuba.core.listener.AfterInsertEntityListener;
import com.haulmont.cuba.core.listener.AfterUpdateEntityListener;
import kz.uco.tsadv.config.IntegrationConfig;
import kz.uco.tsadv.modules.personal.model.Job;
import kz.uco.tsadv.service.IntegrationService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.sql.Connection;

@Component("tsadv_JobListener")
public class JobListener implements AfterInsertEntityListener<Job>, AfterDeleteEntityListener<Job>, AfterUpdateEntityListener<Job> {
    @Inject
    private IntegrationService integrationService;

    @Inject
    private IntegrationConfig integrationConfig;

    @Override
    public void onAfterInsert(Job entity, Connection connection) {
        if (!integrationConfig.getIsIntegrationOff()) {
            try {
                integrationService.createJob(entity, connection);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onAfterDelete(Job entity, Connection connection) {
        if (!integrationConfig.getIsIntegrationOff()) {
            try {
                integrationService.deleteJob(entity, connection);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onAfterUpdate(Job entity, Connection connection) {
        if (!integrationConfig.getIsIntegrationOff()) {
            try {
                integrationService.updateJob(entity, connection);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
