package kz.uco.tsadv.service.portal;

import com.haulmont.cuba.core.entity.BaseGenericIdEntity;
import com.haulmont.cuba.core.global.Metadata;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service(PortalHelperService.NAME)
public class PortalHelperServiceBean implements PortalHelperService {

    @Inject
    protected Metadata metadata;

    @Override
    public <T extends BaseGenericIdEntity<K>, K> T newEntity(String entityName) {
        T entity = (T) metadata.create(entityName);
        entity.setId(null);
        return entity;
    }
}