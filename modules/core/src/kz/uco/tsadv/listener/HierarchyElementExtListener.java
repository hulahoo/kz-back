package kz.uco.tsadv.listener;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.listener.AfterDeleteEntityListener;
import com.haulmont.cuba.core.listener.AfterInsertEntityListener;
import com.haulmont.cuba.core.listener.AfterUpdateEntityListener;
import com.haulmont.cuba.core.listener.BeforeUpdateEntityListener;
import kz.uco.tsadv.IntegrationException;
import kz.uco.tsadv.config.IntegrationConfig;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.model.HierarchyElementExt;
import kz.uco.tsadv.service.IntegrationService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.sql.Connection;
import java.text.SimpleDateFormat;

@Component("tsadv_HierarchyElementExtListener")
public class HierarchyElementExtListener implements AfterDeleteEntityListener<HierarchyElementExt>, AfterInsertEntityListener<HierarchyElementExt>, AfterUpdateEntityListener<HierarchyElementExt>, BeforeUpdateEntityListener<HierarchyElementExt> {

    @Inject
    private IntegrationConfig integrationConfig;

    @Inject
    private IntegrationService integrationService;


    @Override
    public void onAfterDelete(HierarchyElementExt entity, Connection connection) {
        if (!this.integrationConfig.getIsIntegrationOff()) {
            try {
                this.integrationService.deleteHierarchyElement(entity, connection);
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
    public void onAfterInsert(HierarchyElementExt entity, Connection connection) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (!dateFormat.format(entity.getEndDate()).equals(dateFormat.format(CommonUtils.getEndOfTime()))) {
            return;
        }
        if (!this.integrationConfig.getIsIntegrationOff()) {
            try {
                this.integrationService.createHierarchyElement(entity, connection);
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
    public void onAfterUpdate(HierarchyElementExt entity, Connection connection) {
        if (!this.integrationConfig.getIsIntegrationOff()) {
            try {
                this.integrationService.updateHierarchyElement(entity, connection);
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
    public void onBeforeUpdate(HierarchyElementExt entity, EntityManager entityManager) {
        if(entity.getParent() != null && entity.getParent().getGroup() != null){
            entity.setParentGroup(entity.getParent().getGroup());
        }
    }
}