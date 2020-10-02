package kz.uco.tsadv.service;


import kz.uco.base.entity.shared.Hierarchy;
import kz.uco.tsadv.entity.TimecardHierarchy;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;

import java.util.*;

public interface TimecardHierarchyService {
    String NAME = "tsadv_TimecardHierarchyService";

    String HIERARCHY_ELEMENT_PARENT_ID = "HIERARCHY_ELEMENT_PARENT_ID";
    String SEARCH_TEXT = "SEARCH_TEXT";
    String HIERARCHY_ID = "HIERARCHY_ID";

    Collection<TimecardHierarchy> getTimecardHierarchies(Map<String, Object> params);

    Collection<TimecardHierarchy> getPaidinternTimecardHierarchies(Map<String, Object> params);

    List<TimecardHierarchy> getAllNestedElements(UUID parentId, Date startDate, Date endDate);

    TimecardHierarchy getHierarchyElement(PositionGroupExt positionGroup, Hierarchy hierarchy, Date date);

    TimecardHierarchy getHierarchyElement(OrganizationGroupExt organizationGroupExt, Hierarchy hierarchy, Date date);
}