package kz.uco.tsadv.listener;

import com.haulmont.cuba.core.listener.AfterDeleteEntityListener;
import com.haulmont.cuba.core.listener.AfterInsertEntityListener;
import com.haulmont.cuba.core.listener.AfterUpdateEntityListener;
import kz.uco.tsadv.IntegrationException;
import kz.uco.tsadv.config.IntegrationConfig;
import kz.uco.tsadv.entity.tb.PersonQualification;
import kz.uco.tsadv.service.IntegrationService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.sql.Connection;

/**
 * @author Alibek Berdaulet
 */
@Component("tsadv_PersonQualificationListener")
public class PersonQualificationListener implements
        AfterInsertEntityListener<PersonQualification>,
        AfterUpdateEntityListener<PersonQualification>,
        AfterDeleteEntityListener<PersonQualification> {

    @Inject
    protected IntegrationConfig integrationConfig;

    @Inject
    protected IntegrationService integrationService;

    @Override
    public void onAfterDelete(PersonQualification entity, Connection connection) {
        if (!this.integrationConfig.getIsIntegrationOff()) {
            try {
                this.integrationService.deleteQualification(entity, connection);
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
    public void onAfterInsert(PersonQualification entity, Connection connection) {
        if (!this.integrationConfig.getIsIntegrationOff()) {
            try {
                this.integrationService.createQualification(entity, connection);
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
    public void onAfterUpdate(PersonQualification entity, Connection connection) {
        if (!this.integrationConfig.getIsIntegrationOff()) {
            try {
                this.integrationService.updateQualification(entity, connection);
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
