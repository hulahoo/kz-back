package kz.uco.tsadv.datasource;

import com.haulmont.chile.core.model.Instance;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.gui.data.impl.CustomHierarchicalDatasource;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.entity.TimecardHierarchy;
import kz.uco.tsadv.service.TimecardHierarchyService;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


public class TimecardHierarchyDatasource extends CustomHierarchicalDatasource<TimecardHierarchy, UUID> {

    public static final String HIERARCHY_ELEMENT_PARENT_ID = "HIERARCHY_ELEMENT_PARENT_ID";
    public static final String SEARCH_TEXT = "SEARCH_TEXT";
    public static final String HIERARCHY_ID = "HIERARCHY_ID";

    private static final Logger log = LoggerFactory.getLogger(TimecardHierarchyDatasource.class);

    protected CommonService commonService = AppBeans.get(CommonService.class);
    protected Configuration configuration = AppBeans.get(Configuration.NAME);
    protected TimecardHierarchyService timecardHierarchyService = AppBeans.get(TimecardHierarchyService.class);

    @Override
    protected Collection<TimecardHierarchy> getEntities(Map<String, Object> params) {
        return timecardHierarchyService.getTimecardHierarchies(params);
    }

    @Override
    public boolean hasChildren(UUID itemId) {
        final TimecardHierarchy currentItem = getItem(itemId);
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
        Collection<TimecardHierarchy> entities = getEntities(params);
        detachListener(data.values());

        if (entities != null) {
            for (TimecardHierarchy entity : entities) {
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
