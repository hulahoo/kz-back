package kz.uco.tsadv.service.portal;

import com.haulmont.cuba.core.entity.SecurityState;

public interface PortalAccessEntityAttributesService {
    String NAME = "tsadv_PortalAccessEntityAttributesService";

    SecurityState entityAttributesSecurityState(String entityName, String entityId);
}