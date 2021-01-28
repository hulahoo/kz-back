package kz.uco.tsadv.service;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.UserSessionSource;
import kz.uco.tsadv.modules.performance.dictionary.DicGoalCategory;
import kz.uco.tsadv.modules.performance.enums.CardStatusEnum;
import kz.uco.tsadv.modules.performance.model.AssignedGoal;
import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;
import kz.uco.tsadv.modules.personal.model.Salary;
import kz.uco.tsadv.pojo.PairPojo;
import kz.uco.tsadv.pojo.kpi.AssignedPerformancePlanListPojo;
import org.apache.fop.events.Event;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

@Service(KpiService.NAME)
public class KpiServiceBean implements KpiService {

    @Inject
    protected DataManager dataManager;
    @Inject
    protected DatesService datesService;
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

    @Override
    public double calculationOfGZP(AssignmentGroupExt assignmentGroupExt, Date startDate, Date endDate) {
        List<Salary> salaryList = dataManager.load(Salary.class)
                .query("select e from tsadv$Salary e " +
                        " where e.startDate <= :planEndDate " +
                        " and e.endDate >= :planStartDate " +
                        " and e.assignmentGroup = :assignmentGroup" +
                        " order by e.startDate ")
                .parameter("planStartDate", startDate)
                .parameter("planEndDate", endDate)
                .parameter("assignmentGroup", assignmentGroupExt)
                .view("salary.view")
                .list();
        final double[] gzp = {0.0};
        salaryList.forEach(salary -> {
            gzp[0] += ((double) (datesService.getFullDaysCount(salary == ((Vector) salaryList).firstElement()
                            ? startDate
                            : salary.getStartDate()
                    , salary == ((Vector) salaryList).lastElement()
                            ? endDate
                            : salary.getEndDate())))
                    / 365 * salary.getAmount() * 12;
        });
        return gzp[0];
    }

    @Override
    public List kpiAssignedGoals(UUID appId) {
        List<AssignedGoal> assignedGoals = dataManager.loadList(LoadContext.create(AssignedGoal.class)
                .setQuery(LoadContext.createQuery("" +
                        "select ag " +
                        "            from tsadv$AssignedGoal ag " +
                        "            where ag.assignedPerformancePlan.id = :appId " +
                        "            order by ag.category.order, ag.weight desc")
                        .setParameter("appId", appId))
        .setView("assignedGoal-portal-kpi-create-default"));
        List<PairPojo<String, List<AssignedGoal>>> responseAssignedGoals = assignedGoals.stream()
                .collect(Collectors.groupingBy(AssignedGoal::getCategory))
                .entrySet()
                .stream()
                .sorted(Comparator.comparingInt(c -> c.getKey().getOrder()))
                .map((e) -> new PairPojo<>(e.getKey().getInstanceName(), e.getValue()))
                .collect(Collectors.toList());
        return responseAssignedGoals;
    }
}