package kz.uco.tsadv.datasource;

import com.haulmont.chile.core.model.Instance;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.data.impl.CustomHierarchicalDatasource;
import kz.uco.tsadv.modules.personal.model.HierarchyElementExt;
import kz.uco.tsadv.service.HierarchyService;

import java.util.*;

/**
 * @author adilbekov.yernar
 */
public class PositionTreeDatasource extends CustomHierarchicalDatasource<HierarchyElementExt, UUID> {

    public static final String PARENT_HIERARCHY_ELEMENT_ID = "PARENT_HIERARCHY_ELEMENT_ID";
    public static final String HIERARCHY_ID = "HIERARCHY_ID";

    protected HierarchyService hierarchyService = AppBeans.get(HierarchyService.class);

    @Override
    protected Collection<HierarchyElementExt> getEntities(Map<String, Object> params) {
        return hierarchyService.loadPositionHierarchyElement((UUID) params.get(HIERARCHY_ID), (UUID) params.get(PARENT_HIERARCHY_ELEMENT_ID), getView());
    }

    @Override
    public boolean hasChildren(UUID itemId) {
        return hierarchyService.hierarchyElementChildrenCount(itemId, (UUID) savedParameters.get(HIERARCHY_ID)) > 0;
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
        List<UUID> ids = new ArrayList<>();

        Map<String, Object> params = new HashMap<>(savedParameters);
        params.put(PARENT_HIERARCHY_ELEMENT_ID, itemId);

        loadData(params, ids);
        return ids;
    }

    private void loadData(Map<String, Object> params, List<UUID> ids) {
        Collection<HierarchyElementExt> entities = getEntities(params);
        detachListener(data.values());

        if (entities != null) {
            for (HierarchyElementExt entity : entities) {
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
