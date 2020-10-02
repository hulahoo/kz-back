package kz.uco.tsadv.service;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import kz.uco.base.entity.shared.ElementType;
import kz.uco.base.entity.shared.Hierarchy;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.entity.TimecardHierarchy;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;

@Service(TimecardHierarchyService.NAME)
public class TimecardHierarchyServiceBean implements TimecardHierarchyService {

    @Inject
    protected CommonService commonService;

    @Inject
    protected Metadata metadata;
    @Inject
    protected DataManager dataManager;

    @Override
    public Collection<TimecardHierarchy> getTimecardHierarchies(Map<String, Object> params) {
        String searchText = (String) params.get(SEARCH_TEXT);
        UUID parentId = (UUID) params.get(HIERARCHY_ELEMENT_PARENT_ID);
        Date date = (Date) params.get("date");
        if (date == null) {
            date = CommonUtils.getSystemDate();
        }
        TimecardHierarchy positionHierarchy = (TimecardHierarchy) params.get("positionHierarchy");
        Set<String> allowedTimecardHierarchiesIds = (Set<String>) params.get("allowedTimecardHierarchies");


        ArrayList<TimecardHierarchy> list = new ArrayList<>();
        if (allowedTimecardHierarchiesIds != null && !allowedTimecardHierarchiesIds.isEmpty()) {
            for (String allowedParentId : allowedTimecardHierarchiesIds) {
                List<TimecardHierarchy> elements = getElements(UUID.fromString(allowedParentId), "", date);
                list.addAll(elements);
                if (!elements.isEmpty()) {
                    TimecardHierarchy commonParent = getTimecardHierarchy(elements.get(0).getParent().getId()); // common most highest parent
                    commonParent.setHasChild(true);
                    list.add(commonParent);
                }
            }
            return list;
        }

        if (StringUtils.isNotBlank(searchText) && positionHierarchy != null) {
            list.addAll(getElements(parentId, "", date));
            return list;
        }

        if (StringUtils.isNotBlank(searchText)) {
            TimecardHierarchy timecardHierarchy = getTimecardHierarchy(parentId);
            List<TimecardHierarchy> pathElements = getPathElements(getIdPathFromString(timecardHierarchy.getIdPath()), date);
            list.addAll(pathElements);
        }
        list.addAll(getElements(parentId, "", date));
        return list;
    }

    @Override
    public Collection<TimecardHierarchy> getPaidinternTimecardHierarchies(Map<String, Object> params) {
        String searchText = (String) params.get(SEARCH_TEXT);
        UUID parentId = (UUID) params.get(HIERARCHY_ELEMENT_PARENT_ID);
        Date date = (Date) params.get("date");
        if (date == null) {
            date = CommonUtils.getSystemDate();
        }
        Set<String> allowedTimecardHierarchiesIds = (Set<String>) params.get("allowedTimecardHierarchies");


        ArrayList<TimecardHierarchy> list = new ArrayList<>();
        if (allowedTimecardHierarchiesIds != null && !allowedTimecardHierarchiesIds.isEmpty()) {
            for (String allowedParentId : allowedTimecardHierarchiesIds) {
                List<TimecardHierarchy> elements = getPaidinternElements(UUID.fromString(allowedParentId), "", date);
                list.addAll(elements);
                if (!elements.isEmpty()) {
                    TimecardHierarchy commonParent = getTimecardHierarchy(elements.get(0).getParent().getId()); // common most highest parent
                    commonParent.setHasChild(true);
                    list.add(commonParent);
                }
            }
            return list;
        }

        if (StringUtils.isNotBlank(searchText)) {
            TimecardHierarchy timecardHierarchy = getTimecardHierarchy(parentId);
            List<TimecardHierarchy> pathElements = getPathElements(getIdPathFromString(timecardHierarchy.getIdPath()), date);
            list.addAll(pathElements);
        }
        list.addAll(getPaidinternElements(parentId, "", date));
        return list;
    }

    //returns all highers elements
    protected List<TimecardHierarchy> getPathElements(List<UUID> idPath, Date date) {
        List<TimecardHierarchy> timecardHierarchies = commonService.getEntities(TimecardHierarchy.class,
                "select e from tsadv$TimecardHierarchy e " +
                        "   where e.id in :idPath " +
                        "     and e.startDate <= :date " +
                        "     and e.endDate >= :date",
                ParamsMap.of("idPath", idPath, "date", date), "timecardHierarchy-full-view");

        timecardHierarchies.forEach(e -> e.setHasChild(true)); //true because it's position and he has at least one person already!
        return timecardHierarchies;
    }

    public List<TimecardHierarchy> getAllNestedElements(UUID parentId, Date startDate, Date endDate) {
        List<TimecardHierarchy> timecardHierarchies = commonService.getEntities(TimecardHierarchy.class,
                "select e from tsadv$TimecardHierarchy e where e.idPath like concat('%',concat(:parentId,'%')) and  e.startDate <= :endDate and e.endDate >= :startDate",
                ParamsMap.of("parentId", parentId.toString(), "startDate", startDate, "endDate", endDate), "timecardHierarchy-full-view");
        return timecardHierarchies;
    }

    @Override
    public TimecardHierarchy getHierarchyElement(PositionGroupExt positionGroup, Hierarchy hierarchy, Date date) {
        TimecardHierarchy timecardHierarchy = commonService.emQueryFirstResult(TimecardHierarchy.class,
                "select e from tsadv$TimecardHierarchy e " +
                        "   where e.positionGroup.id = :positionGroupId " +
                        "     and e.startDate <= :date " +
                        "     and e.endDate >= :date " +
                        "   order by e.startDate desc",
                ParamsMap.of("positionGroupId", positionGroup.getId(),
                        "date", date),
                "timecardHierarchy-full-view");
        TimecardHierarchy parent = metadata.create(TimecardHierarchy.class);
        parent.setId(timecardHierarchy.getParent().getId());
        timecardHierarchy.setParent(parent);
        return timecardHierarchy;
    }

    @Override
    public TimecardHierarchy getHierarchyElement(OrganizationGroupExt organizationGroupExt, Hierarchy hierarchy, Date date) {
        TimecardHierarchy timecardHierarchy = commonService.emQueryFirstResult(TimecardHierarchy.class,
                "select e from tsadv$TimecardHierarchy e " +
                        "   where e.organizationGroup.id = :organizationGroupExtId" +
                        "     and e.positionGroup is null " +
                        "     and e.startDate <= :date " +
                        "     and e.endDate >= :date " +
                        "   order by e.startDate desc",
                ParamsMap.of("organizationGroupExtId", organizationGroupExt.getId(),
                        "date", date),
                "timecardHierarchy-full-view");
        TimecardHierarchy parent = metadata.create(TimecardHierarchy.class);
        parent.setId(timecardHierarchy.getParent().getId());
        timecardHierarchy.setParent(parent);
        return timecardHierarchy;
    }

    protected List<TimecardHierarchy> getTimecardHierarchiesForNonPerson(UUID parentId, Date date) {
        List<TimecardHierarchy> timecardHierarchiesForNonPerson = commonService.getEntities(TimecardHierarchy.class,
                "select e from tsadv$TimecardHierarchy e " +
                        "   where e.parent" + (parentId != null ? ".id = :parentId" : " IS NULL ") +
                        "     and e.startDate <= :date " +
                        "     and e.endDate >= :date",
                ParamsMap.of("parentId", parentId,
                        "date", date),
                "timecardHierarchy-full-view");

        List<TimecardHierarchy> commonChildren = commonService.getEntities(TimecardHierarchy.class,
                "select e from tsadv$TimecardHierarchy e where e.parent in :parents and e.startDate <= :date and e.endDate >= :date",
                ParamsMap.of("parents",
                        new ArrayList<>(timecardHierarchiesForNonPerson), "date", date), "timecardHierarchy-only-parent-view");

        boolean hasChild;
        for (TimecardHierarchy timecardHierarchy : timecardHierarchiesForNonPerson) {
            hasChild = commonChildren.stream().anyMatch(c -> c.getParent().equals(timecardHierarchy))
                    || getAssignments(timecardHierarchy, date).size() > 0;//todo getAssignments не оптимальная выборка. можно сразу select count...
            timecardHierarchy.setHasChild(hasChild);
        }

        return timecardHierarchiesForNonPerson;
    }

    protected List<TimecardHierarchy> getLvlOneNestedOrgElements(UUID parentId, Date date) {
        return commonService.getEntities(TimecardHierarchy.class, "select e from tsadv$TimecardHierarchy e where e.parent" +
                        (parentId != null ? ".id = :parentId" : " IS NULL ") + " and e.startDate <= :date and e.endDate >= :date and e.elementType = 1",
                ParamsMap.of("parentId", parentId, "date", date), "timecardHierarchy-full-view");
    }

    protected List<TimecardHierarchy> getElements(UUID parentId, String searchText, Date date) {
        ArrayList<TimecardHierarchy> oneNestedLvlTimecardHierarchyList = new ArrayList<>();

        List<TimecardHierarchy> timecardHierarchiesForNonPerson = getTimecardHierarchiesForNonPerson(parentId, date);

        List<TimecardHierarchy> timecardHierarchiesForPerson = getTimecardHierarchiesForPerson(parentId, date);

        oneNestedLvlTimecardHierarchyList.addAll(timecardHierarchiesForNonPerson);

        oneNestedLvlTimecardHierarchyList.addAll(timecardHierarchiesForPerson);

        return oneNestedLvlTimecardHierarchyList;
    }

    protected ArrayList<TimecardHierarchy> getTimecardHierarchiesForPerson(UUID parentId, Date date) {

        if (parentId == null) return new ArrayList<>();

        ArrayList<TimecardHierarchy> timecardHierarchiesForPerson = new ArrayList<>();
        TimecardHierarchy parentTimecardHierarchy = metadata.create(TimecardHierarchy.class);
        parentTimecardHierarchy.setId(parentId);
        parentTimecardHierarchy = dataManager.reload(parentTimecardHierarchy, "timecardHierarchy-full-view");
        List<AssignmentExt> assignments = getAssignments(parentTimecardHierarchy, date);
        for (AssignmentExt a : assignments) {
            TimecardHierarchy timecardHierarchyForPerson = new TimecardHierarchy();
            timecardHierarchyForPerson.setId(a.getPersonGroup().getId());
            timecardHierarchyForPerson.setPersonGroup(a.getPersonGroup());
            timecardHierarchyForPerson.setElementType(ElementType.PERSON);
            timecardHierarchyForPerson.setParent(parentTimecardHierarchy);
            timecardHierarchyForPerson.setHasChild(false);
            timecardHierarchiesForPerson.add(timecardHierarchyForPerson);
        }
        return timecardHierarchiesForPerson;
    }

    protected List<TimecardHierarchy> getPaidinternElements(UUID parentId, String searchText, Date date) {
        ArrayList<TimecardHierarchy> list = new ArrayList<>();

        List<TimecardHierarchy> timecardHierarchies = getLvlOneNestedOrgElements(parentId, date);

        if (parentId != null) {
            TimecardHierarchy timecardHierarchy = metadata.create(TimecardHierarchy.class);
            timecardHierarchy.setId(parentId);
            TimecardHierarchy reload = dataManager.reload(timecardHierarchy, "timecardHierarchy-full-view");
            List<AssignmentExt> assignments = getPaidinternAssignments(reload.getOrganizationGroup().getId(), date);
            assignments.forEach(a -> {
                TimecardHierarchy dtoWithAssignment = new TimecardHierarchy();
                dtoWithAssignment.setId(a.getPersonGroup().getId());
                dtoWithAssignment.setPersonGroup(a.getPersonGroup());
                dtoWithAssignment.setElementType(ElementType.PERSON);

                TimecardHierarchy timecardHierarchyDto = new TimecardHierarchy();
                timecardHierarchyDto.setId(parentId);
                dtoWithAssignment.setParent(timecardHierarchyDto);
                dtoWithAssignment.setHasChild(false);
                list.add(dtoWithAssignment);
            });

        }

        List<TimecardHierarchy> commonChildren = commonService.getEntities(TimecardHierarchy.class,
                "select e from tsadv$TimecardHierarchy e " +
                        "   where e.parent in :parents " +
                        "     and e.startDate <= :date " +
                        "     and e.endDate >= :date " +
                        "     and e.elementType = 1",
                ParamsMap.of("parents",
                        new ArrayList<>(timecardHierarchies), "date", date), "timecardHierarchy-full-view");

        timecardHierarchies.forEach(th -> {
            boolean presentChild = commonChildren.stream().anyMatch(c -> c.getParent().equals(th));
            th.setHasChild(presentChild);

            if (th.getPositionGroup() != null && !presentChild) {
                List<AssignmentExt> assignments = getPaidinternAssignments(th.getOrganizationGroup().getId(), date);
                th.setHasChild(!commonChildren.isEmpty() || assignments.size() > 0);
            }

            TimecardHierarchy parent = new TimecardHierarchy();
            parent.setId(th.getParent() != null ? th.getParent().getId() : null);

            list.add(th);
        });
        return list;
    }

    protected List<AssignmentExt> getPaidinternAssignments(UUID organizationGroupId, Date date) {
        return commonService.getEntities(AssignmentExt.class,
                "select e from base$AssignmentExt e " +
                        "   join base$PersonExt p" +
                        " where e.organizationGroup.id = :organizationGroupId " +
                        "  and  e.startDate <= :date and e.endDate >= :date " +
                        "  and  p.startDate <= :date and p.endDate >= :date " +
                        "   and e.assignmentStatus.code <> 'TERMINATED' " +
                        "   and p.group.id = e.personGroup.id " +
                        "   and p.type.code = 'PAIDINTERN'",
                ParamsMap.of("organizationGroupId", organizationGroupId, "date", date), "assignment.person");
    }


    protected List<AssignmentExt> getAssignments(TimecardHierarchy timecardHierarchy, Date date) {
        if (timecardHierarchy.getPositionGroup() != null) {
            return commonService.getEntities(AssignmentExt.class,
                    "select e from base$AssignmentExt e " +
                            "   where e.positionGroup.id = :positionGroupId " +
                            "     and e.startDate <= :date and e.endDate >= :date " +
                            "     and e.assignmentStatus.code <> 'TERMINATED' ",
                    ParamsMap.of("positionGroupId", timecardHierarchy.getPositionGroup().getId(),
                            "date", date), "assignment.person");
        }
        return new ArrayList<>();
    }

    protected TimecardHierarchy getTimecardHierarchy(UUID id) {
        TimecardHierarchy timecardHierarchy = metadata.create(TimecardHierarchy.class);
        timecardHierarchy.setId(id);
        timecardHierarchy = dataManager.reload(timecardHierarchy, "timecardHierarchy-full-view");
        return timecardHierarchy;
    }


    protected List<UUID> getIdPathFromString(String s) {
        String[] split = s.split("\\*");
        List<UUID> dtoIdPath = new ArrayList<>();
        for (String str : split) {
            dtoIdPath.add(UUID.fromString(str));
        }
        return dtoIdPath;
    }

}