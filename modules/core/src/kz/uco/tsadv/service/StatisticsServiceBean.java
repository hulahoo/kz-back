package kz.uco.tsadv.service;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.sys.AppContext;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.global.entity.AbsenceChartEntity;
import kz.uco.tsadv.global.entity.AssignmentAbsenceChartEntity;
import kz.uco.tsadv.global.entity.CompetenceChartEntity;
import kz.uco.tsadv.global.entity.SalaryChartEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.*;


@Service(StatisticsService.NAME)
public class StatisticsServiceBean implements StatisticsService {

    private Logger log = LoggerFactory.getLogger(StatisticsServiceBean.class);

    @Inject
    private Persistence persistence;

    protected int languageIndex = 0;

    public List<CompetenceChartEntity> getCompetenceChart(UUID assignmentGroupId, String language) {
        languageIndex = languageIndex(language);
        List<CompetenceChartEntity> result = new ArrayList<>();
        String query = "WITH assignment AS\n" +
                "(SELECT\n" +
                "   a.position_group_id,\n" +
                "   a.person_group_id\n" +
                " FROM BASE_ASSIGNMENT a\n" +
                " WHERE a.group_id = ?1)\n" +
                "  ,\n" +
                "    competence AS (\n" +
                "    SELECT cep.competence_group_id\n" +
                "    FROM assignment a LEFT JOIN tsadv_competence_element cep\n" +
                "        ON (cep.position_group_id = a.position_group_id AND cep.delete_ts IS NULL)\n" +
                "    UNION\n" +
                "    SELECT cea.competence_group_id\n" +
                "    FROM assignment a LEFT JOIN tsadv_competence_element cea\n" +
                "        ON (cea.person_group_id = a.person_group_id AND cea.delete_ts IS NULL)\n" +
                "  )\n" +
                "SELECT\n" +
                "  c.competence_name_lang" + languageIndex + ",\n" +
                "  s.scale_name,\n" +
                "  coalesce(slp.level_number, 0),\n" +
                "  slp.level_name_lang" + languageIndex + ",\n" +
                "  slp.level_description_lang" + languageIndex + ",\n" +
                "  coalesce(sla.level_number, 0),\n" +
                "  sla.level_name_lang" + languageIndex +",\n" +
                "  sla.level_description_lang" + languageIndex + "\n" +
                "FROM competence cc CROSS JOIN assignment a\n" +
                "  LEFT JOIN tsadv_competence_element cep\n" +
                "    ON (cep.position_group_id = a.position_group_id AND cep.competence_group_id = cc.competence_group_id AND\n" +
                "        cep.delete_ts IS NULL)\n" +
                "  LEFT JOIN tsadv_competence_element cea\n" +
                "    ON (cea.person_group_id = a.person_group_id AND cea.competence_group_id = cc.competence_group_id AND\n" +
                "        cea.delete_ts IS NULL)\n" +
                "  LEFT JOIN tsadv_competence c ON (c.group_id = cc.competence_group_id)\n" +
                "  LEFT JOIN tsadv_scale s ON (s.id = c.scale_id)\n" +
                "  LEFT JOIN tsadv_scale_level slp ON (slp.id = cep.scale_level_id)\n" +
                "  LEFT JOIN tsadv_scale_level sla ON (sla.id = cea.scale_level_id)" +
                " where cc.competence_group_id  is not null ";
        try (Transaction transaction = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            List<Object[]> resultList = em.createNativeQuery(query)
                    .setParameter(1, assignmentGroupId)
                    .getResultList();

            for (Object[] row : resultList) {
                CompetenceChartEntity entity = new CompetenceChartEntity();
                entity.setCompetenceName((String) row[0]);
                entity.setScaleName((String) row[1]);
                entity.setPositionScaleLevel(((Integer) row[2]).longValue());
                entity.setPositionScaleLevelName((String) row[3]);
                entity.setPositionScaleLevelDesc((String) row[4]);
                entity.setAssignmentScaleLevel(((Integer) row[5]).longValue());
                entity.setAssignmentScaleLevelName((String) row[6]);
                entity.setAssignmentScaleLevelDesc((String) row[7]);

                result.add(entity);
            }
        }

        return result;

    }

    public List<SalaryChartEntity> getSalaryChart(Double salary) {
        List<SalaryChartEntity> result = new ArrayList<>();
        String query = "with list as (select min(amount) as salary, 'Min' as description from tsadv_salary s\n" +
                "union all\n" +
                "select avg(amount) as salary, 'Mid' as description from tsadv_salary s\n" +
                "union all\n" +
                "select max(amount) as salary, 'Max' as description from tsadv_salary s\n" +
                "union all\n" +
                "select ?1  as salary, '" + AppBeans.get(Messages.class).getMessage("kz.uco.tsadv", "salaryChart.current") + "' as description)" +
                " select * from list where salary <> 0" +
                " order by salary ";

        try (Transaction transaction = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            List<Object[]> resultList = em.createNativeQuery(query)
                    .setParameter(1, salary == null ? BigDecimal.valueOf(0) : (BigDecimal.valueOf(salary)))
                    .getResultList();

            for (Object[] row : resultList) {
                SalaryChartEntity entity = new SalaryChartEntity();
                entity.setSalary(((BigDecimal) row[0]).doubleValue());
                entity.setSalaryDescription((String) row[1]);
                entity.setColor(entity.getSalaryDescription().equals(AppBeans.get(Messages.class).getMessage("kz.uco.tsadv", "salaryChart.current")) ? "BLUE" : "RED");
                result.add(entity);
            }
        }

        return result;
    }

    public List<AssignmentAbsenceChartEntity> getAssignmentAbsenceChart(UUID managerPositionGroupId, UUID assignmentGroupId, Date dateFrom, Date dateTo, UUID absenceTypeId) {
        List<AssignmentAbsenceChartEntity> result = new ArrayList<>();

//        log.info(CommonUtils.getSystemDate() + "=systemDate");

        String assignmentsQuery = "select ass.group_id as assignment_group_id," +
                "  p.last_name || ' ' || p.first_name || ' ' || coalesce(p.middle_name, '') as person_full_name\n" +
                "from BASE_ASSIGNMENT ass\n" +
                "join BASE_PERSON p on (p.group_id = ass.person_group_id)\n" +
                "join tsadv_position_structure ps on (ps.position_group_id = ass.position_group_id)" +
                "where (ps.position_group_path like '%' || ?1 || '%')\n" +
                "and (exists (\n" +
                "select 1 \n" +
                "from tsadv_ord_assignment oa, tsadv_absence a \n" +
                "where oa.assignment_group_id = ass.group_id\n" +
                "and a.ord_assignment_id = oa.id\n" +
                "and a.date_from + 30 >= ?2 and a.date_to - 30 <= ?3 " +
                ")\n" +
                "or exists (\n" +
                "select 1\n" +
                "from tsadv_absence_request ar, tsadv_dic_request_status drs\n" +
                "where ar.assignment_group_id = ass.group_id\n" +
                "and drs.id = ar.request_status_id\n" +
                "and drs.code in ('APPROVING', 'APPROVED')\n" +
                "and ar.date_from + 30 >= ?2 and ar.date_to - 30 <= ?3 " +
                ")) " +
                " and ?4 between ass.start_date and ass.end_date " +
                " and ?4 between p.start_date and p.end_date " +
                " and ?4 between ps.start_date and ps.end_date " +
                " and ?4 between ps.pos_start_date and ps.pos_end_date ";

        String absencesQuery = "select a.date_from, a.date_to + time '23:59', a.absence_days, dat.lang_value1 as absence_type, a.type_id \n" +
                "from tsadv_ord_assignment oa, tsadv_absence a, tsadv_dic_absence_type dat \n" +
                "where oa.assignment_group_id = ?1\n" +
                "and a.ord_assignment_id = oa.id\n" +
                "and dat.id = a.type_id\n" +
                "and a.date_from + 30 >= ?2 and a.date_to - 30 <= ?3\n" +
                "union\n" +
                "select ar.date_from, ar.date_to + time '23:59', ar.absence_days, dat.lang_value1 as absence_type, ar.type_id\n" +
                "from tsadv_absence_request ar, tsadv_dic_absence_type dat, tsadv_dic_request_status drs \n" +
                "where ar.assignment_group_id = ?1\n" +
                "and dat.id = ar.type_id\n" +
                "and drs.id = ar.request_status_id\n" +
                "and drs.code in ('APPROVING', 'APPROVED')\n" +
                "and ar.date_from + 30 >= ?2 and ar.date_to - 30 <= ?3\n";

        try (Transaction transaction = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            List<Object[]> assignmentsList = em.createNativeQuery(assignmentsQuery)
                    .setParameter(1, managerPositionGroupId != null ? managerPositionGroupId.toString() : "")
                    .setParameter(2, dateFrom)
                    .setParameter(3, dateTo)
                    .setParameter(4, CommonUtils.getSystemDate())
                    .getResultList();

            for (Object[] assRow : assignmentsList) {
                AssignmentAbsenceChartEntity assignmentAbsenceChartEntity = new AssignmentAbsenceChartEntity();
                assignmentAbsenceChartEntity.setAssignmentGroupId((UUID) assRow[0]);
                assignmentAbsenceChartEntity.setPersonFullName((String) assRow[1]);

                List<Object[]> absencesList = em.createNativeQuery(absencesQuery)
                        .setParameter(1, assignmentAbsenceChartEntity.getAssignmentGroupId())
                        .setParameter(2, dateFrom)
                        .setParameter(3, dateTo)
                        .getResultList();

                List<AbsenceChartEntity> absences = new ArrayList<>();
                int i = 0;
                for (Object[] absRow : absencesList) {
                    AbsenceChartEntity absenceChartEntity = new AbsenceChartEntity();
                    absenceChartEntity.setDateFrom((Date) absRow[0]);
                    absenceChartEntity.setDateTo((Date) absRow[1]);
                    absenceChartEntity.setAbsenceDays((Integer) absRow[2]);
                    absenceChartEntity.setAssignmentAbsence(assignmentAbsenceChartEntity);

                    if (assignmentGroupId != null
                            && assignmentGroupId.equals(assignmentAbsenceChartEntity.getAssignmentGroupId())
                            && dateFrom != null
                            && dateFrom.equals(absenceChartEntity.getDateFrom())
                            && dateTo != null
                            && absenceChartEntity.getDateTo() != null
                            && dateTo.getTime() == (absenceChartEntity.getDateTo().getTime() - (23 * 60 + 59) * 60 * 1000)
                            && absenceTypeId != null
                            && absenceTypeId.equals((UUID) absRow[4]))
                        absenceChartEntity.setColor("RED");
                    else
                        absenceChartEntity.setColor("BLUE");
                    absenceChartEntity.setIndex(i++);
                    absenceChartEntity.setAbsenceType((String) absRow[3]);
                    absences.add(absenceChartEntity);
                }
                assignmentAbsenceChartEntity.setAbsences(absences);
                result.add(assignmentAbsenceChartEntity);
            }
        }

        return result;
    }


    private int languageIndex(String language) {
        String langOrder = AppContext.getProperty("base.abstractDictionary.langOrder");

        if (langOrder != null) {
            List<String> langs = Arrays.asList(langOrder.split(";"));
            int count = 1;
            for (String lang : langs) {
                if (language.equals(lang)) {
                    return count;
                }
                count++;
            }
        }
        return 1;
    }

}