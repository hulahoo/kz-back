package kz.uco.tsadv.service;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.core.global.ViewRepository;
import kz.uco.base.common.BaseCommonUtils;
import kz.uco.base.entity.shared.ElementType;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.administration.UserExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.HierarchyElementExt;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("all")
@Service(HierarchyService.NAME)
public class HierarchyServiceBean implements HierarchyService {

    @Inject
    private DataManager dataManager;
    @Inject
    private ViewRepository viewRepository;
    @Inject
    private Persistence persistence;
    @Inject
    private CommonService commonService;

    @Override
    public List<HierarchyElementExt> loadPositionHierarchyElement(UUID hierarchyId, UUID parentHierarchyId, View view) {
        return persistence.callInTransaction(em -> {
            Query query = em.createQuery(String.format(
                    "select he from base$HierarchyElementExt he " +
                            "join he.hierarchy h " +
                            "where he.elementType = :elementType " +
                            "and he.parent.id = :parentId " +
                            "and %s",
                    hierarchyId != null ?
                            String.format("h.id = '%s'", hierarchyId.toString())
                            : "h.primaryFlag = True"));

            query.setParameter("elementType", ElementType.POSITION.getId());
            query.setParameter("parentId", parentHierarchyId);
            query.setView(view);
            return query.getResultList();
        });
    }

    @Override
    public Long hierarchyElementChildrenCount(UUID parentHierarchyElementId, UUID hierarchyId) {
        return persistence.callInTransaction(new Transaction.Callable<Long>() {
            @Override
            public Long call(EntityManager em) {
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
            }
        });
    }

    @Override
    public UUID getHierarchyElementId(UUID positionGroupId, UUID hierarchyId) {
        return persistence.callInTransaction(new Transaction.Callable<UUID>() {
            @Override
            public UUID call(EntityManager em) {
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
                return list != null && !list.isEmpty() ? (UUID) list.get(0) : null;
            }
        });
    }

    @Override
    public PositionGroupExt getParentPosition(PositionGroupExt positionGroupExt, String view) {
        return persistence.callInTransaction(em -> {
            return em.createQuery(" select p.positionGroup from base$HierarchyElementExt e " +
                            " join e.parent p " +
                            "    on :date between p.startDate and p.endDate " +
                            " where :date between e.startDate and e.endDate" +
                            "   and e.elementType = :elementType  " +
                            "   and e.positionGroup.id = :positionGroupId "
                    , PositionGroupExt.class)
                    .setParameter("date", CommonUtils.getSystemDate())
                    .setParameter("positionGroupId", positionGroupExt.getId())
                    .setParameter("elementType", ElementType.POSITION.getId())
                    .setViewName(StringUtils.isEmpty(view) ? View.MINIMAL : view)
                    .getFirstResult();
        });
    }

    @Override
    public boolean isParent(UUID positionGroupId, UUID hierarchyId) {
        UUID id = getHierarchyElementId(positionGroupId, hierarchyId);
        return id != null && hierarchyElementChildrenCount(id, hierarchyId) > 0;
    }

    @Override
    public List<UUID> getOrganizationGroupIdChild(UUID organizationGroupId) {
        return persistence.callInTransaction(em -> {
            Query query = em.createNativeQuery(" WITH RECURSIVE HierarchyPath AS ( " +
                    "    SELECT  " +
                    "     e.id, " +
                    "        e.organization_group_id, " +
                    "        e.organization_group_id::text as pathOrg, " +
                    "        e.parent_id, " +
                    "        '1'::int as lvl " +
                    "    from base_hierarchy_element e " +
                    "    join base_hierarchy h " +
                    "     on h.id = e.hierarchy_id " +
                    "     and h.delete_ts is null  " +
                    "     and h.primary_flag is true " +
                    "    where e.parent_id is null  " +
                    "     and ?1 between e.start_date and e.end_date " +
                    "     and e.delete_ts is null  " +
                    "    UNION  " +
                    "    SELECT  " +
                    "     e.id, " +
                    "        e.organization_group_id, " +
                    "        p.pathOrg::text || '*' || e.organization_group_id::text as pathOrg, " +
                    "        e.parent_id, " +
                    "        p.lvl + '1'::int as lvl " +
                    "    from HierarchyPath p " +
                    "    join base_hierarchy_element e " +
                    "     on e.parent_id = p.id " +
                    "     and ?1 between e.start_date and e.end_date " +
                    "     and e.delete_ts is null  " +
                    "    join base_hierarchy h " +
                    "     on h.id = e.hierarchy_id " +
                    "     and h.delete_ts is null  " +
                    "     and h.primary_flag is true " +
                    ") " +
                    "SELECT distinct o.group_id FROM HierarchyPath h " +
                    "join base_organization o  " +
                    " on o.group_id = h.organization_group_id " +
                    " and o.delete_ts is null  " +
                    " and ?1 between o.start_date and o.end_date " +
                    "where h.pathOrg like ?2 " +
                    " and h.organization_group_id != ?3 ")
                    .setParameter(1, CommonUtils.getSystemDate())
                    .setParameter(2, '%' + organizationGroupId.toString() + '%')
                    .setParameter(3, organizationGroupId);
            List list = query.getResultList();
            return list;
        });
    }

    @Override
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
    public List<UserExt> findManagerUsers(
            UUID positionGroupId,
            UUID hierarchyId
    ) {
        return commonService.getEntities(UserExt.class,
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

}