package kz.uco.tsadv.service.portal;

import com.haulmont.cuba.core.entity.BaseGenericIdEntity;
import kz.uco.tsadv.modules.administration.PortalMenuCustomization;
import kz.uco.tsadv.pojo.PortalMenuPojo;

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

    List<UUID> getCompaniesForLoadDictionary(UUID personGroupId);

    void initPortalMenu(List<PortalMenuPojo> menuList);

    List<PortalMenuCustomization> getPortalMenu(String menuType);

}