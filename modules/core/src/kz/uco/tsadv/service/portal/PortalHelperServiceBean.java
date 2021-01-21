package kz.uco.tsadv.service.portal;

import com.haulmont.cuba.core.global.Metadata;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service(PortalHelperService.NAME)
public class PortalHelperServiceBean implements PortalHelperService {

    @Inject
    protected Metadata metadata;

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getNewEntity(String entityName) {
        return (T) metadata.create(entityName);
    }
}