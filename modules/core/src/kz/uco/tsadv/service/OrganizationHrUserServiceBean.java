package kz.uco.tsadv.service;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.View;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.dictionary.DicHrRole;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.model.OrganizationHrUser;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

@Service(OrganizationHrUserService.NAME)
public class OrganizationHrUserServiceBean implements OrganizationHrUserService {

    @Inject
    private Persistence persistence;
    @Inject
    private CommonService commonService;
    @Inject
    private DataManager dataManager;

    /**
     * @see EmployeeService#getHrUsers(UUID, String)
     */
    @Override
    public List<OrganizationHrUser> getHrUsers(@Nonnull UUID organizationGroupId, @Nonnull String roleCode, @Nullable Integer counter) {
        Map<Integer, Object> qParams = new HashMap<>();
        qParams.put(1, organizationGroupId);
        qParams.put(2, roleCode);

        List listId = commonService.emNativeQueryResultList("select e.id from tsadv_organization_hr_user e  " +
                " join tsadv_user_ext_person_group p  " +
                "  on e.user_id = p.user_ext_id " +
                "  and p.delete_ts is null  " +
                " join base_assignment s  " +
                "  on s.person_group_id = p.person_group_id " +
                "  and s.primary_flag is true  " +
                "  and s.delete_ts is null " +
                "  and current_date between s.start_date and s.end_date " +
                " join tsadv_dic_assignment_status ss  " +
                "  on ss.id = s.assignment_status_id " +
                "  and ss.code = 'ACTIVE' " +
                "  and ss.delete_ts is null " +
                " join tsadv_dic_hr_role r  " +
                "  on r.id = e.hr_role_id " +
                "  and r.delete_ts is null  " +
                "  and r.code = ?2 " +
                "where e.delete_ts is null  " +
                "  and e.organization_group_id = ?1 " +
                (counter != null ? "  and e.counter is not null " : "") +
                " and current_date between e.date_from and e.date_to", qParams);

        if (!listId.isEmpty()) {
            return commonService.getEntities(OrganizationHrUser.class,
                    "select e from tsadv$OrganizationHrUser e " +
                            "  where e.id in :list " +
                            "  order by e.id ",
                    ParamsMap.of("list", listId),
                    "organizationHrUser.view");
        } else {
            List<OrganizationGroupExt> organizationGroupExtList = commonService.getEntities(OrganizationGroupExt.class,
                    "select e.parent.organizationGroup " +
                            " from base$HierarchyElementExt e " +
                            " where :date between e.startDate and e.endDate" +
                            " and e.organizationGroup.id = :organizationGroupId " +
                            "   and e.hierarchy.primaryFlag = true",
                    ParamsMap.of("organizationGroupId", organizationGroupId, "date", CommonUtils.getSystemDate()),
                    View.MINIMAL);
            if (!organizationGroupExtList.isEmpty()) {
                return getHrUsers(organizationGroupExtList.get(0).getId(), roleCode, counter);
            } else {
                return new ArrayList<>();
            }
        }
//        return employeeService.getHrUsers(organizationGroupId, roleCode);   !!! no check assignment status !!!
    }

    @Override
    public List<OrganizationHrUser> getHrUsersWithCounter(UUID organizationGroupId, String roleCode, Integer counter) {
        List<OrganizationHrUser> list = getHrUsers(organizationGroupId, roleCode, counter);
        if (!(counter == null || counter == 0) && !list.isEmpty()) {
            List<OrganizationHrUser> newList = new ArrayList<>();
            for (OrganizationHrUser organizationHrUser : list) {
                if (organizationHrUser.getCounter() != null &&
                        organizationHrUser.getCounter().equals(counter)) {
                    newList.add(organizationHrUser);
                }
            }
            if (newList.isEmpty()) {
                OrganizationHrUser hrUser = list.get(0);
                hrUser.setCounter(counter);
                hrUser = dataManager.commit(hrUser);
                newList.add(hrUser);
            }
            list = newList;
        }
        return list;
    }

    @Override
    public void setNextCounter(OrganizationHrUser currentHrUser, String roleCode) {
        List<OrganizationHrUser> hrUsers = getHrUsers(currentHrUser.getOrganizationGroup().getId(), roleCode, null)
                .stream().filter(organizationHrUser -> (organizationHrUser.getCounter() != null)).collect(Collectors.toList());
        if (hrUsers.isEmpty() || hrUsers.size() == 1) {
            return;
        }

        OrganizationHrUser nextHrUser = null;

        for (int i = 0; i < hrUsers.size(); i++) {
            if (hrUsers.get(i).getCounter().equals(1) && hrUsers.get(i).getId().equals(currentHrUser.getId())) {
                currentHrUser = hrUsers.get(i);
                nextHrUser = hrUsers.get((i + 1) % hrUsers.size());
                break;
            }
        }
        if (nextHrUser != null) {
            currentHrUser.setCounter(0);
            nextHrUser.setCounter(1);

            try (Transaction tx = persistence.createTransaction()) {
                EntityManager em = persistence.getEntityManager();
                em.merge(currentHrUser);
                em.merge(nextHrUser);
                tx.commit();
            }
        }
    }

    @Override
    public List<DicHrRole> getDicHrRoles(UUID userId) {
        return persistence.callInTransaction(em ->
                em.createQuery("select e.hrRole from tsadv$OrganizationHrUser e " +
                        "  where e.user.id = :userId " +
                        "        and :date between e.dateFrom and e.dateTo", DicHrRole.class)
                        .setParameter("userId", userId)
                        .setParameter("date", CommonUtils.getSystemDate())
                        .setViewName(View.LOCAL)
                        .getResultList());
    }

}
