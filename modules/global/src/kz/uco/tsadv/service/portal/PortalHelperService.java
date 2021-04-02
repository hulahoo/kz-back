package kz.uco.tsadv.service.portal;

import com.haulmont.cuba.core.entity.BaseGenericIdEntity;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import java.util.List;
import java.util.UUID;

public interface PortalHelperService {
    String NAME = "tsadv_PortalHelperService";

    /**
     * @return Instance of entityName with default values without committing.
     * <br>
     * Default values inserted by {@link javax.annotation.PostConstruct}
     */
    <T extends BaseGenericIdEntity<K>, K> T newEntity(String entityName);

    /**
     * @return List of dictionaryName-s filtered by company
     */
    //todo
    <T extends AbstractDictionary> List<T> loadDictionaries(String dictionaryName,
                                                            UUID personGroupId);

}