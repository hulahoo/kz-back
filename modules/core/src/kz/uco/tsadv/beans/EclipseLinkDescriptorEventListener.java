package kz.uco.tsadv.beans;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.entity.SoftDelete;
import com.haulmont.cuba.core.entity.Updatable;
import kz.uco.base.common.BaseCommonUtils;
import kz.uco.base.entity.abstraction.AbstractTimeBasedEntity;
import org.eclipse.persistence.descriptors.DescriptorEvent;

public class EclipseLinkDescriptorEventListener extends com.haulmont.cuba.core.sys.persistence.EclipseLinkDescriptorEventListener {
    @Override
    public void prePersist(DescriptorEvent event) {
        Entity entity = (Entity) event.getObject();
        if (entity instanceof AbstractTimeBasedEntity) {
            AbstractTimeBasedEntity abstractTimeBasedEntity = (AbstractTimeBasedEntity) entity;
            if (abstractTimeBasedEntity.getEndDate() == null && abstractTimeBasedEntity.getEndDate().before(BaseCommonUtils.getSystemDate())) {
                if (!justDeleted((SoftDelete) entity)) {
                    Updatable updatable = (Updatable) event.getObject();
                    updatable.setUpdatedBy(auditInfoProvider.getCurrentUserLogin());
                    updatable.setUpdateTs(timeSource.currentTimestamp());
                }
                return;
            }
        }
        super.prePersist(event);
    }
}
