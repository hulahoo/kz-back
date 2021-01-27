package kz.uco.tsadv.service;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.security.entity.User;
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
    @Inject
    private EmployeeService employeeService;

    @Override
    public List<OrganizationHrUser> getHrUsers(@Nonnull UUID organizationGroupId, @Nonnull String roleCode, @Nullable Integer counter) {
        Map<Integer, Object> qParams = new HashMap<>();
        qParams.put(1, organizationGroupId);
        qParams.put(2, roleCode);

        @SuppressWarnings("rawtypes") List listId = commonService.emNativeQueryResultList(
                "select e.id from tsadv_organization_hr_user e  " +
                        " join sec_user p  " +
                        "  on e.user_id = p.id " +
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

    @Override
    public List<? extends User> getHrUsersForPerson(@Nonnull UUID personGroupId, @Nonnull String roleCode) {
        switch (roleCode) {
            case "MANAGER":
                return getManager(personGroupId);
            default: {
                OrganizationGroupExt organizationGroup = employeeService.getOrganizationGroupByPositionGroupId(personGroupId, View.MINIMAL);
                if (organizationGroup != null)
                    return getHrUsers(organizationGroup.getId(), roleCode, null)
                            .stream()
                            .map(OrganizationHrUser::getUser)
                            .collect(Collectors.toList());
            }
        }
        return new ArrayList<>();
    }

    @Override
    public List<? extends User> getManager(@Nonnull UUID personGroupId) {
        return persistence.callInTransaction(em ->
                em.createNativeQuery("select su.* " +
                        "from base_assignment s " +
                        "join base_hierarchy_element child " +
                        "       on child.position_group_id = s.position_group_id " +
                        "       and #systemDate between child.start_date and child.end_date " +
                        "       and child.delete_ts is null " +
                        "join base_hierarchy_element parent " +
                        "       on parent.GROUP_ID = child.PARENT_GROUP_ID " +
                        "       and #systemDate between parent.start_date and parent.end_date " +
                        "       and parent.delete_ts is null " +
                        "join base_assignment ss " +
                        "       on ss.position_group_id = parent.position_group_id " +
                        "       and #systemDate between ss.start_date and ss.end_date " +
                        "       and ss.primary_flag is true " +
                        "       and ss.delete_ts is null " +
                        "join tsadv_dic_assignment_status ssS " +
                        "       on ss.assignment_status_id = ssS.id " +
                        "       and ssS.code = 'ACTIVE' " +
                        "       and ssS.delete_ts is null " +
                        "join sec_user su " +
                        "       on su.person_group_id = ss.person_group_id " +
                        "       and su.active is true " +
                        "       and su.delete_ts is null " +
                        "where s.person_group_id = #personGroupId " +
                        "       and #systemDate between s.start_date and s.end_date " +
                        "       and s.primary_flag is true " +
                        "       and s.delete_ts is null", User.class)
                        .setParameter("systemDate", CommonUtils.getSystemDate())
                        .setParameter("personGroupId", personGroupId)
                        .getResultList());
    }

}
