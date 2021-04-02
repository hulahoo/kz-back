package kz.uco.tsadv.service.portal;

import com.haulmont.cuba.core.entity.BaseGenericIdEntity;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import java.util.List;
import java.util.UUID;

public interface PortalHelperService {
    String NAME = "tsadv_PortalHelperService";

    <T extends BaseGenericIdEntity<K>, K> T newEntity(String entityName);

    <T extends AbstractDictionary> List<T> loadDictionary(String dictionaryName, UUID personGroupId);
}