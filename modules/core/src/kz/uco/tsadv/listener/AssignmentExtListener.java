package kz.uco.tsadv.listener;

import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.listener.AfterDeleteEntityListener;
import com.haulmont.cuba.core.listener.AfterUpdateEntityListener;
import kz.uco.tsadv.IntegrationException;
import kz.uco.tsadv.config.IntegrationConfig;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.service.BpmService;
import kz.uco.tsadv.service.IntegrationService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.sql.Connection;

@Component("tsadv_AssignmentExtListener")
public class AssignmentExtListener implements
        AfterUpdateEntityListener<AssignmentExt>,
        AfterDeleteEntityListener<AssignmentExt> {

    @Inject
    protected IntegrationConfig integrationConfig;
    @Inject
    protected Persistence persistence;
    @Inject
    protected IntegrationService integrationService;

    @Override
    public void onAfterDelete(AssignmentExt entity, Connection connection) {
        if (!this.integrationConfig.getIsIntegrationOff()) {
            try {
                this.integrationService.deleteAssignment(entity, connection);
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
    public void onAfterUpdate(AssignmentExt entity, Connection connection) {
        if (persistence.getTools().isDirty(entity, "organizationGroup"))
            AppBeans.get(BpmService.class).notifyManagersAboutAccessInf(entity.getStartDate(),
                    entity.getPersonGroup(),
                    entity.getOrganizationGroup(),
                    (OrganizationGroupExt) persistence.getTools().getOldValue(entity, "organizationGroup"),
                    entity.getPositionGroup(),
                    (PositionGroupExt) persistence.getTools().getOldValue(entity, "positionGroup"),
                    false);
        if (!this.integrationConfig.getIsIntegrationOff()) {
            try {
                this.integrationService.integrateAssignment(entity, connection);
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