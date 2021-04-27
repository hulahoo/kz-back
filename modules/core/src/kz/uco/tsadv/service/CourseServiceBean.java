package kz.uco.tsadv.service;

import com.google.gson.Gson;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.global.*;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.service.NotificationSenderAPIService;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonConfig;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.modules.learning.dictionary.DicCategory;
import kz.uco.tsadv.modules.learning.enums.EnrollmentStatus;
import kz.uco.tsadv.modules.learning.model.*;
import kz.uco.tsadv.modules.performance.model.Trainer;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.pojo.CommentPojo;
import kz.uco.tsadv.pojo.CoursePojo;
import kz.uco.tsadv.pojo.PairPojo;
import kz.uco.tsadv.pojo.ScormInputData;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service(CourseService.NAME)
public class CourseServiceBean implements CourseService {

    @Inject
    protected Persistence persistence;

    @Inject
    protected DataManager dataManager;

    @Inject
    protected Metadata metadata;

    @Inject
    protected Messages messages;

    @Inject
    protected CommonConfig commonConfig;

    @Inject
    protected NotificationSenderAPIService notificationSenderAPIService;

    @Inject
    protected Gson gson;

    @Inject
    private UserSessionSource userSessionSource;

    protected String selectForMethodLoadAssignedTest =
            "SELECT " +
                    "  t.enrollment_id, " +
                    "  t.pg_id, " +
                    "  t.full_name, " +
                    "  t.organization_name_lang1, " +
                    "  t.position_name_lang1, " +
                    "  t.test_id, " +
                    "  t.test_name, " +
                    "  t.enrollment_status, " +
                    "  max(t.csa_success), " +
                    "  t.attempts, " +
                    "  max(t.test_result), " +
                    "  t.course_section_id, " +
                    "  t.course_id, " +
                    "  t.section_name," +
                    "  t.org_id, " +
                    "  t.created_by " +
                    "FROM ( " +
                    "       SELECT " +
                    "         DISTINCT " +
                    "         e.id                  AS                                      enrollment_id, " +
                    "         bp.group_id                                                   pg_id, " +
                    "         concat(bp.first_name , ' ' , bp.last_name , ' ',bp.middle_name) full_name, " +
                    "         org.ORGANIZATION_NAME_LANG1, " +
                    "         pos.position_name_lang1, " +
                    "         t.id                  AS                                      test_id, " +
                    "         t.name                AS                                      test_name, " +
                    "         e.status                                                      enrollment_status, " +
                    "         CASE WHEN csa.success = TRUE " +
                    "           THEN 1 " +
                    "         ELSE 0 END            AS                                      csa_success, " +
                    "         COUNT(csa.id) " +
                    "         OVER ( " +
                    "           PARTITION BY e.id ) AS                                      attempts, " +
                    "         csa.test_result, " +
                    "         cs.id                                                         course_section_id," +
                    "         e.course_id, " +
                    "         cs.section_name, " +
                    "         org.group_id as org_id, " +
                    "         e.created_by  as  created_by" +
                    "       FROM tsadv_enrollment e " +
                    "         JOIN tsadv_course_section cs ON cs.course_id = e.course_id " +
                    "         JOIN tsadv_course_section_object cso ON cso.id = cs.section_object_id " +
                    "         JOIN tsadv_test t ON t.id = cso.test_id " +
                    "         JOIN base_person bp " +
                    "           ON bp.group_id = e.person_group_id AND " +
                    "           CURRENT_DATE BETWEEN bp.start_date AND bp.end_date " +
                    "            and bp.delete_ts is null " +
                    "         LEFT JOIN base_assignment a ON a.person_group_id = e.person_group_id " +
                    "                                   AND CURRENT_DATE BETWEEN a.start_date AND a.end_date " +
                    "                                   AND a.primary_flag = TRUE " +
                    "         LEFT JOIN tsadv_dic_assignment_status das ON das.id = a.assignment_status_id " +
                    "                                                 AND das.code = 'ACTIVE' " +
                    "         LEFT JOIN tsadv_course_section_attempt csa ON csa.enrollment_id = e.id " +
                    "                       and csa.course_section_id = cs.id " +
                    "         LEFT JOIN BASE_ORGANIZATION org " +
                    "           ON org.group_id = a.ORGANIZATION_GROUP_ID AND CURRENT_DATE BETWEEN org.start_date AND org.end_date " +
                    "         LEFT JOIN BASE_POSITION pos " +
                    "           ON pos.group_id = a.POSITION_GROUP_ID AND CURRENT_DATE BETWEEN pos.start_date AND pos.end_date " +
                    " where e.delete_ts is null) t " +
                    " GROUP BY t.enrollment_id, t.pg_id, t.full_name, t.organization_name_lang1, t.position_name_lang1, t.test_id, t.test_name, t.enrollment_status, " +
                    "  t.attempts, t.course_id,t.section_name, t.course_section_id, t.org_id, t.created_by";


    @Override
    public void updateCourseSectionAttempt(CourseSectionAttempt courseSectionAttempt) {
        try (Transaction transaction = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            em.merge(courseSectionAttempt);
            transaction.commit();
        }

        updateEnrollmentStatus(courseSectionAttempt);
    }

    @SuppressWarnings("all")
    @Override
    public void addCourseReview(CourseReview courseReview) {
        try (Transaction transaction = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            em.persist(courseReview);
            em.flush();
            transaction.commit();
        }
    }

    @Override
    public void addLearningPathReview(LearningPathReview learningPathReview) {
        try (Transaction transaction = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            em.persist(learningPathReview);
            em.flush();
            transaction.commit();
        }
    }

    @Override
    public List<UUID> getCategoryHierarchy(String categoryId) {
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createNativeQuery(String.format("WITH RECURSIVE r AS ( " +
                    "  SELECT id " +
                    "  FROM tsadv_dic_category " +
                    "  WHERE id = '%s' " +
                    "  UNION " +
                    "  SELECT tdc.id " +
                    "  FROM tsadv_dic_category tdc " +
                    "    JOIN r " +
                    "      ON tdc.parent_category_id = r.id and tdc.delete_ts is null " +
                    ") " +
                    "SELECT * FROM r", categoryId));
            return query.getResultList();
        }
    }

    @Override
    public int addFavorite(UUID learningPathId, UUID personGroupId) {
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createNativeQuery(
                    "insert into TSADV_PERSON_LEARNING_PATH (ID, LEARNING_PATH_ID, PERSON_GROUP_ID,VERSION) " +
                            "values (?1, ?2, ?3, ?4)");
            query.setParameter(1, UUID.randomUUID());
            query.setParameter(2, learningPathId);
            query.setParameter(3, personGroupId);
            query.setParameter(4, 1);
            int result = query.executeUpdate();
            tx.commit();
            return result;
        }
    }

    @Override
    public int deleteFavorite(UUID learningPathId, UUID personGroupId) {
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createNativeQuery(
                    "delete from TSADV_PERSON_LEARNING_PATH where LEARNING_PATH_ID = ?1 and PERSON_GROUP_ID = ?2");
            query.setParameter(1, learningPathId);
            query.setParameter(2, personGroupId);
            int result = query.executeUpdate();
            tx.commit();
            return result;
        }
    }

    @Override
    public void deleteAllAttempt(Enrollment enrollment) {
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createNativeQuery(
                    "delete from TSADV_COURSE_SECTION_ATTEMPT where ENROLLMENT_ID = ?1 and SUCCESS = ?2");
            query.setParameter(1, enrollment.getId());
            query.setParameter(2, Boolean.FALSE);
            query.executeUpdate();
            tx.commit();
        }
    }

    @Override
    public void sendParametrizedNotification(String notificationCode, TsadvUser user, Map<String, Object> params) {
        notificationSenderAPIService.sendParametrizedNotification(notificationCode, user, params);
    }

    @Override
    public void updateEnrollmentStatus(CourseSectionAttempt courseSectionAttempt) {
        UUID enrollmentId = courseSectionAttempt.getEnrollment().getId();
        float completePercent = completedSectionsPercent(enrollmentId);
        boolean complete = completePercent > 100f; //todo ошибка? больше 100 быть не может...

        if (complete) {
            completeEnrollment(enrollmentId);
        }
    }

    @Override
    public void insertPersonAnswers(List<PersonAnswer> personAnswers) {
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            personAnswers.forEach(em::persist);
            tx.commit();
        }
    }

    @Override
    public float completedSectionsPercent(UUID enrollmentId) {
        List<CourseSection> courseSections = getCourseSections(enrollmentId);
        int completeCount = 0;
        for (CourseSection courseSection : courseSections) {
            if (courseSection.getCourseSectionAttempts() == null || courseSection.getCourseSectionAttempts().isEmpty()) {
                break;
            } else {
                boolean hasSuccessAttempt = courseSection.getCourseSectionAttempts().stream()
                        .anyMatch(courseSectionAttempt -> BooleanUtils.isTrue(courseSectionAttempt.getSuccess())
                                && BooleanUtils.isTrue(courseSectionAttempt.getActiveAttempt())
                                && courseSectionAttempt.getEnrollment() != null
                                && courseSectionAttempt.getEnrollment().getId().equals(enrollmentId));

                if (hasSuccessAttempt) completeCount++;
            }
        }

        int size = courseSections.size();

        if (size == 0) return 0;

        return (float) 100 * (float) completeCount / (float) size;
    }

    @Override
    public List<AssignedTestPojo> loadAssignedTest(int firstResult, int maxResults, boolean forRowCount, int paramsForOrderBy, Map<String, Object> param) {
        List<AssignedTestPojo> list = new ArrayList<>();
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            String stringForQuery = changeSQLQueryWithSecurity(param) + " order by " + paramsForOrderBy;
            Query query = em.createNativeQuery(stringForQuery);
            if (!forRowCount) {
                query.setFirstResult(firstResult);
                query.setMaxResults(maxResults);
            }
            List<Object[]> rows = query.getResultList();

            if (rows != null && !rows.isEmpty()) {
                for (Object[] row : rows) {
                    AssignedTestPojo assignedTest = metadata.create(AssignedTestPojo.class);
                    assignedTest.setEnrollmentId((UUID) row[0]);
                    assignedTest.setPersonGroupId((UUID) row[1]);
                    assignedTest.setPersonFullName((String) row[2]);
                    assignedTest.setOrganization((String) row[3]);
                    assignedTest.setPosition((String) row[4]);
                    assignedTest.setTestId((UUID) row[5]);
                    assignedTest.setTestName((String) row[6]);
                    assignedTest.setEnrollmentStatus(EnrollmentStatus.fromId((Integer) row[7]));
                    assignedTest.setSuccess(((Integer) row[8]) > 0);
                    Object testResult = row[10];
                    if (testResult != null) {
                        assignedTest.setScore(Long.valueOf(testResult.toString()));
                    }

                    assignedTest.setCourseSectionId((UUID) row[11]);
                    assignedTest.setCourseId((UUID) row[12]);
                    assignedTest.setCourseSectionName((String) row[13]);

                    assignedTest.setAttemptsCount(calculateAttemptCount(assignedTest.getCourseSectionId())); //ignoring this row[9]
                    assignedTest.setOrganizationGroupId((UUID) row[14]);
                    assignedTest.setCreatedByLogin((String) row[15]);

                    list.add(assignedTest);
                }
            }
        } finally {
            tx.end();
        }
        return list;
    }

    @Override
    public List<AssignedTestPojo> loadAssignedTest(int firstResult, int maxResults, boolean forRowCount, int paramsForOrderBy, List<Map<String, String>> filter, String lang, Map<String, Object> param) {
        List<AssignedTestPojo> list = new ArrayList<>();
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            String stringForQuery = "";
            CommonService commonService = AppBeans.get(CommonService.class);
            StringBuilder filterStr = new StringBuilder().append(" where 1=1 ");


            if (filter != null && !filter.isEmpty()) {
                List<OrganizationGroupExt> organizationGroupExts = new ArrayList<>();
                for (Map<String, String> stringStringMap : filter) {
                    if (stringStringMap.containsKey("column") &&
                            stringStringMap.get("column").equals(messages.getMessage("kz.uco.tsadv.modules.personal.model", "OrganizationExtWithDaughter"))) {
                        organizationGroupExts = commonService.getEntities(OrganizationGroupExt.class,
                                " select distinct os.organizationGroup from tsadv$OrganizationStructure os " +
                                        " join base$OrganizationExt og " +
                                        " on os.path like concat('%',concat(og.group.id,'%')) " +
                                        " and CURRENT_DATE between og.startDate and og.endDate " +
                                        " where ('ru' = :locale  " +
                                        " and (og.organizationNameLang1 like concat('%',concat(:value,'%')))) " +
                                        " or ('en' = :locale " +
                                        " and (og.organizationNameLang2 like concat('%',concat(:value,'%'))) )" +
                                        " or ('kz' = :locale " +
                                        " and (og.organizationNameLang3 like concat('%',concat(:value,'%')))) ",
                                ParamsMap.of("value", stringStringMap.get("value"),
                                        "locale", lang),
                                View.LOCAL);
                        break;
                    }
                }

                for (Map<String, String> map : filter) {
                    String column = "";
                    if (map.containsKey("column")) column = map.get("column");
                    String operation = "";
                    if (map.containsKey("operation")) operation = map.get("operation");
                    String value = "";
                    if (map.containsKey("value")) value = map.get("value");
                    if (column.equals(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Person.fullName"))) {
                        if (operation.equals(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Op.CONTAINS"))) {
                            filterStr.append(" and ").append(" lower(t.full_name) ").append(" like '%").append(value.toLowerCase()).append("%' ");
                        } else if (operation.equals(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Op.DOES_NOT_CONTAIN"))) {
                            filterStr.append(" and ").append(" lower(t.full_name) ").append(" not like '%").append(value.toLowerCase()).append("%' ");
                        }
                    } else if (column.equals(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Position"))) {
                        if (operation.equals(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Op.CONTAINS"))) {
                            filterStr.append(" and ").append(" lower(t.position_name_lang1) ").append(" like '%").append(value.toLowerCase()).append("%' ");
                        } else if (operation.equals(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Op.DOES_NOT_CONTAIN"))) {
                            filterStr.append(" and ").append(" lower(t.position_name_lang1) ").append(" not like '%").append(value.toLowerCase()).append("%' ");
                        }
                    } else if (column.equals(messages.getMessage("kz.uco.tsadv.modules.personal.model", "OrganizationExt"))) {
                        if (operation.equals(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Op.CONTAINS"))) {
                            filterStr.append(" and ").append(" lower(t.organization_name_lang1) ").append(" like '%").append(value.toLowerCase()).append("%' ");
                        } else if (operation.equals(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Op.DOES_NOT_CONTAIN"))) {
                            filterStr.append(" and ").append(" lower(t.organization_name_lang1) ").append(" not like '%").append(value.toLowerCase()).append("%' ");
                        }
                        break;
                    } else if (column.equals(messages.getMessage("kz.uco.tsadv.modules.personal.model", "OrganizationExtWithDaughter"))) {
                        if (operation.equals(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Op.CONTAINS"))) {
                            StringBuilder orgListId = new StringBuilder().append(" ( ");
                            for (OrganizationGroupExt organizationGroupExt : organizationGroupExts) {
                                orgListId.append("'").append(organizationGroupExt.getId().toString()).append("', ");
                            }
                            orgListId.delete(orgListId.length() - 2, orgListId.length()).append(") ");
                            filterStr.append(" and ").append(" t.org_id in ").append(orgListId.toString());
                        } else if (operation.equals(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Op.DOES_NOT_CONTAIN"))) {
                            StringBuilder orgListId = new StringBuilder().append(" ( ");
                            for (OrganizationGroupExt organizationGroupExt : organizationGroupExts) {
                                orgListId.append("'").append(organizationGroupExt.getId().toString()).append("', ");
                            }
                            orgListId.delete(orgListId.length() - 2, orgListId.length()).append(") ");
                            filterStr.append(" and not (").append(" t.org_id in ").append(orgListId.toString()).append(") ");
                        }
                        break;

                    } else if (column.equals(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Enrollment.status"))) {
                        if (operation.equals(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Op.EQUAL"))) {
                            filterStr.append(" and ").append(EnrollmentStatus.valueOf(value).getId().toString()).append(" = t.enrollment_status ");
                            break;
                        } else if (operation.equals(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Op.NOT_EQUAL"))) {
                            filterStr.append(" and ").append(EnrollmentStatus.valueOf(value).getId().toString()).append(" <> t.enrollment_status ");
                            break;
                        }
                    } else if (column.equals(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Test"))) {
                        if (operation.equals(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Op.CONTAINS"))) {
                            filterStr.append(" and ").append(" lower(t.test_name) ").append(" like '%").append(value.toLowerCase()).append("%' ");
                        } else if (operation.equals(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Op.DOES_NOT_CONTAIN"))) {
                            filterStr.append(" and ").append(" lower(t.test_name) ").append(" not like '%").append(value.toLowerCase()).append("%' ");
                        }
                    }
                }
            }
            String[] selectForMethodLoadAssignedTestArray = changeSQLQueryWithSecurity(param).split("GROUP BY");
            stringForQuery = new StringBuilder()
                    .append(selectForMethodLoadAssignedTestArray[0])
                    .append(filterStr.toString()).append(" GROUP BY ")
                    .append(selectForMethodLoadAssignedTestArray[1])
                    .append(" order by ")
                    .append(paramsForOrderBy).toString();
            Query query = em.createNativeQuery(stringForQuery);

            if (!forRowCount) {
                query.setFirstResult(firstResult);
                query.setMaxResults(maxResults);
            }
            List<Object[]> rows = query.getResultList();

            if (rows != null && !rows.isEmpty()) {
                for (Object[] row : rows) {
                    AssignedTestPojo assignedTest = metadata.create(AssignedTestPojo.class);
                    assignedTest.setEnrollmentId((UUID) row[0]);
                    assignedTest.setPersonGroupId((UUID) row[1]);
                    assignedTest.setPersonFullName((String) row[2]);
                    assignedTest.setOrganization((String) row[3]);
                    assignedTest.setPosition((String) row[4]);
                    assignedTest.setTestId((UUID) row[5]);
                    assignedTest.setTestName((String) row[6]);
                    assignedTest.setEnrollmentStatus(EnrollmentStatus.fromId((Integer) row[7]));
                    assignedTest.setSuccess(((Integer) row[8]) > 0);
                    Object testResult = row[10];
                    if (testResult != null) {
                        assignedTest.setScore(Long.valueOf(testResult.toString()));
                    }

                    assignedTest.setCourseSectionId((UUID) row[11]);
                    assignedTest.setCourseId((UUID) row[12]);
                    assignedTest.setCourseSectionName((String) row[13]);

                    assignedTest.setAttemptsCount(calculateAttemptCount(assignedTest.getCourseSectionId())); //ignoring this row[9]
                    assignedTest.setOrganizationGroupId((UUID) row[14]);
                    assignedTest.setCreatedByLogin((String) row[15]);

                    list.add(assignedTest);
                }
            }
        } finally {
            tx.end();
        }
        return list;
    }

    @Override
    public List<AssignedTestPojo> loadAssignedTest(int firstResult, int maxResults, boolean forRowCount, Map<String, Object> param) {
        List<AssignedTestPojo> list = new ArrayList<>();
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            String stringForQuery = changeSQLQueryWithSecurity(param);
            Query query = em.createNativeQuery(stringForQuery);
            if (!forRowCount) {
                query.setFirstResult(firstResult);
                query.setMaxResults(maxResults);
            }
            List<Object[]> rows = query.getResultList();

            if (rows != null && !rows.isEmpty()) {
                for (Object[] row : rows) {
                    AssignedTestPojo assignedTest = metadata.create(AssignedTestPojo.class);
                    assignedTest.setEnrollmentId((UUID) row[0]);
                    assignedTest.setPersonGroupId((UUID) row[1]);
                    assignedTest.setPersonFullName((String) row[2]);
                    assignedTest.setOrganization((String) row[3]);
                    assignedTest.setPosition((String) row[4]);
                    assignedTest.setTestId((UUID) row[5]);
                    assignedTest.setTestName((String) row[6]);
                    assignedTest.setEnrollmentStatus(EnrollmentStatus.fromId((Integer) row[7]));
                    assignedTest.setSuccess(((Integer) row[8]) > 0);
                    Object testResult = row[10];
                    if (testResult != null) {
                        assignedTest.setScore(Long.valueOf(testResult.toString()));
                    }

                    assignedTest.setCourseSectionId((UUID) row[11]);
                    assignedTest.setCourseId((UUID) row[12]);
                    assignedTest.setCourseSectionName((String) row[13]);
                    assignedTest.setCreatedByLogin((String) row[15]);

                    assignedTest.setAttemptsCount(calculateAttemptCount(assignedTest.getCourseSectionId())); //ignoring this row[9]

                    list.add(assignedTest);
                }
            }
        } finally {
            tx.end();
        }
        return list;
    }

    /**
     * @param paramsForOrderBy column index by which the data is sorted.
     *                         Take value from AssignedTestDatasource. @see AssignedTestDatasource.getEntities.
     */
    @Override
    public List<AssignedTestPojo> loadAssignedTest(int firstResult, int maxResults, boolean forRowCount, int paramsForOrderBy) {
        List<AssignedTestPojo> list = new ArrayList<>();
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            String stringForQuery = selectForMethodLoadAssignedTest + " order by " + paramsForOrderBy;
            Query query = em.createNativeQuery(stringForQuery);
            if (!forRowCount) {
                query.setFirstResult(firstResult);
                query.setMaxResults(maxResults);
            }
            List<Object[]> rows = query.getResultList();

            if (rows != null && !rows.isEmpty()) {
                for (Object[] row : rows) {
                    AssignedTestPojo assignedTest = metadata.create(AssignedTestPojo.class);
                    assignedTest.setEnrollmentId((UUID) row[0]);
                    assignedTest.setPersonGroupId((UUID) row[1]);
                    assignedTest.setPersonFullName((String) row[2]);
                    assignedTest.setOrganization((String) row[3]);
                    assignedTest.setPosition((String) row[4]);
                    assignedTest.setTestId((UUID) row[5]);
                    assignedTest.setTestName((String) row[6]);
                    assignedTest.setEnrollmentStatus(EnrollmentStatus.fromId((Integer) row[7]));
                    assignedTest.setSuccess(((Integer) row[8]) > 0);
                    Object testResult = row[10];
                    if (testResult != null) {
                        assignedTest.setScore(Long.valueOf(testResult.toString()));
                    }

                    assignedTest.setCourseSectionId((UUID) row[11]);
                    assignedTest.setCourseId((UUID) row[12]);
                    assignedTest.setCourseSectionName((String) row[13]);

                    assignedTest.setAttemptsCount(calculateAttemptCount(assignedTest.getCourseSectionId())); //ignoring this row[9]
                    assignedTest.setOrganizationGroupId((UUID) row[14]);
                    assignedTest.setCreatedByLogin((String) row[15]);

                    list.add(assignedTest);
                }
            }
        } finally {
            tx.end();
        }
        return list;
    }

    @Override
    public List<AssignedTestPojo> loadAssignedTest(int firstResult, int maxResults, boolean forRowCount, int paramsForOrderBy, List<Map<String, String>> filter, String lang) {
        List<AssignedTestPojo> list = new ArrayList<>();
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            String stringForQuery = "";
            CommonService commonService = AppBeans.get(CommonService.class);
            StringBuilder filterStr = new StringBuilder().append(" where 1=1 ");


            if (filter != null && !filter.isEmpty()) {
                List<OrganizationGroupExt> organizationGroupExts = new ArrayList<>();
                for (Map<String, String> stringStringMap : filter) {
                    if (stringStringMap.containsKey("column") &&
                            stringStringMap.get("column").equals(messages.getMessage("kz.uco.tsadv.modules.personal.model", "OrganizationExtWithDaughter"))) {
                        organizationGroupExts = commonService.getEntities(OrganizationGroupExt.class,
                                " select distinct os.organizationGroup from tsadv$OrganizationStructure os " +
                                        " join base$OrganizationExt og " +
                                        " on os.path like concat('%',concat(og.group.id,'%')) " +
                                        " and CURRENT_DATE between og.startDate and og.endDate " +
                                        " where ('ru' = :locale  " +
                                        " and (og.organizationNameLang1 like concat('%',concat(:value,'%')))) " +
                                        " or ('en' = :locale " +
                                        " and (og.organizationNameLang2 like concat('%',concat(:value,'%'))) )" +
                                        " or ('kz' = :locale " +
                                        " and (og.organizationNameLang3 like concat('%',concat(:value,'%')))) ",
                                ParamsMap.of("value", stringStringMap.get("value"),
                                        "locale", lang),
                                View.LOCAL);
                        break;
                    }
                }

                for (Map<String, String> map : filter) {
                    String column = "";
                    if (map.containsKey("column")) column = map.get("column");
                    String operation = "";
                    if (map.containsKey("operation")) operation = map.get("operation");
                    String value = "";
                    if (map.containsKey("value")) value = map.get("value");
                    if (column.equals(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Person.fullName"))) {
                        if (operation.equals(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Op.CONTAINS"))) {
                            filterStr.append(" and ").append(" lower(t.full_name) ").append(" like '%").append(value.toLowerCase()).append("%' ");
                        } else if (operation.equals(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Op.DOES_NOT_CONTAIN"))) {
                            filterStr.append(" and ").append(" lower(t.full_name) ").append(" not like '%").append(value.toLowerCase()).append("%' ");
                        }
                    } else if (column.equals(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Position"))) {
                        if (operation.equals(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Op.CONTAINS"))) {
                            filterStr.append(" and ").append(" lower(t.position_name_lang1) ").append(" like '%").append(value.toLowerCase()).append("%' ");
                        } else if (operation.equals(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Op.DOES_NOT_CONTAIN"))) {
                            filterStr.append(" and ").append(" lower(t.position_name_lang1) ").append(" not like '%").append(value.toLowerCase()).append("%' ");
                        }
                    } else if (column.equals(messages.getMessage("kz.uco.tsadv.modules.personal.model", "OrganizationExt"))) {
                        if (operation.equals(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Op.CONTAINS"))) {
                            filterStr.append(" and ").append(" lower(t.organization_name_lang1) ").append(" like '%").append(value.toLowerCase()).append("%' ");
                        } else if (operation.equals(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Op.DOES_NOT_CONTAIN"))) {
                            filterStr.append(" and ").append(" lower(t.organization_name_lang1) ").append(" not like '%").append(value.toLowerCase()).append("%' ");
                        }
                        break;
                    } else if (column.equals(messages.getMessage("kz.uco.tsadv.modules.personal.model", "OrganizationExtWithDaughter"))) {
                        if (operation.equals(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Op.CONTAINS"))) {
                            StringBuilder orgListId = new StringBuilder().append(" ( ");
                            for (OrganizationGroupExt organizationGroupExt : organizationGroupExts) {
                                orgListId.append("'").append(organizationGroupExt.getId().toString()).append("', ");
                            }
                            orgListId.delete(orgListId.length() - 2, orgListId.length()).append(") ");
                            filterStr.append(" and ").append(" t.org_id in ").append(orgListId.toString());
                        } else if (operation.equals(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Op.DOES_NOT_CONTAIN"))) {
                            StringBuilder orgListId = new StringBuilder().append(" ( ");
                            for (OrganizationGroupExt organizationGroupExt : organizationGroupExts) {
                                orgListId.append("'").append(organizationGroupExt.getId().toString()).append("', ");
                            }
                            orgListId.delete(orgListId.length() - 2, orgListId.length()).append(") ");
                            filterStr.append(" and not (").append(" t.org_id in ").append(orgListId.toString()).append(") ");
                        }
                        break;

                    } else if (column.equals(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Enrollment.status"))) {
                        if (operation.equals(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Op.EQUAL"))) {
                            filterStr.append(" and ").append(EnrollmentStatus.valueOf(value).getId().toString()).append(" = t.enrollment_status ");
                            break;
                        } else if (operation.equals(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Op.NOT_EQUAL"))) {
                            filterStr.append(" and ").append(EnrollmentStatus.valueOf(value).getId().toString()).append(" <> t.enrollment_status ");
                            break;
                        }
                    } else if (column.equals(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Test"))) {
                        if (operation.equals(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Op.CONTAINS"))) {
                            filterStr.append(" and ").append(" lower(t.test_name) ").append(" like '%").append(value.toLowerCase()).append("%' ");
                        } else if (operation.equals(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Op.DOES_NOT_CONTAIN"))) {
                            filterStr.append(" and ").append(" lower(t.test_name) ").append(" not like '%").append(value.toLowerCase()).append("%' ");
                        }
                    }
                }
            }
            String[] selectForMethodLoadAssignedTestArray = selectForMethodLoadAssignedTest.split("GROUP BY");
            stringForQuery = new StringBuilder()
                    .append(selectForMethodLoadAssignedTestArray[0])
                    .append(filterStr.toString()).append(" GROUP BY ")
                    .append(selectForMethodLoadAssignedTestArray[1])
                    .append(" order by ")
                    .append(paramsForOrderBy).toString();
            Query query = em.createNativeQuery(stringForQuery);

            if (!forRowCount) {
                query.setFirstResult(firstResult);
                query.setMaxResults(maxResults);
            }
            List<Object[]> rows = query.getResultList();

            if (rows != null && !rows.isEmpty()) {
                for (Object[] row : rows) {
                    AssignedTestPojo assignedTest = metadata.create(AssignedTestPojo.class);
                    assignedTest.setEnrollmentId((UUID) row[0]);
                    assignedTest.setPersonGroupId((UUID) row[1]);
                    assignedTest.setPersonFullName((String) row[2]);
                    assignedTest.setOrganization((String) row[3]);
                    assignedTest.setPosition((String) row[4]);
                    assignedTest.setTestId((UUID) row[5]);
                    assignedTest.setTestName((String) row[6]);
                    assignedTest.setEnrollmentStatus(EnrollmentStatus.fromId((Integer) row[7]));
                    assignedTest.setSuccess(((Integer) row[8]) > 0);
                    Object testResult = row[10];
                    if (testResult != null) {
                        assignedTest.setScore(Long.valueOf(testResult.toString()));
                    }

                    assignedTest.setCourseSectionId((UUID) row[11]);
                    assignedTest.setCourseId((UUID) row[12]);
                    assignedTest.setCourseSectionName((String) row[13]);

                    assignedTest.setAttemptsCount(calculateAttemptCount(assignedTest.getCourseSectionId())); //ignoring this row[9]
                    assignedTest.setOrganizationGroupId((UUID) row[14]);
                    assignedTest.setCreatedByLogin((String) row[15]);

                    list.add(assignedTest);
                }
            }
        } finally {
            tx.end();
        }
        return list;
    }

    protected String changeSQLQueryWithSecurity(Map<String, Object> param) {
        UUID userGroupId = null;
        if (param.containsKey("userGroupId")) {
            userGroupId = (UUID) param.get("userGroupId");
        }
        String disabledSecurityGroupId = commonConfig.getDisabledSecurityGroupId();
        if (disabledSecurityGroupId != null && !disabledSecurityGroupId.isEmpty()
                && userGroupId != null && !userGroupId.toString().equals(disabledSecurityGroupId)) {
            return selectForMethodLoadAssignedTest.replaceFirst("where e.delete_ts is null\\) t",
                    "where e.delete_ts is null) t " +
                            " join  tsadv_security_person_list tspl on tspl.delete_ts is null " +
                            " and t.pg_id = tspl.person_group_id " +
                            " and tspl.security_group_id= '" + userGroupId.toString() + "' ");
        }
        return selectForMethodLoadAssignedTest;
    }

    @Override
    public List<AssignedTestPojo> loadAssignedTest(int firstResult, int maxResults, boolean forRowCount) {
        List<AssignedTestPojo> list = new ArrayList<>();
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            String stringForQuery = selectForMethodLoadAssignedTest;
            Query query = em.createNativeQuery(stringForQuery);
            if (!forRowCount) {
                query.setFirstResult(firstResult);
                query.setMaxResults(maxResults);
            }
            List<Object[]> rows = query.getResultList();

            if (rows != null && !rows.isEmpty()) {
                for (Object[] row : rows) {
                    AssignedTestPojo assignedTest = metadata.create(AssignedTestPojo.class);
                    assignedTest.setEnrollmentId((UUID) row[0]);
                    assignedTest.setPersonGroupId((UUID) row[1]);
                    assignedTest.setPersonFullName((String) row[2]);
                    assignedTest.setOrganization((String) row[3]);
                    assignedTest.setPosition((String) row[4]);
                    assignedTest.setTestId((UUID) row[5]);
                    assignedTest.setTestName((String) row[6]);
                    assignedTest.setEnrollmentStatus(EnrollmentStatus.fromId((Integer) row[7]));
                    assignedTest.setSuccess(((Integer) row[8]) > 0);
                    Object testResult = row[10];
                    if (testResult != null) {
                        assignedTest.setScore(Long.valueOf(testResult.toString()));
                    }

                    assignedTest.setCourseSectionId((UUID) row[11]);
                    assignedTest.setCourseId((UUID) row[12]);
                    assignedTest.setCourseSectionName((String) row[13]);
                    assignedTest.setCreatedByLogin((String) row[15]);

                    assignedTest.setAttemptsCount(calculateAttemptCount(assignedTest.getCourseSectionId())); //ignoring this row[9]

                    list.add(assignedTest);
                }
            }
        } finally {
            tx.end();
        }
        return list;
    }

    protected Long calculateAttemptCount(UUID courseSectionId) {
        if (courseSectionId == null) return 0L;
        return persistence.callInTransaction(em -> (Long) em.createNativeQuery(
                "SELECT count(*) " +
                        "FROM tsadv_course_section_attempt csa " +
                        "WHERE csa.delete_ts IS NULL " +
                        "      AND csa.course_section_id = ?1")
                .setParameter(1, courseSectionId)
                .getSingleResult());
    }

    @Override
    public void addEnrollment(AssignedTestPojo assignedTestPojo) {
        UUID courseId = assignedTestPojo.getCourseId();
        if (courseId == null) {
            throw new NullPointerException("Course ID is null!");
        }

        UUID personGroupId = assignedTestPojo.getPersonGroupId();
        if (personGroupId == null) {
            throw new NullPointerException("PersonGroup ID is null!");
        }

        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            Enrollment enrollment = metadata.create(Enrollment.class);
            enrollment.setCourse(em.getReference(Course.class, courseId));
            enrollment.setStatus(EnrollmentStatus.REQUEST);
            enrollment.setDate(new Date());
            enrollment.setPersonGroup(em.getReference(PersonGroupExt.class, personGroupId));
            em.persist(enrollment);
            tx.commit();
        }
    }

    @Override
    public void removeEnrollment(UUID enrollmentId) {
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            em.remove(em.getReference(Enrollment.class, enrollmentId));
            tx.commit();
        }
    }

    @Override
    public CoursePojo courseInfo(UUID courseId, UUID personGroupId) {
        Course course = persistence.callInTransaction(em -> Objects.requireNonNull(em.find(Course.class, courseId, "portal-course-edit"), "Can not find course by id: " + courseId));
        course.setSections(course.getSections()
                .stream()
                .sorted(Comparator.comparing(CourseSection::getOrder))
                .peek(s -> s.setSession(s.getSession().stream().sorted(Comparator.comparing(CourseSectionSession::getEndDate))
                        .collect(Collectors.toList())))
                .collect(Collectors.toList()));

        CoursePojo coursePojo = new CoursePojo();
        coursePojo.setName(course.getName());
        coursePojo.setAvgRate(course.getAvgRate());
        coursePojo.setFinished((int) course.getEnrollments().stream().filter(e -> e.getStatus().equals(EnrollmentStatus.COMPLETED)).count());
        coursePojo.setIssuedCertificate(course.getIsIssuedCertificate());
        coursePojo.setLearningProof(course.getLearningProof() != null ? course.getLearningProof().getLangValue() : null);

        Double avgRate = course.getReviews().isEmpty()
                ? 0D
                : course.getReviews().stream()
                .map(CourseReview::getRate)
                .reduce(Double::sum)
                .orElseThrow(() -> new NullPointerException("Reviews is empty!")) / course.getReviews().size();
        coursePojo.setAvgRate(avgRate);

        List<CourseSection> courseSections = course.getSections();
        List<CourseSection> courseSectionsWithSessions = courseSections.stream().filter(cs -> !cs.getSession().isEmpty()).collect(Collectors.toList());
        coursePojo.setStartDate(CollectionUtils.isEmpty(courseSectionsWithSessions)
                ? null
                : CollectionUtils.isEmpty(courseSectionsWithSessions.get(0).getSession())
                ? null
                : courseSectionsWithSessions.get(0).getSession().get(0).getStartDate());

        coursePojo.setEndDate(CollectionUtils.isEmpty(courseSectionsWithSessions)
                ? null
                : CollectionUtils.isEmpty(courseSectionsWithSessions.get(courseSectionsWithSessions.size() - 1).getSession())
                ? null
                : courseSectionsWithSessions.get(courseSectionsWithSessions.size() - 1).getSession().get(0).getEndDate());
        coursePojo.setPreRequisitions(course.getPreRequisition().stream().map(pr -> pr.getRequisitionCourse().getName()).collect(Collectors.joining(", ")));
        coursePojo.setTrainers(course.getCourseTrainers().stream().map(ct -> new PairPojo<>(ct.getTrainer().getId(), ct.getTrainer().getTrainerFullName())).collect(Collectors.toList()));
        if (course.getLogo() != null) {
            coursePojo.setLogo(course.getLogo().getId());
        }
        coursePojo.setRateReviewCount(course.getReviews().size());
        coursePojo.setComments(course.getReviews().stream().map(r -> CommentPojo.CommentPojoBuilder.builder()
                .user(r.getPersonGroup().getFullName())
                .date(r.getCreateTs())
                .comment(r.getText())
                .rating(r.getRate())
                .build()).collect(Collectors.toList()));
        coursePojo.setRating(course.getReviews().stream()
                .collect(Collectors.groupingBy(CourseReview::getRate, Collectors.counting()))
                .entrySet().stream()
                .map(m -> new PairPojo<>(m.getKey(), m.getValue()))
                .collect(Collectors.toList()));
        coursePojo.setDescription(course.getDescription());
        coursePojo.setEducationDuration(ObjectUtils.defaultIfNull(course.getEducationDuration(), 0L));
        coursePojo.setEducationPeriod(ObjectUtils.defaultIfNull(course.getEducationPeriod(), 0L));
        coursePojo.setEnrollmentId(course.getEnrollments().stream().filter(e -> e.getPersonGroup().getId().equals(personGroupId)).findFirst()
                .map(e -> e.getId().toString())
                .orElse(null));

        coursePojo.setSelfEnrollment(course.getSelfEnrollment());

        return coursePojo;
    }

    @Override
    public Map<String, Object> courseTrainerInfo(UUID trainerId) {
        Trainer trainer = persistence.callInTransaction(em -> Objects.requireNonNull(em.find(Trainer.class, trainerId, "course-trainer-info"), "Can not find trainer by ID: " + trainerId));

        Map<String, Object> response = new HashMap<>();
        response.put("fullName", trainer.getTrainerFullName());
        response.put("courseCount", trainer.getCourseTrainer().size());
        response.put("finished", trainer.getCourseTrainer().stream().flatMap(ct -> ct.getCourse().getEnrollments().stream()).filter(e -> e.getStatus().equals(EnrollmentStatus.COMPLETED)).count());
        response.put("image", trainer.getEmployee().getPerson().getImage());
        return response;
    }

    @Override
    public List<DicCategory> allCourses() {
        return dataManager.loadList(LoadContext.create(DicCategory.class)
                .setQuery(LoadContext.createQuery("" +
                        "select distinct c " +
                        "   from tsadv$DicCategory c " +
                        "   where c.courses is not empty"))
                .setView("category-courses"))
                .stream()
                .peek(category ->
                        category.getCourses()
                                .forEach(course -> course.setEnrollments(
                                        course.getEnrollments()
                                                .stream()
                                                .filter(e ->
                                                        e.getPersonGroup().equals(userSessionSource.getUserSession().getAttribute(StaticVariable.USER_PERSON_GROUP)))
                                                .collect(Collectors.toList()))))
                .peek(c -> c.setCourses(c.getCourses().stream()
                        .filter(course -> BooleanUtils.isTrue(course.getActiveFlag()))
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }


    @Override
    public List<DicCategory> searchCourses(String courseName) {
        UUID personGroupId = userSessionSource.getUserSession().getAttribute(StaticVariable.USER_PERSON_GROUP);
        return dataManager.loadList(LoadContext.create(DicCategory.class)
                .setQuery(LoadContext.createQuery("" +
                        "select distinct c " +
                        "            from tsadv$DicCategory c " +
                        "            join c.courses cc " +
                        "            where (lower (cc.name) like lower (concat(concat('%', :courseName), '%'))) " +
                        "            and c.courses is not empty")
                        .setParameter("courseName", courseName))
                .setView("category-courses"))
                .stream()
                .peek(c -> c.setCourses(c.getCourses().stream()
                        .peek(course -> course.setEnrollments(course.getEnrollments().stream().filter(e -> e.getPersonGroup().getId().equals(personGroupId)).collect(Collectors.toList())))
                        .filter(course -> course.getName().toLowerCase().contains(courseName.toLowerCase())
                                && BooleanUtils.isTrue(course.getActiveFlag()))
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Override
    public Enrollment courseEnrollmentInfo(UUID enrollmentId) {
        Enrollment enrollment = dataManager.load(Enrollment.class)
                .id(enrollmentId)
                .view("enrollment-course")
                .one();
        enrollment.setCourse(dataManager.reload(enrollment.getCourse(), "course-enrollment"));
        enrollment.getCourse()
                .getSections()
                .forEach(section -> {
                    section.setCourseSectionAttempts(section.getCourseSectionAttempts()
                            .stream()
                            .filter(attempt -> attempt.getEnrollment().getId().equals(enrollment.getId()))
                            .map(attempt -> dataManager.reload(attempt, "course-section-attempt"))
                            .collect(Collectors.toList()));
                });
        return enrollment;
    }

    @Override
    public CourseSection courseSectionWithEnrollmentAttempts(UUID courseSectionId, UUID enrollmentId) {
        return persistence.callInTransaction(em -> {
            CourseSection courseSection = em.find(CourseSection.class, courseSectionId, "course.section.with.format.session");
            courseSection.setCourseSectionAttempts(courseSection.getCourseSectionAttempts().stream().filter(a -> a.getEnrollment().getId().equals(enrollmentId)).collect(Collectors.toList()));
            return courseSection;
        });
    }

    @Override
    public void createScormAttempt(UUID courseSectionId, UUID enrollmentId, List<ScormInputData> inputData, Boolean success) {
        persistence.runInTransaction(em -> {
            Date currentDate = new Date();

            CourseSectionAttempt newAttempt = metadata.create(CourseSectionAttempt.class);
            newAttempt.setCourseSection(dataManager.load(LoadContext.create(CourseSection.class).setId(courseSectionId).setView("courseSection.course.sections")));
            newAttempt.setEnrollment(dataManager.load(LoadContext.create(Enrollment.class).setId(enrollmentId).setView("enrollment.person")));
            newAttempt.setActiveAttempt(false);
            newAttempt.setAttemptDate(currentDate);
            newAttempt.setSuccess(success);
            em.persist(newAttempt);

            inputData.forEach(id -> {
                CourseSectionScormResult scormResult = metadata.create(CourseSectionScormResult.class);
                scormResult.setAnswer(id.getAnswer());
                scormResult.setAnswerTimeStamp(currentDate);
                scormResult.setIsCorrect(false);
                scormResult.setCourseSectionAttempt(newAttempt);
                scormResult.setQuestion(dataManager.load(LoadContext.create(ScormQuestionMapping.class)
                        .setQuery(LoadContext.createQuery("" +
                                "select e " +
                                "from tsadv_ScormQuestionMapping e " +
                                "where e.code = :code")
                                .setParameter("code", id.getFieldId()))));
                scormResult.setScore(id.getScore());
                scormResult.setMaxScore(id.getMaxScore());
                scormResult.setMinScore(id.getMinScore());

                em.persist(scormResult);
            });
        });
    }

    @Override
    public CourseSectionAttempt createTestScormAttempt(@NotNull UUID courseSectionId, @NotNull UUID enrollmentId, @NotNull BigDecimal score, @NotNull BigDecimal minScore, @NotNull BigDecimal maxScore) {
        CourseSectionAttempt newAttempt = metadata.create(CourseSectionAttempt.class);
        CourseSection courseSection = dataManager.load(LoadContext.create(CourseSection.class).setId(courseSectionId).setView("courseSection.with.learningObject"));

        newAttempt.setCourseSection(courseSection);
        newAttempt.setEnrollment(dataManager.load(LoadContext.create(Enrollment.class).setId(enrollmentId).setView(View.MINIMAL)));
        newAttempt.setActiveAttempt(false);
        newAttempt.setAttemptDate(new Date());

        BigDecimal passingScore = courseSection.getSectionObject().getContent().getPassingScore();
        newAttempt.setSuccess(passingScore == null || passingScore.compareTo(score) <= 0);
        newAttempt.setTestResult(score);
        newAttempt.setTestResultPercent(score.subtract(minScore).multiply(BigDecimal.valueOf(100).divide(maxScore.subtract(minScore), 2, RoundingMode.DOWN)));
        dataManager.commit(newAttempt);

        return dataManager.reload(newAttempt, View.LOCAL);
    }

    @Override
    public PairPojo<Boolean, String> validateEnroll(UUID courseId, String locale) {
        List<Object[]> result = persistence.callInTransaction(em -> em.createNativeQuery("" +
                "SELECT c.id, " +
                "       prc.name           AS course_name, " +
                "       e.id           AS enrollment " +
                "FROM tsadv_course c " +
                "         JOIN tsadv_course_pre_requisition pr " +
                "              ON pr.course_id = c.id " +
                "                  AND pr.delete_ts IS NULL " +
                "         JOIN tsadv_course prc " +
                "              ON pr.requisition_course_id = prc.id " +
                "                  AND prc.active_flag " +
                "         LEFT JOIN tsadv_enrollment e " +
                "                   ON prc.id = e.course_id " +
                "                       AND e.delete_ts IS NULL " +
                "                       AND e.person_group_id = ?2 " +
                "WHERE c.id = ?1" +
                "   AND ((NOT e.status = 5) OR (e.id IS NULL));")
                .setParameter(1, courseId)
                .setParameter(2, ((UUID) userSessionSource.getUserSession().getAttribute(StaticVariable.USER_PERSON_GROUP_ID)))
                .getResultList());
        PairPojo<Boolean, String> response = new PairPojo<>(true, null);
        if (result.size() != 0) {
            String notFininshedCourses = result.stream()
                    .map(row -> (String) row[1])
                    .collect(Collectors.joining(", "));
            return new PairPojo<>(false, messages.getMessage("kz.uco.tsadv.service", "course.enroll.error.null", new Locale(locale)) + " " + notFininshedCourses);
        }

        return response;
    }


    protected void completeEnrollment(UUID enrollmentId) {
        try (Transaction transaction = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();

            Query query = em.createQuery(
                    "UPDATE tsadv$Enrollment c SET c.status = :status where c.id = :id");
            query.setParameter("status", EnrollmentStatus.COMPLETED);
            query.setParameter("id", enrollmentId);
            query.executeUpdate();
            transaction.commit();
        }
    }

    protected List<CourseSection> getCourseSections(UUID enrollmentId) {
        LoadContext<CourseSection> loadContext = LoadContext.create(CourseSection.class);
        LoadContext.Query query = LoadContext.createQuery("select distinct e from tsadv$CourseSection e" +
                " where e.course.id = (select en.course.id from tsadv$Enrollment en " +
                "                       where en.id = :eId) ");
        query.setParameter("eId", enrollmentId);
        loadContext.setQuery(query);
        loadContext.setView("courseSection.for.status");
        return dataManager.loadList(loadContext);
    }
}