package kz.uco.tsadv.service;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.global.*;
import kz.uco.tsadv.config.ExtAppPropertiesConfig;
import kz.uco.tsadv.modules.performance.enums.CardStatusEnum;
import kz.uco.tsadv.modules.performance.model.AssignedGoal;
import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.OrganizationGroupGoalLink;
import kz.uco.tsadv.modules.personal.model.Salary;
import kz.uco.tsadv.pojo.PairPojo;
import kz.uco.tsadv.pojo.kpi.AssignedPerformancePlanListPojo;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service(KpiService.NAME)
public class KpiServiceBean implements KpiService {

    @Inject
    protected DataManager dataManager;
    @Inject
    protected DatesService datesService;
    @Inject
    protected Metadata metadata;
    @Inject
    protected ExtAppPropertiesConfig extAppPropertiesConfig;
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
    public BigDecimal calculationOfGzpWithAbsences(PersonGroupExt personGroupExt, Date startDate, Date endDate) {
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();

            Query query = em.createNativeQuery(
                    "with params as (" +
                            "    select ?1::date as start_date, ?2::date as end_date, ?3::uuid as person_group_id" +
                            "), " +
                            "     absence as ( " +
                            "         select * from tsadv_absence a " +
                            "                           join tsadv_dic_absence_type t on t.id = a.type_id and t.include_calc_gzp = true " +
                            "                           join params on 1 = 1 " +
                            "         where a.delete_ts is null " +
                            "           and a.date_from <= params.end_date " +
                            "           and params.start_date <= a.date_to " +
                            "           and a.person_group_id = params.person_group_id " +
                            "         order by a.date_from " +
                            "     ), " +
                            "     days as ( " +
                            "         select dd as one_day, count(a.*) as absence_count from generate_series " +
                            "                                                                    ( (select greatest(min(a.date_from) , ?1 ) from absence a ), " +
                            "                                                                      (select least(max(a.date_to) , ?2) from absence a ), " +
                            "                                                                      '1 day'::interval) dd " +
                            "                                                                    left join absence a on dd between a.date_from and a.date_to " +
                            "         group by dd " +
                            "     ), " +
                            " my_salary as ( " +
                            "         select s.id, " +
                            "                s.delete_ts, " +
                            "                s.start_date, " +
                            "                s.end_date, " +
                            "                s.assignment_group_id, " +
                            "                s.type_, " +
                            "                case when s.type_  = 'HOURLYRATE' then s.amount * " + extAppPropertiesConfig.getChts() +
                            "  else s.amount end as amount " +
                            "         from tsadv_salary s " +
                            "                  join base_assignment ss " +
                            "                       on ss.group_id = s.assignment_group_id " +
                            "                           and ss.delete_ts is null " +
                            "                           and current_date between ss.start_date and ss.end_date " +
                            "                           and ss.person_group_id = ?3 " +
                            "         where s.delete_ts is null " +
                            "     )," +
                            "     salary as ( " +
                            "         select s.id, " +
                            "                greatest(min(days), s.start_date) as start_date, " +
                            "                least(max(days), s.end_date) as end_date, " +
                            "                1 + least(max(days), s.end_date)::date - greatest(min(days), s.start_date)::date as full_days, " +
                            "                1 + (least(max(days), s.end_date)::date - greatest(min(days), s.start_date)::date) - count(dd.one_day) as salary_days, " +
                            "                ss.person_group_id, " +
                            "                s.assignment_group_id, " +
                            "                s.amount " +
                            "         from generate_series( ?1, ?2, '1 day'::interval) days " +
                            "                  join my_salary s on  days between s.start_date and s.end_date and s.delete_ts is null " +
                            "                  join base_assignment ss " +
                            "                       on ss.group_id = s.assignment_group_id " +
                            "                           and ss.delete_ts is null " +
                            "                           and days between ss.start_date and ss.end_date " +
                            "                           and ss.person_group_id = ?3 " +
                            "                  left join days dd on dd.one_day = days and dd.absence_count > 0 " +
                            "         group by s.id, s.start_date, s.end_date, ss.person_group_id, s.assignment_group_id, s.amount, to_char(days, 'yyyyMM') " +
                            "         order by s.start_date " +
                            "     ) " +
                            "select sum(s.salary_days * 1.0 / ( (date_trunc('month', s.start_date)::date + '1 month'::interval)::date - date_trunc('month', s.start_date)::date ) * s.amount) " +
                            "from salary s;");

            query.setParameter(1, startDate);
            query.setParameter(2, endDate);
            query.setParameter(3, personGroupExt.getId());

            List<Object[]> rows = query.getResultList();
            if (!rows.isEmpty() && ((Vector) rows).firstElement() != null) {
                return new BigDecimal(((Vector) rows).firstElement().toString());
            }
        }
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getMaxBonus(PersonGroupExt personGroupExt, Date startDate, Date endDate) {
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();

            Query query = em.createNativeQuery(
                    "with params as (" +
                            "    select ?1::date as start_date, ?2::date as end_date, ?3::uuid as person_group_id" +
                            "), " +
                            "     absence as ( " +
                            "         select * " +
                            "         from tsadv_absence a " +
                            "                  join tsadv_dic_absence_type t on t.id = a.type_id and t.include_calc_gzp = true " +
                            "                  join params on 1 = 1 " +
                            "         where a.delete_ts is null " +
                            "           and a.date_from <= params.end_date " +
                            "           and params.start_date <= a.date_to " +
                            "           and a.person_group_id = params.person_group_id " +
                            "         order by a.date_from " +
                            "     ), " +
                            "     days as ( " +
                            "         select dd as one_day, count(a.*) as absence_count from generate_series " +
                            "                        ( (select greatest(min(a.date_from) , ?1 ) from absence a ), " +
                            "                        (select least(max(a.date_to) , ?2) from absence a ), " +
                            "                        '1 day'::interval) dd " +
                            "                        left join absence a on dd between a.date_from and a.date_to " +
                            "         group by dd " +
                            "     ), " +
                            "     my_salary as ( " +
                            "         select s.id, " +
                            "                s.delete_ts, " +
                            "                days                               as days, " +
                            "                s.assignment_group_id, " +
                            "                s.type_, " +
                            "                (case when s.type_ = 'HOURLYRATE' then s.amount * " + extAppPropertiesConfig.getChts() +
                            "                             else s.amount end) * " +
                            "                coalesce(g.bonus_percent, 0) / 100 as amount " +
                            "         from generate_series(?1, ?2, '1 day'::interval) days " +
                            "                  join tsadv_salary s on days between s.start_date and s.end_date and s.delete_ts is null " +
                            "                  join base_assignment ss " +
                            "                       on ss.group_id = s.assignment_group_id " +
                            "                           and ss.delete_ts is null " +
                            "                           and days between ss.start_date and ss.end_date " +
                            "                           and ss.person_group_id = ?3 " +
                            "                  join tsadv_grade g " +
                            "                       on g.group_id = ss.grade_group_id " +
                            "                           and days between g.start_date and g.end_date " +
                            "                           and g.delete_ts is null " +
                            "     ), " +
                            "     salary as ( " +
                            "         select s.id, " +
                            "                s.days, " +
                            "                ss.person_group_id, " +
                            "                s.assignment_group_id, " +
                            "                s.amount " +
                            "         from my_salary s " +
                            "                  join base_assignment ss " +
                            "                       on ss.group_id = s.assignment_group_id " +
                            "                           and ss.delete_ts is null " +
                            "                           and days between ss.start_date and ss.end_date " +
                            "                           and ss.person_group_id = ?3 " +
                            "                  left join days dd on dd.one_day = days and dd.absence_count > 0 " +
                            "         group by s.id, s.days, ss.person_group_id, s.assignment_group_id, s.amount, " +
                            "                  to_char(days, 'yyyyMM') " +
                            "         order by s.days " +
                            "     ) " +
                            "select sum(1.0 * s.amount / ((date_trunc('month', s.days)::date + '1 month'::interval)::date - " +
                            "                             date_trunc('month', s.days)::date)) " +
                            "from salary s;");

            query.setParameter(1, startDate);
            query.setParameter(2, endDate);
            query.setParameter(3, personGroupExt.getId());

            List<Object[]> rows = query.getResultList();
            if (!rows.isEmpty() && ((Vector) rows).firstElement() != null) {
                return new BigDecimal(((Vector) rows).firstElement().toString());
            }
        }
        return BigDecimal.ZERO;
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

    @Override
    public List<OrganizationGroupGoalLink> getHierarchyGoals(UUID organizationGroupId) {
        List<OrganizationGroupGoalLink> organizationGroupGoalList = new ArrayList<>();
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();

            Query query = em.createNativeQuery(
                    " select t.lvl, " +
                            "         toggl.id " +
                            " from tsadv_organization_structure_vw t " +
                            "         join tsadv_organization_group_goal_link toggl on t.organization_group_path " +
                            " like '%'|| toggl.organization_group_id || '%' " +
                            " where current_date between t.start_date and t.end_date " +
                            "         and toggl.delete_ts is null " +
                            "         and t.delete_ts is null" +
                            "         and t.organization_group_id = ?1 "
            );

            query.setParameter(1, organizationGroupId);

            List<Object[]> rows = query.getResultList();
            if (!rows.isEmpty()) {
                for (Object[] row : rows) {
                    OrganizationGroupGoalLink organizationGroupGoalLink = metadata.create(OrganizationGroupGoalLink.class);
                    organizationGroupGoalLink.setId((UUID) row[1]);
                    organizationGroupGoalList.add(organizationGroupGoalLink);
                }
                ;
                return organizationGroupGoalList;
            }
        }
        return organizationGroupGoalList;
    }
}