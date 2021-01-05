package kz.uco.tsadv.service;

import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.UserSessionSource;
import kz.uco.tsadv.modules.performance.enums.CardStatusEnum;
import kz.uco.tsadv.pojo.kpi.AssignedPerformancePlanListPojo;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service(KpiService.NAME)
public class KpiServiceBean implements KpiService {

    @Inject
    private Persistence persistence;

    @Inject
    private UserSessionSource userSessionSource;

    @Inject
    private Messages messages;

    @Override
    public List<AssignedPerformancePlanListPojo> loadUsersPerformancePlans(Integer page, Integer pageSize) {
        final UUID currentUserId = userSessionSource.currentOrSubstitutedUserId();
        return persistence.callInTransaction(em -> {
            List<Object[]> resultList = em.createNativeQuery(getLoadUsersPerformancePlansQuery(false))
                    .setParameter(1, currentUserId)
                    .setFirstResult((1 - page) * pageSize)
                    .setMaxResults(pageSize)
                    .getResultList();

            return resultList
                    .stream()
                    .map(row -> {
                        byte i = 0;
                        return AssignedPerformancePlanListPojo.AssignedPerformancePlanListPojoBuilder.anAssignedPerformancePlanListPojo()
                                .id((UUID) row[i++])
                                .startDate((Date) row[i++])
                                .endDate((Date) row[i++])
                                .performancePlanName((String) row[i++])
                                .statusName(row[i] != null ? messages.getMessage(Objects.requireNonNull(CardStatusEnum.fromId((String) row[i]))) : null)
                                .build();
                    })
                    .collect(Collectors.toList());
        });
    }

    @Override
    public Long countUsersPerformancePlans() {
        final UUID currentUserId = userSessionSource.currentOrSubstitutedUserId();
        return persistence.callInTransaction(em -> (Long) em.createNativeQuery(getLoadUsersPerformancePlansQuery(true))
                .setParameter(1, currentUserId)
                .getSingleResult());
    }

    protected String getLoadUsersPerformancePlansQuery(boolean isCount) {
        StringBuilder sb = new StringBuilder("" +
                "SELECT app.id         AS id, " +
                "       app.start_date AS start_date, " +
                "       app.end_date   AS end_date, " +
                "       pp.performance_plan_name    AS performance_plan_name, " +
                "       app.status    AS status " +
                "FROM tsadv_assigned_performance_plan app " +
                "         INNER JOIN tsadv_performance_plan pp " +
                "                    ON app.performance_plan_id = pp.id " +
                "                        AND pp.delete_ts IS NULL " +
                "         INNER JOIN base_person_group apg " +
                "                    ON app.assigned_person_id = apg.id " +
                "                        AND apg.delete_ts IS NULL " +
                "         INNER JOIN sec_user apsu " +
                "                    ON apg.id = apsu.person_group_id " +
                "                        AND apsu.delete_ts IS NULL " +
                "                        AND apsu.id = ?1 " +
                "WHERE app.delete_ts IS NULL");
        if (isCount) {
            sb.insert(0, "SELECT count(*) FROM (");
            sb.append(") t");
        }
        return sb.toString();
    }
}