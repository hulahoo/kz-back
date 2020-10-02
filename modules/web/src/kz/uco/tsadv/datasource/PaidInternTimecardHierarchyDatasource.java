package kz.uco.tsadv.datasource;

import kz.uco.tsadv.entity.TimecardHierarchy;

import java.util.Collection;
import java.util.Map;

public class PaidInternTimecardHierarchyDatasource extends TimecardHierarchyDatasource {
    @Override
    protected Collection<TimecardHierarchy> getEntities(Map<String, Object> params) {
        return timecardHierarchyService.getPaidinternTimecardHierarchies(params);
    }
}
