package kz.uco.tsadv.datasource;

import com.haulmont.chile.core.model.Instance;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.data.impl.CustomHierarchicalDatasource;
import kz.uco.tsadv.global.entity.OrganizationTree;
import kz.uco.tsadv.service.OrganizationService;
import org.apache.commons.lang3.BooleanUtils;

import java.util.*;

/**
 * @author adilbekov.yernar
 */
public class OrganizationTreeDatasource extends CustomHierarchicalDatasource<OrganizationTree, UUID> {

    public static final String HIERARCHY_ELEMENT_PARENT_ID = "HIERARCHY_ELEMENT_PARENT_ID";
    public static final String SEARCH_TEXT = "SEARCH_TEXT";
    public static final String HIERARCHY_ID = "HIERARCHY_ID";

    protected OrganizationService organizationService = AppBeans.get(OrganizationService.class);

    @Override
    protected Collection<OrganizationTree> getEntities(Map<String, Object> params) {
        return organizationService.loadOrganizationTree((UUID) params.get(HIERARCHY_ELEMENT_PARENT_ID), (UUID) params.get(HIERARCHY_ID), (String) params.get(SEARCH_TEXT));
    }

    @Override
    public boolean hasChildren(UUID itemId) {
        final OrganizationTree currentItem = getItem(itemId);
        return currentItem != null && BooleanUtils.isTrue(currentItem.getHasChild());
    }

    @Override
    protected void loadData(Map<String, Object> params) {
        loadData(params, null);
    }

    @Override
    public RefreshMode getRefreshMode() {
        return RefreshMode.NEVER;
    }

    @Override
    public Collection<UUID> getChildren(UUID itemId) {
        if (savedParameters.containsKey(SEARCH_TEXT)) {
            return super.getChildren(itemId);
        }

        List<UUID> ids = new ArrayList<>();

        Map<String, Object> params = new HashMap<>(savedParameters);
        params.put(HIERARCHY_ELEMENT_PARENT_ID, itemId);

        loadData(params, ids);
        return ids;
    }

    private void loadData(Map<String, Object> params, List<UUID> ids) {
        Collection<OrganizationTree> entities = getEntities(params);
        detachListener(data.values());

        if (entities != null) {
            for (OrganizationTree entity : entities) {
                UUID id = entity.getId();
                data.put(id, entity);
                attachListener(entity);

                if (ids != null) {
                    ids.add(id);
                }
            }
        }
    }

    @Override
    public void detachListener(Collection instances) {
        super.detachListener(instances);
    }

    public void attachListener() {
        data.values().forEach(o -> attachListener((Instance) o));
    }
}
