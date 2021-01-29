package kz.uco.tsadv.service.portal;

import com.haulmont.cuba.core.entity.BaseGenericIdEntity;

public interface PortalHelperService {
    String NAME = "tsadv_PortalHelperService";

    <T extends BaseGenericIdEntity<K>, K> T newEntity(String entityName);
}