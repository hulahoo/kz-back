package kz.uco.tsadv.service;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.global.View;
import kz.uco.base.common.BaseCommonUtils;
import kz.uco.base.entity.shared.ElementType;
import kz.uco.base.entity.shared.Hierarchy;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.HierarchyElementExt;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

@Service(HierarchyService.NAME)
public class HierarchyServiceBean implements HierarchyService {

    @Inject
    private Persistence persistence;
    @Inject
    private CommonService commonService;

    @Override
    public List<HierarchyElementExt> loadPositionHierarchyElement(UUID hierarchyId, UUID parentHierarchyId, View view) {
        String stringQuery = String.format(
                "select he from base$HierarchyElementExt he " +
                        "join he.hierarchy h " +
                        "where he.elementType = :elementType " +
                        "and he.parent.id = :parentId " +
                        "and %s",
                hierarchyId != null ?
                        String.format("h.id = '%s'", hierarchyId.toString())
                        : "h.primaryFlag = True");

        return persistence.callInTransaction(em ->
                em.createQuery(stringQuery, HierarchyElementExt.class)
                        .setParameter("elementType", ElementType.POSITION.getId())
                        .setParameter("parentId", parentHierarchyId)
                        .setView(view)
                        .getResultList());
    }

    @Override
    public Long hierarchyElementChildrenCount(UUID parentHierarchyElementId, UUID hierarchyId) {
        return persistence.callInTransaction(em -> {
            Query query = em.createQuery(String.format(
                    "select count(he) " +
                            "from base$HierarchyElementExt he " +
                            "join he.hierarchy h " +
                            "where he.elementType = :elementType " +
                            "and he.parent.id = :parentId " +
                            "and %s",
                    hierarchyId != null ?
                            String.format("h.id = '%s'", hierarchyId.toString())
                            : "h.primaryFlag = True"));
            query.setParameter("elementType", ElementType.POSITION.getId());
            query.setParameter("parentId", parentHierarchyElementId);
            return (Long) query.getSingleResult();
        });
    }

    @Nullable
    @Override
    @SuppressWarnings("ConstantConditions")
    public UUID getHierarchyElementId(UUID positionGroupId, UUID hierarchyId) {
        return persistence.callInTransaction(em -> {
            Query query = em.createQuery(String.format(
                    "select e.id from base$HierarchyElementExt e " +
                            "join e.positionGroup p " +
                            "join e.hierarchy h " +
                            "where e.elementType = :elementType " +
                            "and p.id = :positionGroupId " +
                            "and %s",
                    hierarchyId != null ?
                            String.format("h.id = '%s'", hierarchyId.toString())
                            : "h.primaryFlag = True"));

            query.setParameter("elementType", ElementType.POSITION.getId());
            query.setParameter("positionGroupId", positionGroupId);
            List list = query.getResultList();
            return !list.isEmpty() ? (UUID) list.get(0) : null;
        });
    }

    @Nullable
    @Override
    @SuppressWarnings("ConstantConditions")
    public PositionGroupExt getParentPosition(PositionGroupExt positionGroupExt, String view) {
        return persistence.callInTransaction(em ->
                em.createQuery(" select p.positionGroup from base$HierarchyElementExt e " +
                        " join e.parent p " +
                        "    on :date between p.startDate and p.endDate " +
                        " where :date between e.startDate and e.endDate" +
                        "   and e.elementType = :elementType  " +
                        "   and e.positionGroup.id = :positionGroupId ", PositionGroupExt.class)
                        .setParameter("date", CommonUtils.getSystemDate())
                        .setParameter("positionGroupId", positionGroupExt.getId())
                        .setParameter("elementType", ElementType.POSITION.getId())
                        .setViewName(StringUtils.isEmpty(view) ? View.MINIMAL : view)
                        .getFirstResult());
    }

    @Override
    public boolean isParent(UUID positionGroupId, UUID hierarchyId) {
        UUID id = getHierarchyElementId(positionGroupId, hierarchyId);
        return id != null && hierarchyElementChildrenCount(id, hierarchyId) > 0;
    }

    @Override
    public List<UUID> getOrganizationGroupIdChild(UUID organizationGroupId) {
        return getOrganizationGroupIdChild(Collections.singletonList(organizationGroupId));
    }

    @Override
    public List<UUID> getOrganizationGroupIdChild(List<UUID> organizationGroupIdList) {
        return persistence.callInTransaction(em -> {
            Query query = em.createNativeQuery("WITH RECURSIVE HierarchyPath AS (\n" +
                    "    SELECT child.id,\n" +
                    "           child.organization_group_id,\n" +
                    "           child.group_id,\n" +
                    "           '1'::int as lvl\n" +
                    "    from base_hierarchy_element e\n" +
                    "             join base_hierarchy h\n" +
                    "                  on h.id = e.hierarchy_id\n" +
                    "                      and h.delete_ts is null\n" +
                    "                      and h.primary_flag is true\n" +
                    "             join base_hierarchy_element child\n" +
                    "                  on child.parent_group_id = e.group_id\n" +
                    "                      and child.delete_ts is null\n" +
                    "                      and #systemDate between child.start_date and child.end_date\n" +
                    "                      and child.hierarchy_id = h.id\n" +
                    "    where #organizationGroupIdList ~ e.organization_group_id::text \n" +
                    "      and #systemDate between e.start_date and e.end_date\n" +
                    "      and e.delete_ts is null\n" +
                    "    UNION\n" +
                    "    SELECT e.id,\n" +
                    "           e.organization_group_id,\n" +
                    "           e.group_id,\n" +
                    "           p.lvl + '1' as lvl\n" +
                    "    from base_hierarchy_element e\n" +
                    "             join base_hierarchy h\n" +
                    "                  on h.id = e.hierarchy_id\n" +
                    "                      and h.delete_ts is null\n" +
                    "                      and h.primary_flag is true\n" +
                    "             join HierarchyPath as p\n" +
                    "                  on e.parent_group_id = p.group_id\n" +
                    "    where #systemDate between e.start_date and e.end_date\n" +
                    "      and e.delete_ts is null\n" +
                    ")\n" +
                    "SELECT distinct o.group_id\n" +
                    "FROM HierarchyPath h\n" +
                    "         join base_organization o\n" +
                    "              on o.group_id = h.organization_group_id\n" +
                    "                  and o.delete_ts is null\n" +
                    "                  and #systemDate between o.start_date and o.end_date")
                    .setParameter("systemDate", CommonUtils.getSystemDate())
                    .setParameter("organizationGroupIdList", organizationGroupIdList.stream()
                            .map(UUID::toString)
                            .collect(Collectors.joining("/")));
            @SuppressWarnings("unchecked") List<UUID> list = query.getResultList();
            return list;
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<UUID> getPositionGroupIdChild(UUID positionGroupId, Date date) {
        return persistence.callInTransaction(em ->
                (List<UUID>) em.createNativeQuery("WITH RECURSIVE HierarchyPath AS ( " +
                        "  SELECT e.id, " +
                        "         e.position_group_id, " +
                        "         e.position_group_id::text as pathPos, " +
                        "         e.parent_id, " +
                        "         '1'::int                  as lvl " +
                        "  from base_hierarchy_element e " +
                        "  where e.parent_id is null " +
                        "    and ?2 between e.start_date and e.end_date " +
                        "    and e.delete_ts is null " +
                        "    and e.element_type = '2' " +
                        "  UNION " +
                        "  SELECT e.id, " +
                        "         e.position_group_id, " +
                        "         p.pathPos::text || '*' || e.position_group_id::text as pathPos, " +
                        "         e.parent_id, " +
                        "         p.lvl + " +
                        "         '1'::int                                            as lvl " +
                        "  from HierarchyPath p " +
                        "         join base_hierarchy_element e " +
                        "              on e.parent_id = p.id " +
                        "                and ?2 between e.start_date and e.end_date " +
                        "                and e.delete_ts is null " +
                        "                and e.element_type = '2' " +
                        ") " +
                        "SELECT distinct h.position_group_id " +
                        "FROM HierarchyPath h " +
                        "where h.pathPos like concat('%',concat( ?1 ,'%')) " +
                        "   and h.position_group_id <> ?1 ")
                        .setParameter(1, positionGroupId)
                        .setParameter(2, date)
                        .getResultList());
    }

    /**
     * Возвращает список пользователей руководителей для заданной штатной единицы в заданной иерархии
     */
    @Override
    public List<TsadvUser> findManagerUsers(UUID positionGroupId, UUID hierarchyId) {
        return commonService.getEntities(TsadvUser.class,
                "" +
                        "select e " +
                        "  from tsadv$UserExt e " +
                        " where e.personGroup.id in (" +
                        "          select ae.personGroup.id " +
                        "            from base$AssignmentExt ae " +
                        "           where ae.assignmentStatus.code = 'ACTIVE' " +
                        "             and ae.primaryFlag = true " +
                        "             and :timeMachineDate between ae.startDate and ae.endDate " +
                        "             and ae.positionGroup.id in (" +
                        "                    select par.positionGroup.id " +
                        "                      from base$HierarchyElementExt he " +
                        "                      join he.parent par " +
                        "                     where he.positionGroup.id = :positionGroupId " +
                        "                       and he.hierarchy.id = :hierarchyId" +
                        "                 )" +
                        "       )",
                ParamsMap.of(
                        "positionGroupId", positionGroupId,
                        "hierarchyId", hierarchyId,
                        "timeMachineDate", BaseCommonUtils.getSystemDate()
                ),
                View.LOCAL);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<UUID> getHierarchyException() {
        return persistence.callInTransaction(
                em -> (List<UUID>) em.createNativeQuery("select * from get_hierarchy_exception(#systemDate)")
                        .setParameter("systemDate", CommonUtils.getSystemDate())
                        .getResultList());
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<HierarchyElementExt> getChildHierarchyElement(@Nonnull Hierarchy hierarchy, @Nullable HierarchyElementExt parent) {
        String sqlString = "select * from get_child_hierarchy_element(#hierarchyId, #parentId, #systemDate)";

        Map<UUID, Boolean> idMap = new HashMap<>();

        ((List<Object[]>) persistence.callInTransaction(em ->
                em.createNativeQuery(sqlString)
                        .setParameter("hierarchyId", hierarchy.getId())
                        .setParameter("parentId", parent != null ? parent.getGroup().getId() : null)
                        .setParameter("systemDate", CommonUtils.getSystemDate())
                        .getResultList()))
                .forEach(objects -> idMap.put((UUID) objects[0], Boolean.TRUE.equals(objects[1])));

        List<HierarchyElementExt> hierarchyElementList = getHierarchyList(idMap.keySet());

        hierarchyElementList.forEach(hierarchyElement -> hierarchyElement.setHasChild(idMap.get(hierarchyElement.getId())));

        return hierarchyElementList;
    }

    protected List<HierarchyElementExt> getHierarchyList(Collection<UUID> uuidCollection) {
        return commonService.getEntities(HierarchyElementExt.class,
                "select e from base$HierarchyElementExt e where e.id in :idList",
                ParamsMap.of("idList", uuidCollection),
                "new.hierarchyElement.browse");
    }

    @Override
    public List<HierarchyElementExt> search(@Nonnull Hierarchy hierarchy, @Nonnull String searchText) {
        String sqlString = "select * from search_hierarchy_element(#hierarchyId, #searchText, #systemDate)";

        @SuppressWarnings("unchecked")
        List<Object[]> list = persistence.callInTransaction(em ->
                em.createNativeQuery(sqlString)
                        .setParameter("hierarchyId", hierarchy.getId())
                        .setParameter("searchText", searchText)
                        .setParameter("systemDate", CommonUtils.getSystemDate())
                        .getResultList());

        List<UUID> idParentList = new ArrayList<>();

        for (Object[] row : list) {
            String fullParentId = (String) row[2];      //splitted by '|'
            if (org.apache.commons.lang3.StringUtils.isNotBlank(fullParentId)) {
                for (String parentId : fullParentId.split("\\|")) {
                    idParentList.add(UUID.fromString(parentId));
                }
            }

            String fullChildId = (String) row[3];     //splitted by '|'

            if (org.apache.commons.lang3.StringUtils.isNotBlank(fullChildId)) {
                for (String parentId : fullChildId.split("\\|")) {
                    idParentList.add(UUID.fromString(parentId));
                }
            }
        }

        return getHierarchyList(idParentList);
    }

}