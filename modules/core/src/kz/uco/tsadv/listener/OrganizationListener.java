package kz.uco.tsadv.listener;

import com.haulmont.cuba.core.listener.AfterDeleteEntityListener;
import com.haulmont.cuba.core.listener.AfterInsertEntityListener;
import com.haulmont.cuba.core.listener.AfterUpdateEntityListener;
import kz.uco.tsadv.config.IntegrationConfig;
import kz.uco.tsadv.modules.personal.model.OrganizationExt;
import kz.uco.tsadv.service.IntegrationService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.sql.Connection;

@Component("tsadv_OrganizationListener")
public class OrganizationListener implements AfterInsertEntityListener<OrganizationExt>, AfterDeleteEntityListener<OrganizationExt>, AfterUpdateEntityListener<OrganizationExt> {

    @Inject
    private IntegrationService integrationService;

    @Inject
    private IntegrationConfig integrationConfig;

    @Override
    public void onAfterInsert(OrganizationExt entity, Connection connection) {
        if (!integrationConfig.getIsIntegrationOff()) {
            try {
                integrationService.createOrganization(entity, connection);
            } catch (Exception e) {
                //При коммите транзакции в api реплецируемой системы возникла ошибка
                throw new RuntimeException(e);
            }
        }
    }


    @Override
    public void onAfterDelete(OrganizationExt entity, Connection connection) {
    }


    @Override
    public void onAfterUpdate(OrganizationExt entity, Connection connection) {
        if (!integrationConfig.getIsIntegrationOff()) {
            try {
                integrationService.updateOrganization(entity, connection);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


}